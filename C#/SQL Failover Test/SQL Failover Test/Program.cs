using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Data.SqlClient;

namespace SQL_Failover_Test
{
    class Program //:: StateChangeEventHandler
    {
        private SqlConnection dbConnection;
        private int failCount = 0;

        static void Main(string[] args)
        {
            Program p = new Program();
            p.Test();
            
        }

        private void Test()
        {
            int a = 0;
            int b = 0;

            dbConnection = new SqlConnection("Server=SQL;Database=C_Sharp_Database;Trusted_Connection=true");
            dbConnection.InfoMessage += new SqlInfoMessageEventHandler(OnInfoMessage);
            dbConnection.Open();
            Failover("Connection Open");

            SqlCommand command = new SqlCommand("SELECT * FROM dbo.TestTable", dbConnection);

            Failover("SQL Command Created");
            using (SqlDataReader reader = command.ExecuteReader())
            {
                Console.WriteLine("PK\tFibonacci");
                while (reader.Read())
                {
                    b = a;
                    a= (int) reader[1];
                    Console.WriteLine(String.Format("{0} \t | {1}",
                        reader[0], a));
                    //Failover("Reading Records");
                }
            }

            SqlCommand insertCommand = new SqlCommand("INSERT INTO dbo.TestTable (Fibonacci) VALUES (@0)", dbConnection);
            insertCommand.Parameters.Add(new SqlParameter("0", a + b));
            Failover("Execute Insert");
            insertCommand.ExecuteNonQuery();

            dbConnection.Close();

            Failover("Close");

        }

        private void Failover(String location)
        {
            failCount++;
            Console.WriteLine(location + " Failover ---------------------- " + failCount.ToString());
            Console.ReadLine();
        }

        protected static void OnInfoMessage(object sender, SqlInfoMessageEventArgs args)
        {
            Console.WriteLine("SQL Info Message");

            foreach (SqlError err in args.Errors)
            {
                Console.WriteLine("The {0} has received a severity {1}, state {2} error number {3}\n" +
                                  "on line {4} of procedure {5} on server {6}:\n{7}",
                                  err.Source, err.Class, err.State, err.Number, err.LineNumber,
                                  err.Procedure, err.Server, err.Message);
            }
        }

    }
}

import java.sql.*;

public class OldSQLClient {
	private static final String CONNECTION_STRING = "jdbc:sqlserver://SQL;DatabaseName=Java_Database;integratedSecurity=true;";
	
	private Connection dbConnection;
	private Statement sqlStatement;
	private ResultSet recordSet;

	public static void main(String[] args) {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			OldSQLClient s = new OldSQLClient();
			s.dbtest();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void dbtest() {
		int a = 0;
		int b = 0;
		try {
			dbConnection = DriverManager.getConnection(CONNECTION_STRING);
			System.out.println("Failover ----------------------");
			if (dbConnection.isClosed() == true || dbConnection.isValid(60) == false )
				dbConnection = DriverManager.getConnection(CONNECTION_STRING);
			
			String query = "SELECT * FROM dbo.TestTable";
			sqlStatement = dbConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			System.out.println("Failover ----------------------");
			if (dbConnection.isClosed() == true || dbConnection.isValid(60) == false ){
				dbConnection = DriverManager.getConnection(CONNECTION_STRING);
				sqlStatement = dbConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			}
			recordSet = sqlStatement.executeQuery(query);
			System.out.println("Failover ----------------------");
			while (recordSet.next()) {
				a = b;
				b = recordSet.getInt("Fibonacci");
				System.out.println(recordSet.getInt("TestPK") + ", " + b);
				System.out.println("Failover ----------------------");
			}
			
			System.out.println("Failover ----------------------");
			recordSet.moveToInsertRow();
			System.out.println("Failover ----------------------");
			recordSet.updateInt("Fibonacci", a+b);
			System.out.println("Failover ----------------------");
			recordSet.insertRow();
			
			System.out.println("Failover ----------------------");
			recordSet.close();
			
			sqlStatement.close();
			dbConnection.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		
	}
}

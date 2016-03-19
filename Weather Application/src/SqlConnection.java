import java.sql.*;
import javax.swing.*;
public class SqlConnection 
{
	Connection con = null;
	public static Connection dbConnector()
	{
		try
		{
			Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;database=WeatherData", "user", "password");
			JOptionPane.showMessageDialog(null, "SQL Connection successful.");
			return con;
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, "Sorry the SQL Connection was unsuccessful.");
			return null;
		}
	}
}

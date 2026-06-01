package petadoption;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection connect() {
        try {

            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/databasename", "root", "1234");
                //replace databasename

            System.out.println("Connected successfully!");

            return conn;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
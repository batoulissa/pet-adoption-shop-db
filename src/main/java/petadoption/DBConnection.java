package petadoption;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static void connect() {
        try {
            String url = "jdbc:mysql://localhost:3306/sys";
            String user = "root";
            String password = "1234";

            Connection conn = DriverManager.getConnection(url, user, password);

            System.out.println("Connected successfully!");

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
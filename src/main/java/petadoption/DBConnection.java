package petadoption;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection connect() {
        try {
            //make sure to enter your own database URL, username and password here
            String url = "jdbc:mysql://localhost:3306/pet_adoption_db";
            String user = "root";
            String password = "Bukitmas@04";

            Connection conn = DriverManager.getConnection(url, user, password);

            System.out.println("Connected successfully!");

            return conn;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
package petadoption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TablePrinterTest {
    public static void printCatsByBreed() {
        String breed = InputHelper.getString("Enter breed: ");

        String sql =
            "SELECT cat_id, cat_name, age_months " +
            "FROM cat " +
            "WHERE breed = ?";

        try {
            Connection conn = DBConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, breed);

            ResultSet rs = pstmt.executeQuery();
            TablePrinter.print(rs);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}


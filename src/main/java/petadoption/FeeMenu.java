package petadoption;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FeeMenu {
    private final Connection conn;
    private final Scanner scanner;

    public FeeMenu(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void updateAdoptionFee() {
        try {
            System.out.print("Cat type to update (kitten/adult/senior/special_needs): ");
            String catType = scanner.nextLine();

            System.out.print("New adoption fee: ");
            BigDecimal newFee = new BigDecimal(scanner.nextLine());

            System.out.print("Changed by: ");
            String changedBy = scanner.nextLine();

            System.out.print("Change reason: ");
            String reason = scanner.nextLine();

            String closeOldFeeSql = """
                    UPDATE fee_schedule
                    SET effective_to = CURDATE()
                    WHERE cat_type = ?
                    AND effective_to IS NULL
                    """;

            String insertNewFeeSql = """
                    INSERT INTO fee_schedule
                    (cat_type, unit_price, effective_from, effective_to, changed_by, change_reason)
                    VALUES (?, ?, CURDATE(), NULL, ?, ?)
                    """;

            conn.setAutoCommit(false);

            try {
                PreparedStatement closeStmt = conn.prepareStatement(closeOldFeeSql);
                closeStmt.setString(1, catType);
                closeStmt.executeUpdate();

                PreparedStatement insertStmt = conn.prepareStatement(insertNewFeeSql);
                insertStmt.setString(1, catType);
                insertStmt.setBigDecimal(2, newFee);
                insertStmt.setString(3, changedBy);
                insertStmt.setString(4, reason);
                insertStmt.executeUpdate();

                conn.commit();

                closeStmt.close();
                insertStmt.close();

                System.out.println("Adoption fee updated successfully.");
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Fee update failed. Transaction rolled back: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Database error while updating fee: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid fee amount.");
        }
    }

    public void priceChangeAnalysis() {
        try {
            System.out.print("Cat type to analyze (kitten/adult/senior/special_needs): ");
            String catType = scanner.nextLine();

            String sql = """
                    SELECT
                        f.cat_type,
                        f.unit_price,
                        f.effective_from,
                        f.effective_to,
                        COUNT(i.basket_item_id) AS adoption_count,
                        COALESCE(SUM(i.quantity * i.unit_price_at_sale), 0) AS collected_fee
                    FROM fee_schedule f
                    LEFT JOIN adoption_basket_items i ON f.fee_id = i.fee_id
                    WHERE f.cat_type = ?
                    GROUP BY f.fee_id, f.cat_type, f.unit_price, f.effective_from, f.effective_to
                    ORDER BY f.effective_from
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, catType);

            ResultSet rs = pstmt.executeQuery();

            System.out.printf("%-15s %-10s %-15s %-15s %-15s %-15s%n",
                    "Cat Type", "Fee", "From", "To", "Adoptions", "Collected");
            System.out.println("-------------------------------------------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-15s %-10.2f %-15s %-15s %-15d %-15.2f%n",
                        rs.getString("cat_type"),
                        rs.getDouble("unit_price"),
                        rs.getDate("effective_from"),
                        rs.getDate("effective_to") == null ? "CURRENT" : rs.getDate("effective_to").toString(),
                        rs.getInt("adoption_count"),
                        rs.getDouble("collected_fee"));
            }

            if (!found) {
                System.out.println("No fee history found for this cat type.");
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Database error while analyzing price changes: " + e.getMessage());
        }
    }
}
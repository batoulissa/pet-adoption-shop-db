package petadoption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class CatMenu {
    private final Connection conn;
    private final Scanner scanner;

    public CatMenu(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void addCat() {
        try {
            System.out.print("Cat name: ");
            String catName = scanner.nextLine();

            System.out.print("Breed: ");
            String breed = scanner.nextLine();

            System.out.print("Age in months: ");
            int ageMonths = Integer.parseInt(scanner.nextLine());

            System.out.print("Cat type (kitten/adult/senior/special_needs): ");
            String catType = scanner.nextLine();

            System.out.print("Gender (male/female): ");
            String gender = scanner.nextLine();

            System.out.print("Color: ");
            String color = scanner.nextLine();

            System.out.print("Description: ");
            String description = scanner.nextLine();

            String sql = """
                    INSERT INTO cat
                    (cat_name, breed, age_months, cat_type, gender, color, description, status, intake_date)
                    VALUES (?, ?, ?, ?, ?, ?, ?, 'available', CURDATE())
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, catName);
            pstmt.setString(2, breed);
            pstmt.setInt(3, ageMonths);
            pstmt.setString(4, catType);
            pstmt.setString(5, gender);
            pstmt.setString(6, color);
            pstmt.setString(7, description);

            int rows = pstmt.executeUpdate();
            System.out.println(rows + " cat inserted.");

            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Database error while adding cat: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Cat was not inserted.");
        }
    }

    public void searchAvailableCatsByType() {
        try {
            System.out.print("Cat type to search (kitten/adult/senior/special_needs): ");
            String catType = scanner.nextLine();

            String sql = """
                    SELECT cat_id, cat_name, breed, age_months, cat_type,
                           gender, color, current_adoption_fee, medical_visits
                    FROM v_available_cats
                    WHERE cat_type = ?
                    ORDER BY cat_name
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, catType);

            ResultSet rs = pstmt.executeQuery();

            System.out.printf("%-5s %-15s %-22s %-8s %-15s %-8s %-12s %-10s %-10s%n",
                    "ID", "Name", "Breed", "Age", "Type", "Gender", "Color", "Fee", "Medical");
            System.out.println("------------------------------------------------------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-5d %-15s %-22s %-8d %-15s %-8s %-12s %-10.2f %-10d%n",
                        rs.getInt("cat_id"),
                        rs.getString("cat_name"),
                        rs.getString("breed"),
                        rs.getInt("age_months"),
                        rs.getString("cat_type"),
                        rs.getString("gender"),
                        rs.getString("color"),
                        rs.getDouble("current_adoption_fee"),
                        rs.getInt("medical_visits"));
            }

            if (!found) {
                System.out.println("No available cats found for this type.");
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Database error while searching cats: " + e.getMessage());
        }
    }

    public void deleteAvailableCat() {
        try {
            System.out.print("Cat ID to delete: ");
            int catId = Integer.parseInt(scanner.nextLine());

            String sql = """
                    DELETE FROM cat
                    WHERE cat_id = ?
                    AND status = 'available'
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, catId);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Available cat deleted.");
            } else {
                System.out.println("No cat deleted. The cat may not exist or may not be available.");
            }

            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Database error while deleting cat: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid cat ID.");
        }
    }
}
package petadoption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import petadoption.TablePrinter;

public class ReportMenu {
    private static final String RESET  = "\u001B[0m";
    private static final String BOLD   = "\u001B[1m";
    private static final String CYAN   = "\u001B[36m";
    private static final String GREEN  = "\u001B[32m";
    private static final String RED    = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String WHITE  = "\u001B[37m";
    private final Connection conn;
    private final Scanner scanner;

    public ReportMenu(Connection conn, Scanner scanner) {
        this.conn = conn;
        this.scanner = scanner;
    }

    public void showMenu() {
        while (true) {
            System.out.println();
            System.out.println(CYAN + BOLD + "╔══════════════════════════════════════════════╗" + RESET);
            System.out.println(CYAN + BOLD + "║               📊 Report Menu                 ║" + RESET);
            System.out.println(CYAN + BOLD + "╚══════════════════════════════════════════════╝" + RESET);
            System.out.println();
            System.out.println("  " + CYAN + BOLD + "[1]" + RESET + WHITE + " Worker salary report by role"              + RESET);
            System.out.println("  " + CYAN + BOLD + "[2]" + RESET + WHITE + " Worker count and avg salary by city"       + RESET);
            System.out.println("  " + CYAN + BOLD + "[3]" + RESET + WHITE + " Adoption summary by shelter city"          + RESET);
            System.out.println("  " + CYAN + BOLD + "[4]" + RESET + WHITE + " Adoption fee change analysis by cat type"  + RESET);
            System.out.println("  " + CYAN + BOLD + "[5]" + RESET + WHITE + " Adopter demographic change sales analysis" + RESET);
            System.out.println("  " + RED  + BOLD + "[0]" + RESET + RED   + " Back"                                      + RESET);
            System.out.println();

            int choice = readInt(YELLOW + "  ▶ Choose: " + RESET);

            switch (choice) {
                case 1 -> workerSalaryReportByRole();
                case 2 -> workerCountAndSalaryByCity();
                case 3 -> adoptionSummaryByShelterCity();
                case 4 -> feeChangeAnalysisByCatType();
                case 5 -> adopterDemographicSalesAnalysis();
                case 0 -> {
                    return;
                }
                default -> System.out.println(RED + "  ✘ Invalid choice." + RESET);
            }
        }
    }

    private void workerSalaryReportByRole() {
        try {
            System.out.print("Role to search (volunteer/vet/coordinator/admin/caretaker): ");
            String role = scanner.nextLine();

            String sql = """
                    SELECT worker_id, first_name, last_name, role, employment_type,
                           salary, hire_date, shelter_name, shelter_city
                    FROM v_worker_salary
                    WHERE role = ?
                    ORDER BY salary DESC
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, role);

            ResultSet rs = pstmt.executeQuery();
            TablePrinter.print(rs);

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Database error while creating worker salary report: " + e.getMessage());
        }
    }

    private void workerCountAndSalaryByCity() {
        try {
            System.out.print("Shelter city to search: ");
            String city = scanner.nextLine();

            String sql = """
                    SELECT s.city,
                           s.shelter_name,
                           w.role,
                           COUNT(w.worker_id) AS worker_count,
                           ROUND(AVG(w.salary), 2) AS average_salary
                    FROM workers w
                    LEFT JOIN shelter s ON w.shelter_id = s.shelter_id
                    WHERE s.city = ?
                    GROUP BY s.city, s.shelter_name, w.role
                    ORDER BY s.shelter_name, w.role
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, city);

            ResultSet rs = pstmt.executeQuery();
            TablePrinter.print(rs);

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Database error while creating worker count report: " + e.getMessage());
        }
    }

    private void adoptionSummaryByShelterCity() {
        try {
            System.out.print("Shelter city to search: ");
            String city = scanner.nextLine();

            String sql = """
                    SELECT s.city,
                           s.shelter_name,
                           c.cat_type,
                           COUNT(i.basket_item_id) AS adoption_count,
                           COALESCE(SUM(i.quantity * i.unit_price_at_sale), 0) AS total_adoption_fees
                    FROM adoption_transaction t
                    JOIN shelter s ON t.shelter_id = s.shelter_id
                    JOIN adoption_basket_items i ON t.transaction_id = i.transaction_id
                    JOIN cat c ON i.cat_id = c.cat_id
                    WHERE s.city = ?
                    GROUP BY s.city, s.shelter_name, c.cat_type
                    ORDER BY s.shelter_name, c.cat_type
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, city);

            ResultSet rs = pstmt.executeQuery();
            TablePrinter.print(rs);

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Database error while creating adoption summary: " + e.getMessage());
        }
    }

    private void feeChangeAnalysisByCatType() {
        try {
            System.out.print("Cat type to analyze (kitten/adult/senior/special_needs): ");
            String catType = scanner.nextLine();

            String sql = """
                    SELECT f.cat_type,
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
            TablePrinter.print(rs);

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Database error while creating fee change analysis: " + e.getMessage());
        }
    }

    private void adopterDemographicSalesAnalysis() {
        try {
            int adopterId = readInt("Adopter ID to analyze: ");

            String sql = """
                    SELECT a.adopter_id,
                           CONCAT(a.first_name, ' ', a.last_name) AS adopter_name,
                           h.history_id,
                           h.snapshot_date,
                           h.city,
                           h.address,
                           h.age,
                           h.change_reason,
                           COUNT(i.basket_item_id) AS adoption_count,
                           COALESCE(SUM(i.quantity * i.unit_price_at_sale), 0) AS total_adoption_fees
                    FROM adopter a
                    JOIN adopter_history h ON a.adopter_id = h.adopter_id
                    LEFT JOIN adoption_transaction t ON t.adopter_history_id = h.history_id
                    LEFT JOIN adoption_basket_items i ON t.transaction_id = i.transaction_id
                    WHERE a.adopter_id = ?
                    GROUP BY a.adopter_id, a.first_name, a.last_name,
                             h.history_id, h.snapshot_date, h.city, h.address,
                             h.age, h.change_reason
                    ORDER BY h.snapshot_date
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, adopterId);

            ResultSet rs = pstmt.executeQuery();
            TablePrinter.print(rs);

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Database error while creating demographic sales analysis: " + e.getMessage());
        }
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
    }
}
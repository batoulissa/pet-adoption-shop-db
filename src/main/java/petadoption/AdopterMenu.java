package petadoption;

import java.sql.*;

public class AdopterMenu {

    // =========================================================
    // MAIN MENU
    // =========================================================
    public static void menu() {
        while (true) {
            System.out.println("\n========== CUSTOMER MENU ==========");
            System.out.println(" 1. View all adopters");
            System.out.println(" 2. Search adopter by name");
            System.out.println(" 3. View adopter details (with adoption history)");
            System.out.println(" 4. Add new adopter");
            System.out.println(" 5. Update adopter info (records demographic snapshot)");
            System.out.println(" 6. Delete adopter");
            System.out.println(" 7. View adopter demographic history");
            System.out.println(" 8. Compare sales BEFORE vs AFTER demographic change");
            System.out.println(" 9. Sales breakdown by city across all adopters");
            System.out.println(" 0. Back to main menu");
            System.out.println("====================================");

            int choice = InputHelper.getMenuChoice("Enter choice: ", 0, 9);
            switch (choice) {
                case 1 -> viewAllAdopters();
                case 2 -> searchAdopterByName();
                case 3 -> viewAdopterDetails();
                case 4 -> addAdopter();
                case 5 -> updateAdopter();
                case 6 -> deleteAdopter();
                case 7 -> viewDemographicHistory();
                case 8 -> compareSalesBeforeAfterChange();
                case 9 -> salesBreakdownByCity();
                case 0 -> { return; }
            }
        }
    }

    // =========================================================
    // 1. VIEW ALL ADOPTERS
    // =========================================================
    private static void viewAllAdopters() {
        String sql = """
                SELECT adopter_id, first_name, last_name, email, phone,
                       city, birth_year, age, created_at
                FROM adopter
                ORDER BY last_name, first_name
                """;
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- All Adopters ---");
            TablePrinter.print(rs);

        } catch (SQLException e) {
            System.out.println("Error fetching adopters: " + e.getMessage());
        }
    }

    // =========================================================
    // 2. SEARCH ADOPTER BY NAME
    // =========================================================
    private static void searchAdopterByName() {
        String name = InputHelper.getString("Enter first or last name to search: ");
        String sql = """
                SELECT adopter_id, first_name, last_name, email, phone, city, age
                FROM adopter
                WHERE first_name LIKE ? OR last_name LIKE ?
                ORDER BY last_name
                """;
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            ps.setString(2, "%" + name + "%");
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- Search Results ---");
            TablePrinter.print(rs);

        } catch (SQLException e) {
            System.out.println("Error searching adopters: " + e.getMessage());
        }
    }

    // =========================================================
    // 3. VIEW ADOPTER DETAILS WITH ADOPTION HISTORY
    // =========================================================
    private static void viewAdopterDetails() {
        int adopterId = InputHelper.getInt("Enter adopter ID: ");

        // Basic info
        String infoSql = """
                SELECT adopter_id, first_name, last_name, email, phone,
                       city, address, birth_year, age, created_at
                FROM adopter
                WHERE adopter_id = ?
                """;

        // Adoption history with total fees
        String histSql = """
                SELECT
                    at2.transaction_id,
                    at2.transaction_timestamp,
                    s.shelter_name,
                    GROUP_CONCAT(c.cat_name ORDER BY c.cat_name SEPARATOR ', ') AS cats_adopted,
                    taf.total_amount
                FROM adoption_transaction at2
                JOIN shelter s ON at2.shelter_id = s.shelter_id
                JOIN adoption_basket_items abi ON at2.transaction_id = abi.transaction_id
                JOIN cat c ON abi.cat_id = c.cat_id
                JOIN total_adoption_fees taf ON at2.transaction_id = taf.transaction_id
                WHERE at2.adopter_id = ?
                GROUP BY at2.transaction_id, at2.transaction_timestamp,
                         s.shelter_name, taf.total_amount
                ORDER BY at2.transaction_timestamp
                """;

        try (Connection conn = DBConnection.connect()) {
            // Print adopter info
            try (PreparedStatement ps = conn.prepareStatement(infoSql)) {
                ps.setInt(1, adopterId);
                ResultSet rs = ps.executeQuery();
                System.out.println("\n--- Adopter Info ---");
                TablePrinter.print(rs);
            }

            // Print adoption history
            try (PreparedStatement ps = conn.prepareStatement(histSql)) {
                ps.setInt(1, adopterId);
                ResultSet rs = ps.executeQuery();
                System.out.println("\n--- Adoption History ---");
                TablePrinter.print(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching adopter details: " + e.getMessage());
        }
    }

    // =========================================================
    // 4. ADD NEW ADOPTER
    // =========================================================
    private static void addAdopter() {
        System.out.println("\n--- Add New Adopter ---");
        String firstName = InputHelper.getString("First name: ");
        String lastName  = InputHelper.getString("Last name: ");
        String email     = InputHelper.getString("Email: ");
        String phone     = InputHelper.getString("Phone: ");
        String city      = InputHelper.getString("City: ");
        String address   = InputHelper.getString("Address: ");
        int birthYear    = InputHelper.getInt("Birth year: ");
        int age          = InputHelper.getInt("Age: ");

        String sql = """
                INSERT INTO adopter (first_name, last_name, email, phone,
                                     city, address, birth_year, age)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, city);
            ps.setString(6, address);
            ps.setInt(7, birthYear);
            ps.setInt(8, age);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int newId = keys.getInt(1);
                System.out.println("Adopter added successfully! ID: " + newId);

                // Record initial snapshot in adopter_history (REQ14)
                insertAdopterSnapshot(conn, newId, city, address, birthYear, age,
                        "Initial registration");
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Error: Email already exists.");
        } catch (SQLException e) {
            System.out.println("Error adding adopter: " + e.getMessage());
        }
    }

    // =========================================================
    // 5. UPDATE ADOPTER INFO (records demographic snapshot - REQ14)
    // =========================================================
    private static void updateAdopter() {
        int adopterId = InputHelper.getInt("Enter adopter ID to update: ");

        // Fetch current values first
        String fetchSql = "SELECT * FROM adopter WHERE adopter_id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement fetch = conn.prepareStatement(fetchSql)) {

            fetch.setInt(1, adopterId);
            ResultSet rs = fetch.executeQuery();

            if (!rs.next()) {
                System.out.println("Adopter not found.");
                return;
            }

            // Show current info
            System.out.println("\nCurrent info:");
            System.out.println("  Name      : " + rs.getString("first_name") + " " + rs.getString("last_name"));
            System.out.println("  Email     : " + rs.getString("email"));
            System.out.println("  Phone     : " + rs.getString("phone"));
            System.out.println("  City      : " + rs.getString("city"));
            System.out.println("  Address   : " + rs.getString("address"));
            System.out.println("  Birth year: " + rs.getInt("birth_year"));
            System.out.println("  Age       : " + rs.getInt("age"));

            String oldCity    = rs.getString("city");
            String oldAddress = rs.getString("address");
            int    oldBYear   = rs.getInt("birth_year");
            int    oldAge     = rs.getInt("age");

            System.out.println("\nEnter new values (press Enter to keep current):");
            String newEmail   = InputHelper.getString("Email [" + rs.getString("email") + "]: ");
            String newPhone   = InputHelper.getString("Phone [" + rs.getString("phone") + "]: ");
            String newCity    = InputHelper.getString("City [" + oldCity + "]: ");
            String newAddress = InputHelper.getString("Address [" + oldAddress + "]: ");
            String newByearStr= InputHelper.getString("Birth year [" + oldBYear + "]: ");
            String newAgeStr  = InputHelper.getString("Age [" + oldAge + "]: ");

            // Apply defaults if blank
            if (newEmail.isBlank())   newEmail   = rs.getString("email");
            if (newPhone.isBlank())   newPhone   = rs.getString("phone");
            if (newCity.isBlank())    newCity    = oldCity;
            if (newAddress.isBlank()) newAddress = oldAddress;
            int newBYear = newByearStr.isBlank() ? oldBYear : Integer.parseInt(newByearStr);
            int newAge   = newAgeStr.isBlank()   ? oldAge   : Integer.parseInt(newAgeStr);

            // Check if demographics changed
            boolean demographicChanged = !newCity.equals(oldCity)
                    || !newAddress.equals(oldAddress)
                    || newBYear != oldBYear
                    || newAge != oldAge;

            // Snapshot BEFORE update if demographics changed (REQ14)
            if (demographicChanged) {
                String reason = InputHelper.getString("Reason for demographic change: ");
                insertAdopterSnapshot(conn, adopterId, oldCity, oldAddress, oldBYear, oldAge,
                        "Before change: " + reason);
            }

            String updateSql = """
                    UPDATE adopter
                    SET email = ?, phone = ?, city = ?, address = ?,
                        birth_year = ?, age = ?
                    WHERE adopter_id = ?
                    """;

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, newEmail);
                ps.setString(2, newPhone);
                ps.setString(3, newCity);
                ps.setString(4, newAddress);
                ps.setInt(5, newBYear);
                ps.setInt(6, newAge);
                ps.setInt(7, adopterId);
                ps.executeUpdate();
            }

            // Snapshot AFTER update if demographics changed (REQ14)
            if (demographicChanged) {
                insertAdopterSnapshot(conn, adopterId, newCity, newAddress, newBYear, newAge,
                        "Current profile after relocation");
                System.out.println("Demographic change recorded in history.");
            }

            System.out.println("Adopter updated successfully.");

        } catch (SQLException e) {
            System.out.println("Error updating adopter: " + e.getMessage());
        }
    }

    // =========================================================
    // 6. DELETE ADOPTER
    // =========================================================
    private static void deleteAdopter() {
        int adopterId = InputHelper.getInt("Enter adopter ID to delete: ");

        // Check if adopter has transactions
        String checkSql = "SELECT COUNT(*) FROM adoption_transaction WHERE adopter_id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement check = conn.prepareStatement(checkSql)) {

            check.setInt(1, adopterId);
            ResultSet rs = check.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                System.out.println("Cannot delete: adopter has existing adoption transactions.");
                return;
            }

            if (!InputHelper.getBool("Are you sure you want to delete adopter " + adopterId + "?")) {
                System.out.println("Cancelled.");
                return;
            }

            String sql = "DELETE FROM adopter WHERE adopter_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, adopterId);
                int rows = ps.executeUpdate();
                if (rows > 0) System.out.println("Adopter deleted.");
                else          System.out.println("Adopter not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting adopter: " + e.getMessage());
        }
    }

    // =========================================================
    // 7. [REQ14] VIEW ADOPTER DEMOGRAPHIC HISTORY
    // =========================================================
    private static void viewDemographicHistory() {
        int adopterId = InputHelper.getInt("Enter adopter ID: ");

        String nameSql = "SELECT first_name, last_name FROM adopter WHERE adopter_id = ?";
        String histSql = """
                SELECT
                    ah.history_id,
                    ah.snapshot_date,
                    ah.city,
                    ah.address,
                    ah.birth_year,
                    ah.age,
                    ah.change_reason
                FROM adopter_history ah
                WHERE ah.adopter_id = ?
                ORDER BY ah.snapshot_date
                """;

        try (Connection conn = DBConnection.connect()) {

            try (PreparedStatement ps = conn.prepareStatement(nameSql)) {
                ps.setInt(1, adopterId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    System.out.println("\n--- Demographic History for: "
                            + rs.getString("first_name") + " " + rs.getString("last_name") + " ---");
                } else {
                    System.out.println("Adopter not found.");
                    return;
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(histSql)) {
                ps.setInt(1, adopterId);
                ResultSet rs = ps.executeQuery();
                TablePrinter.print(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching history: " + e.getMessage());
        }
    }

    // =========================================================
    // 8. [REQ14] COMPARE SALES BEFORE vs AFTER DEMOGRAPHIC CHANGE
    //    Uses adopter_history_id on adoption_transaction to join
    //    each sale to the demographic snapshot valid at that time.
    // =========================================================
    private static void compareSalesBeforeAfterChange() {
        int adopterId = InputHelper.getInt("Enter adopter ID to analyze: ");

        System.out.println("\n--- REQ14: Sales Before vs After Demographic Change ---");
        System.out.println("This shows each adoption linked to the adopter's city/address");
        System.out.println("at the TIME of that adoption (not their current address).\n");

        // Step 1: Show demographic snapshots so user can pick the change point
        String snapSql = """
                SELECT history_id, snapshot_date, city, address, age, change_reason
                FROM adopter_history
                WHERE adopter_id = ?
                ORDER BY snapshot_date
                """;

        try (Connection conn = DBConnection.connect()) {

            try (PreparedStatement ps = conn.prepareStatement(snapSql)) {
                ps.setInt(1, adopterId);
                ResultSet rs = ps.executeQuery();
                System.out.println("Demographic snapshots for this adopter:");
                TablePrinter.print(rs);
            }

            // Ask user which snapshot is the "change point"
            int changeHistoryId = InputHelper.getInt(
                    "Enter the history_id of the snapshot that marks the CHANGE point: ");

            // Get the date of that snapshot
            String dateSql = "SELECT snapshot_date FROM adopter_history WHERE history_id = ?";
            Timestamp changeDate;
            try (PreparedStatement ps = conn.prepareStatement(dateSql)) {
                ps.setInt(1, changeHistoryId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    System.out.println("History record not found.");
                    return;
                }
                changeDate = rs.getTimestamp("snapshot_date");
            }

            // Step 2: Sales BEFORE the change point
            String beforeSql = """
                    SELECT
                        at2.transaction_id,
                        at2.transaction_timestamp,
                        ah.city          AS city_at_time,
                        ah.address       AS address_at_time,
                        ah.age           AS age_at_time,
                        GROUP_CONCAT(c.cat_name SEPARATOR ', ') AS cats,
                        taf.total_amount
                    FROM adoption_transaction at2
                    JOIN adopter_history ah ON at2.adopter_history_id = ah.history_id
                    JOIN adoption_basket_items abi ON at2.transaction_id = abi.transaction_id
                    JOIN cat c ON abi.cat_id = c.cat_id
                    JOIN total_adoption_fees taf ON at2.transaction_id = taf.transaction_id
                    WHERE at2.adopter_id = ?
                      AND at2.transaction_timestamp < ?
                    GROUP BY at2.transaction_id, at2.transaction_timestamp,
                             ah.city, ah.address, ah.age, taf.total_amount
                    ORDER BY at2.transaction_timestamp
                    """;

            // Step 3: Sales AFTER the change point
            String afterSql = """
                    SELECT
                        at2.transaction_id,
                        at2.transaction_timestamp,
                        ah.city          AS city_at_time,
                        ah.address       AS address_at_time,
                        ah.age           AS age_at_time,
                        GROUP_CONCAT(c.cat_name SEPARATOR ', ') AS cats,
                        taf.total_amount
                    FROM adoption_transaction at2
                    JOIN adopter_history ah ON at2.adopter_history_id = ah.history_id
                    JOIN adoption_basket_items abi ON at2.transaction_id = abi.transaction_id
                    JOIN cat c ON abi.cat_id = c.cat_id
                    JOIN total_adoption_fees taf ON at2.transaction_id = taf.transaction_id
                    WHERE at2.adopter_id = ?
                      AND at2.transaction_timestamp >= ?
                    GROUP BY at2.transaction_id, at2.transaction_timestamp,
                             ah.city, ah.address, ah.age, taf.total_amount
                    ORDER BY at2.transaction_timestamp
                    """;

            System.out.println("\n=== BEFORE change (snapshot date: " + changeDate + ") ===");
            double totalBefore = 0;
            try (PreparedStatement ps = conn.prepareStatement(beforeSql)) {
                ps.setInt(1, adopterId);
                ps.setTimestamp(2, changeDate);
                ResultSet rs = ps.executeQuery();
                // Collect total before printing
                totalBefore = sumAndPrint(conn, adopterId, changeDate, true);
                // Re-run for TablePrinter
                ps.setInt(1, adopterId);
                ps.setTimestamp(2, changeDate);
                ResultSet rs2 = ps.executeQuery();
                TablePrinter.print(rs2);
            }

            System.out.println("\n=== AFTER change (snapshot date: " + changeDate + ") ===");
            double totalAfter = 0;
            try (PreparedStatement ps = conn.prepareStatement(afterSql)) {
                ps.setInt(1, adopterId);
                ps.setTimestamp(2, changeDate);
                ResultSet rs = ps.executeQuery();
                totalAfter = sumAndPrint(conn, adopterId, changeDate, false);
                ps.setInt(1, adopterId);
                ps.setTimestamp(2, changeDate);
                ResultSet rs2 = ps.executeQuery();
                TablePrinter.print(rs2);
            }

            // Summary
            System.out.println("\n========== SUMMARY ==========");
            System.out.printf(" Total spent BEFORE change : $%.2f%n", totalBefore);
            System.out.printf(" Total spent AFTER  change : $%.2f%n", totalAfter);
            System.out.printf(" Difference                : $%.2f%n", (totalAfter - totalBefore));
            System.out.println("==============================");

        } catch (SQLException e) {
            System.out.println("Error comparing sales: " + e.getMessage());
        }
    }

    // Helper: sum total_amount for before/after query
    private static double sumAndPrint(Connection conn, int adopterId,
                                      Timestamp changeDate, boolean before) throws SQLException {
        String op = before ? "<" : ">=";
        String sql = "SELECT COALESCE(SUM(taf.total_amount), 0) AS total " +
                     "FROM adoption_transaction at2 " +
                     "JOIN total_adoption_fees taf ON at2.transaction_id = taf.transaction_id " +
                     "WHERE at2.adopter_id = ? AND at2.transaction_timestamp " + op + " ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adopterId);
            ps.setTimestamp(2, changeDate);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("total");
        }
        return 0;
    }

    // =========================================================
    // 9. [REQ14] SALES BREAKDOWN BY CITY ACROSS ALL ADOPTERS
    //    Each sale is attributed to the city the adopter lived in
    //    AT THE TIME of the adoption (via adopter_history_id).
    // =========================================================
    private static void salesBreakdownByCity() {
        System.out.println("\n--- REQ14: Sales Breakdown by City (at time of adoption) ---");

        String sql = """
                SELECT
                    ah.city                          AS city_at_adoption,
                    COUNT(DISTINCT at2.transaction_id) AS total_transactions,
                    COUNT(abi.cat_id)                AS total_cats_adopted,
                    SUM(taf.total_amount)            AS total_revenue,
                    AVG(taf.total_amount)            AS avg_per_transaction
                FROM adoption_transaction at2
                JOIN adopter_history ah  ON at2.adopter_history_id = ah.history_id
                JOIN adoption_basket_items abi ON at2.transaction_id = abi.transaction_id
                JOIN total_adoption_fees taf   ON at2.transaction_id = taf.transaction_id
                GROUP BY ah.city
                ORDER BY total_revenue DESC
                """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            TablePrinter.print(rs);

        } catch (SQLException e) {
            System.out.println("Error fetching city breakdown: " + e.getMessage());
        }
    }

    // =========================================================
    // HELPER: Insert adopter_history snapshot (REQ14)
    // =========================================================
    public static void insertAdopterSnapshot(Connection conn, int adopterId,
                                              String city, String address,
                                              int birthYear, int age,
                                              String reason) throws SQLException {
        String sql = """
                INSERT INTO adopter_history (adopter_id, snapshot_date, city, address,
                                             birth_year, age, change_reason)
                VALUES (?, NOW(), ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adopterId);
            ps.setString(2, city);
            ps.setString(3, address);
            ps.setInt(4, birthYear);
            ps.setInt(5, age);
            ps.setString(6, reason);
            ps.executeUpdate();
        }
    }
}

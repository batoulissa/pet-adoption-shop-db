package petadoption;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdoptionMenu {

    // =========================================================
    // MAIN MENU
    // =========================================================
    public static void menu() {
        while (true) {
            System.out.println("\n========== ADOPTION MENU ==========");
            System.out.println(" 1. Browse available cats");
            System.out.println(" 2. Create new adoption transaction");
            System.out.println(" 3. View transaction details");
            System.out.println(" 4. View all transactions");
            System.out.println(" 5. View transactions by adopter");
            System.out.println(" 6. View transactions by shelter");
            System.out.println(" 0. Back to main menu");
            System.out.println("====================================");

            int choice = InputHelper.getMenuChoice("Enter choice: ", 0, 6);
            switch (choice) {
                case 1 -> browseAvailableCats();
                case 2 -> createAdoptionTransaction();
                case 3 -> viewTransactionDetails();
                case 4 -> viewAllTransactions();
                case 5 -> viewTransactionsByAdopter();
                case 6 -> viewTransactionsByShelter();
                case 0 -> { return; }
            }
        }
    }

    // =========================================================
    // 1. BROWSE AVAILABLE CATS (uses v_available_cats view)
    // =========================================================
    private static void browseAvailableCats() {
        System.out.println("\n--- Available Cats ---");
        System.out.println("Filter by:");
        System.out.println(" 1. All available cats");
        System.out.println(" 2. By type (kitten / adult / senior / special_needs)");
        System.out.println(" 3. By gender");
        System.out.println(" 4. By breed");
        int filter = InputHelper.getMenuChoice("Choice: ", 1, 4);

        String sql;
        String param = null;

        String base = """
                SELECT cat_id,
                       cat_name AS name,
                       breed,
                       age_months AS age,
                       cat_type AS type,
                       gender,
                       color,
                       description AS descript,
                       intake_date AS intake,
                       current_adoption_fee AS fee,
                       medical_visits AS visits
                FROM v_available_cats
                """;

        switch (filter) {
            case 2 -> {
                param = InputHelper.getString("Enter type (kitten/adult/senior/special_needs): ");
                sql = base + "WHERE cat_type = ? ORDER BY cat_id";
            }
            case 3 -> {
                param = InputHelper.getString("Enter gender (male/female): ");
                sql = base + "WHERE gender = ? ORDER BY cat_id";
            }
            case 4 -> {
                param = InputHelper.getString("Enter breed: ");
                sql = base + "WHERE breed LIKE ? ORDER BY cat_id";
                param = "%" + param + "%";
            }
            default -> sql = base + "ORDER BY cat_id";
        }

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (param != null) ps.setString(1, param);
            ResultSet rs = ps.executeQuery();
            TablePrinter.print(rs);

        } catch (SQLException e) {
            System.out.println("Error fetching available cats: " + e.getMessage());
        }
    }

    // =========================================================
    // 2. CREATE NEW ADOPTION TRANSACTION
    // =========================================================
    private static void createAdoptionTransaction() {
        System.out.println("\n--- New Adoption Transaction ---");

        // a) Get adopter
        int adopterId = InputHelper.getInt("Enter adopter ID: ");
        if (!adopterExists(adopterId)) {
            System.out.println("Adopter ID not found.");
            return;
        }
        printAdopterSummary(adopterId);

        // b) Get shelter
        int shelterId = InputHelper.getInt("Enter shelter ID: ");
        if (!shelterExists(shelterId)) {
            System.out.println("Shelter ID not found.");
            return;
        }

        // c) Build basket
        List<int[]> basket = new ArrayList<>();
        List<String> catNames = new ArrayList<>();

        while (true) {
            System.out.println("\nCurrent basket: " + (basket.isEmpty() ? "(empty)" : catNames));
            System.out.println(" 1. Add a cat");
            System.out.println(" 2. Remove last cat");
            System.out.println(" 3. Done - proceed to checkout");
            int action = InputHelper.getMenuChoice("Choice: ", 1, 3);

            if (action == 3) {
                if (basket.isEmpty()) {
                    System.out.println("Basket is empty. Add at least one cat.");
                    continue;
                }
                break;
            }

            if (action == 2) {
                if (!basket.isEmpty()) {
                    basket.remove(basket.size() - 1);
                    catNames.remove(catNames.size() - 1);
                    System.out.println("Last cat removed.");
                }
                continue;
            }

            // Add cat
            int catId = InputHelper.getInt("Enter cat ID to adopt: ");

            boolean duplicate = basket.stream().anyMatch(b -> b[0] == catId);
            if (duplicate) {
                System.out.println("That cat is already in the basket.");
                continue;
            }

            String catSql = """
                    SELECT c.cat_id, c.cat_name, c.cat_type, c.status,
                           f.fee_id, f.unit_price
                    FROM cat c
                    JOIN fee_schedule f
                      ON f.cat_type = c.cat_type AND f.effective_to IS NULL
                    WHERE c.cat_id = ?
                    """;
            try (Connection conn = DBConnection.connect();
                 PreparedStatement ps = conn.prepareStatement(catSql)) {

                ps.setInt(1, catId);
                ResultSet rs = ps.executeQuery();

                if (!rs.next()) {
                    System.out.println("Cat ID not found.");
                    continue;
                }

                String status = rs.getString("status");
                if (!status.equals("available")) {
                    System.out.println("Cat is not available (status: " + status + ").");
                    continue;
                }

                String catName = rs.getString("cat_name");
                String catType = rs.getString("cat_type");
                int    feeId   = rs.getInt("fee_id");
                double price   = rs.getDouble("unit_price");

                basket.add(new int[]{catId, feeId, (int)(price * 100)});
                catNames.add(catName + " (" + catType + ", $" + String.format("%.2f", price) + ")");
                System.out.println("Added: " + catName + " — $" + String.format("%.2f", price));

            } catch (SQLException e) {
                System.out.println("Error fetching cat: " + e.getMessage());
            }
        }

        // d) Show basket summary
        System.out.println("\n========== BASKET SUMMARY ==========");
        double total = 0;
        for (int i = 0; i < basket.size(); i++) {
            double price = basket.get(i)[2] / 100.0;
            System.out.printf("  %d. %s%n", i + 1, catNames.get(i));
            total += price;
        }
        System.out.printf("  TOTAL: $%.2f%n", total);
        System.out.println("=====================================");

        if (!InputHelper.getBool("Confirm adoption transaction?")) {
            System.out.println("Transaction cancelled.");
            return;
        }

        // e) Insert everything in one transaction
        try (Connection conn = DBConnection.connect()) {
            conn.setAutoCommit(false);

            try {
                int historyId = getLatestHistoryId(conn, adopterId);

                String txnSql = """
                        INSERT INTO adoption_transaction
                            (transaction_timestamp, shelter_id, adopter_id, adopter_history_id)
                        VALUES (NOW(), ?, ?, ?)
                        """;
                int transactionId;
                try (PreparedStatement ps = conn.prepareStatement(txnSql,
                                                Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, shelterId);
                    ps.setInt(2, adopterId);
                    if (historyId > 0) ps.setInt(3, historyId);
                    else               ps.setNull(3, Types.INTEGER);
                    ps.executeUpdate();
                    ResultSet keys = ps.getGeneratedKeys();
                    keys.next();
                    transactionId = keys.getInt(1);
                }

                String itemSql = """
                        INSERT INTO adoption_basket_items
                            (transaction_id, cat_id, fee_id, quantity, unit_price_at_sale)
                        VALUES (?, ?, ?, 1, ?)
                        """;
                try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                    for (int[] item : basket) {
                        ps.setInt(1, transactionId);
                        ps.setInt(2, item[0]);
                        ps.setInt(3, item[1]);
                        ps.setDouble(4, item[2] / 100.0);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                String feeSql = """
                        INSERT INTO total_adoption_fees (transaction_id, total_amount)
                        VALUES (?, ?)
                        """;
                try (PreparedStatement ps = conn.prepareStatement(feeSql)) {
                    ps.setInt(1, transactionId);
                    ps.setDouble(2, total);
                    ps.executeUpdate();
                }

                String updateCatSql = "UPDATE cat SET status = 'adopted' WHERE cat_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateCatSql)) {
                    for (int[] item : basket) {
                        ps.setInt(1, item[0]);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                conn.commit();
                System.out.println("\nTransaction #" + transactionId + " completed successfully!");
                System.out.printf("Total charged: $%.2f%n", total);

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Transaction failed — rolled back: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

    // =========================================================
    // 3. VIEW TRANSACTION DETAILS
    // =========================================================
    private static void viewTransactionDetails() {
        int txnId = InputHelper.getInt("Enter transaction ID: ");
     
        String headerSql = """
                SELECT
                    at2.transaction_id AS txn_id,
                    at2.transaction_timestamp AS timestamp,
                    a.first_name,
                    a.last_name,
                    ah.city       AS city_at_time,
                    ah.address    AS addr_at_time,
                    ah.age        AS age_at_time,
                    s.shelter_name AS shelter,
                    taf.total_amount AS total
                FROM adoption_transaction at2
                JOIN adopter a   ON at2.adopter_id = a.adopter_id
                LEFT JOIN adopter_history ah ON at2.adopter_history_id = ah.history_id
                JOIN shelter s   ON at2.shelter_id = s.shelter_id
                JOIN total_adoption_fees taf ON at2.transaction_id = taf.transaction_id
                WHERE at2.transaction_id = ?
                ORDER BY at2.transaction_id ASC
                """;     

        String itemsSql = """
                SELECT
                    c.cat_id,
                    c.cat_name,
                    c.breed,
                    c.cat_type,
                    c.gender,
                    abi.unit_price_at_sale   AS price_at_adoption,
                    fs.unit_price            AS current_price,
                    abi.fee_id,
                    fs.effective_from        AS fee_period_from,
                    COALESCE(fs.effective_to, 'current') AS fee_period_to
                FROM adoption_basket_items abi
                JOIN cat c          ON abi.cat_id = c.cat_id
                JOIN fee_schedule fs ON abi.fee_id = fs.fee_id
                WHERE abi.transaction_id = ?
                """;

        try (Connection conn = DBConnection.connect()) {

            System.out.println("\n--- Transaction Header ---");
            try (PreparedStatement ps = conn.prepareStatement(headerSql)) {
                ps.setInt(1, txnId);
                ResultSet rs = ps.executeQuery();
                TablePrinter.print(rs);
            }

            System.out.println("\n--- Cats in this Transaction (REQ13: price snapshot) ---");
            try (PreparedStatement ps = conn.prepareStatement(itemsSql)) {
                ps.setInt(1, txnId);
                ResultSet rs = ps.executeQuery();
                TablePrinter.print(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching transaction: " + e.getMessage());
        }
    }

    // =========================================================
    // 4. VIEW ALL TRANSACTIONS
    // =========================================================
    private static void viewAllTransactions() {
        String sql = """
                SELECT
                    at2.transaction_id,
                    at2.transaction_timestamp,
                    CONCAT(a.first_name, ' ', a.last_name) AS adopter,
                    s.shelter_name,
                    taf.total_amount
                FROM adoption_transaction at2
                JOIN adopter a ON at2.adopter_id = a.adopter_id
                JOIN shelter s ON at2.shelter_id = s.shelter_id
                JOIN total_adoption_fees taf ON at2.transaction_id = taf.transaction_id
                ORDER BY at2.transaction_timestamp DESC
                """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- All Adoption Transactions ---");
            TablePrinter.print(rs);

        } catch (SQLException e) {
            System.out.println("Error fetching transactions: " + e.getMessage());
        }
    }

    // =========================================================
    // 5. VIEW TRANSACTIONS BY ADOPTER
    // =========================================================
    private static void viewTransactionsByAdopter() {
        int adopterId = InputHelper.getInt("Enter adopter ID: ");

        String sql = """
                SELECT
                    at2.transaction_id,
                    at2.transaction_timestamp,
                    s.shelter_name,
                    ah.city   AS city_at_adoption,
                    ah.age    AS age_at_adoption,
                    GROUP_CONCAT(c.cat_name SEPARATOR ', ') AS cats,
                    taf.total_amount
                FROM adoption_transaction at2
                JOIN shelter s ON at2.shelter_id = s.shelter_id
                LEFT JOIN adopter_history ah ON at2.adopter_history_id = ah.history_id
                JOIN adoption_basket_items abi ON at2.transaction_id = abi.transaction_id
                JOIN cat c ON abi.cat_id = c.cat_id
                JOIN total_adoption_fees taf ON at2.transaction_id = taf.transaction_id
                WHERE at2.adopter_id = ?
                GROUP BY at2.transaction_id, at2.transaction_timestamp,
                         s.shelter_name, ah.city, ah.age, taf.total_amount
                ORDER BY at2.transaction_timestamp
                """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, adopterId);
            ResultSet rs = ps.executeQuery();
            System.out.println("\n--- Transactions for Adopter " + adopterId + " ---");
            TablePrinter.print(rs);

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================
    // 6. VIEW TRANSACTIONS BY SHELTER
    // =========================================================
    private static void viewTransactionsByShelter() {
        int shelterId = InputHelper.getInt("Enter shelter ID: ");

        String sql = """
                SELECT
                    at2.transaction_id,
                    at2.transaction_timestamp,
                    CONCAT(a.first_name, ' ', a.last_name) AS adopter,
                    GROUP_CONCAT(c.cat_name SEPARATOR ', ') AS cats,
                    taf.total_amount
                FROM adoption_transaction at2
                JOIN adopter a ON at2.adopter_id = a.adopter_id
                JOIN adoption_basket_items abi ON at2.transaction_id = abi.transaction_id
                JOIN cat c ON abi.cat_id = c.cat_id
                JOIN total_adoption_fees taf ON at2.transaction_id = taf.transaction_id
                WHERE at2.shelter_id = ?
                GROUP BY at2.transaction_id, at2.transaction_timestamp,
                         a.first_name, a.last_name, taf.total_amount
                ORDER BY at2.transaction_timestamp DESC
                """;

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, shelterId);
            ResultSet rs = ps.executeQuery();
            System.out.println("\n--- Transactions for Shelter " + shelterId + " ---");
            TablePrinter.print(rs);

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================
    private static boolean adopterExists(int adopterId) {
        return countExists("SELECT COUNT(*) FROM adopter WHERE adopter_id = ?", adopterId);
    }

    private static boolean shelterExists(int shelterId) {
        return countExists("SELECT COUNT(*) FROM shelter WHERE shelter_id = ?", shelterId);
    }

    private static boolean countExists(String sql, int id) {
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private static void printAdopterSummary(int adopterId) {
        String sql = "SELECT first_name, last_name, email, city FROM adopter WHERE adopter_id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adopterId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Adopter: " + rs.getString("first_name")
                        + " " + rs.getString("last_name")
                        + " | " + rs.getString("email")
                        + " | " + rs.getString("city"));
            }
        } catch (SQLException e) {
            System.out.println("Could not fetch adopter summary.");
        }
    }

    private static int getLatestHistoryId(Connection conn, int adopterId) throws SQLException {
        String sql = """
                SELECT history_id FROM adopter_history
                WHERE adopter_id = ?
                ORDER BY snapshot_date DESC
                LIMIT 1
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adopterId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("history_id");
        }
        return -1;
    }
}
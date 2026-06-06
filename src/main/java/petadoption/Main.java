package petadoption;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Connection conn = DBConnection.connect();
             Scanner scanner = new Scanner(System.in)) {

            if (conn == null) {
                System.out.println("Database connection failed.");
                return;
            }

            CatMenu catMenu = new CatMenu(conn, scanner);
            FeeMenu feeMenu = new FeeMenu(conn, scanner);
            WorkerMenu workerMenu = new WorkerMenu(conn, scanner);
            ReportMenu reportMenu = new ReportMenu(conn, scanner);

            while (true) {
                System.out.println();
                System.out.println("===== Cat Adoption Center System =====");
                System.out.println("1. Cat Menu");
                System.out.println("2. Fee Menu");
                System.out.println("3. Adopter Menu");
                System.out.println("4. Adoption Menu");
                System.out.println("5. Worker Menu");
                System.out.println("6. Report Menu");
                System.out.println("0. Exit");

                int choice = readInt(scanner, "Choose menu: ");

                switch (choice) {
                    case 1 -> showCatMenu(scanner, catMenu);
                    case 2 -> showFeeMenu(scanner, feeMenu);
                    case 3 -> AdopterMenu.menu();
                    case 4 -> AdoptionMenu.menu();
                    case 5 -> workerMenu.showMenu();
                    case 6 -> reportMenu.showMenu();
                    case 0 -> {
                        System.out.println("Program ended.");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void showCatMenu(Scanner scanner, CatMenu catMenu) {
        while (true) {
            System.out.println();
            System.out.println("===== Cat Menu =====");
            System.out.println("1. Add cat");
            System.out.println("2. Search available cats by type");
            System.out.println("3. Delete available cat");
            System.out.println("0. Back");

            int choice = readInt(scanner, "Choose menu: ");

            switch (choice) {
                case 1 -> catMenu.addCat();
                case 2 -> catMenu.searchAvailableCatsByType();
                case 3 -> catMenu.deleteAvailableCat();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void showFeeMenu(Scanner scanner, FeeMenu feeMenu) {
        while (true) {
            System.out.println();
            System.out.println("===== Fee Menu =====");
            System.out.println("1. Update adoption fee");
            System.out.println("2. Analyze fee change");
            System.out.println("0. Back");

            int choice = readInt(scanner, "Choose menu: ");

            switch (choice) {
                case 1 -> feeMenu.updateAdoptionFee();
                case 2 -> feeMenu.priceChangeAnalysis();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
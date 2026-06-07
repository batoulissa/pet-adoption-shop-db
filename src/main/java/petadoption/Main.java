package petadoption;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import petadoption.AdopterMenu;
import petadoption.AdoptionMenu;
import petadoption.CatMenu;
import petadoption.DBConnection;
import petadoption.FeeMenu;

public class Main {

    // Color constants
    static final String RESET  = "\u001B[0m";
    static final String BOLD   = "\u001B[1m";
    static final String CYAN   = "\u001B[36m";
    static final String GREEN  = "\u001B[32m";
    static final String RED    = "\u001B[31m";
    static final String YELLOW = "\u001B[33m";
    static final String WHITE  = "\u001B[37m";

    // Standard box width for all menus
    static final String TOP = CYAN + BOLD + "╔══════════════════════════════════════════════╗" + RESET;
    static final String BOT = CYAN + BOLD + "╚══════════════════════════════════════════════╝" + RESET;

    public static void main(String[] args) {
        try (Connection conn = DBConnection.connect();
                Scanner scanner = new Scanner(System.in)) {

            if (conn == null) {
                System.out.println(RED + "✘ Database connection failed." + RESET);
                return;
            }

            // Set shared scanner for InputHelper (fixes dual-scanner bug)
            InputHelper.setScanner(scanner);

            CatMenu catMenu       = new CatMenu(conn, scanner);
            FeeMenu feeMenu       = new FeeMenu(conn, scanner);
            WorkerMenu workerMenu = new WorkerMenu(conn, scanner);
            ReportMenu reportMenu = new ReportMenu(conn, scanner);

            while (true) {
                System.out.println();
                System.out.println(TOP);
                System.out.println(CYAN + BOLD + "║          CAT ADOPTION CENTER SYSTEM          ║" + RESET);
                System.out.println(CYAN + BOLD + "║               🐱  WELCOME!  🐱               ║" + RESET);
                System.out.println(BOT);
                System.out.println();
                System.out.println("  " + CYAN + BOLD + "[1]" + RESET + WHITE + " Cat Menu"      + RESET);
                System.out.println("  " + CYAN + BOLD + "[2]" + RESET + WHITE + " Fee Menu"      + RESET);
                System.out.println("  " + CYAN + BOLD + "[3]" + RESET + WHITE + " Adopter Menu"  + RESET);
                System.out.println("  " + CYAN + BOLD + "[4]" + RESET + WHITE + " Adoption Menu" + RESET);
                System.out.println("  " + CYAN + BOLD + "[5]" + RESET + WHITE + " Worker Menu"   + RESET);
                System.out.println("  " + CYAN + BOLD + "[6]" + RESET + WHITE + " Report Menu"   + RESET);
                System.out.println("  " + RED  + BOLD + "[0]" + RESET + RED   + " Exit"          + RESET);
                System.out.println();

                int choice = readInt(scanner, YELLOW + "  ▶ Choose: " + RESET);

                switch (choice) {
                    case 1 -> showCatMenu(scanner, catMenu);
                    case 2 -> showFeeMenu(scanner, feeMenu);
                    case 3 -> AdopterMenu.menu();
                    case 4 -> AdoptionMenu.menu();
                    case 5 -> workerMenu.showMenu();
                    case 6 -> reportMenu.showMenu();
                    case 0 -> {
                        System.out.println();
                        System.out.println(GREEN + "  Goodbye! See you next time~ 🐱" + RESET);
                        System.out.println();
                        return;
                    }
                    default -> System.out.println(RED + "  ✘ Invalid choice." + RESET);
                }
            }

        } catch (SQLException e) {
            System.out.println(RED + "Database error: " + e.getMessage() + RESET);
        }
    }

    private static void showCatMenu(Scanner scanner, CatMenu catMenu) {
        while (true) {
            System.out.println();
            System.out.println(TOP);
            System.out.println(CYAN + BOLD + "║                 🐱 Cat Menu                  ║" + RESET);
            System.out.println(BOT);
            System.out.println();
            System.out.println("  " + CYAN + BOLD + "[1]" + RESET + WHITE + " Add cat"                       + RESET);
            System.out.println("  " + CYAN + BOLD + "[2]" + RESET + WHITE + " Search available cats by type" + RESET);
            System.out.println("  " + CYAN + BOLD + "[3]" + RESET + WHITE + " Delete available cat"          + RESET);
            System.out.println("  " + RED  + BOLD + "[0]" + RESET + RED   + " Back to Main Menu"                          + RESET);
            System.out.println();

            int choice = readInt(scanner, YELLOW + "  ▶ Choose: " + RESET);

            switch (choice) {
                case 1 -> catMenu.addCat();
                case 2 -> catMenu.searchAvailableCatsByType();
                case 3 -> catMenu.deleteAvailableCat();
                case 0 -> {
                    return;
                }
                default -> System.out.println(RED + "  ✘ Invalid choice." + RESET);
            }
        }
    }

    private static void showFeeMenu(Scanner scanner, FeeMenu feeMenu) {
        while (true) {
            System.out.println();
            System.out.println(TOP);
            System.out.println(CYAN + BOLD + "║                 💰 Fee Menu                  ║" + RESET);
            System.out.println(BOT);
            System.out.println();
            System.out.println("  " + CYAN + BOLD + "[1]" + RESET + WHITE + " Update adoption fee" + RESET);
            System.out.println("  " + CYAN + BOLD + "[2]" + RESET + WHITE + " Analyze fee change"  + RESET);
            System.out.println("  " + RED  + BOLD + "[0]" + RESET + RED   + " Back to Main Menu"                + RESET);
            System.out.println();

            int choice = readInt(scanner, YELLOW + "  ▶ Choose: " + RESET);

            switch (choice) {
                case 1 -> feeMenu.updateAdoptionFee();
                case 2 -> feeMenu.priceChangeAnalysis();
                case 0 -> {
                    return;
                }
                default -> System.out.println(RED + "  ✘ Invalid choice." + RESET);
            }
        }
    }

    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(RED + "  ✘ Invalid input. Please enter a number." + RESET);
            }
        }
    }
}

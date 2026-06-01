package petadoption;

import java.util.Scanner;

public class InputHelper {
    private static Scanner scanner = new Scanner(System.in);

    //reads string from user
    public static String getString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    //reads integer from user
    public static int getInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
    }

    //reads double from user
    public static double getDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    //reads boolean (Y/N input) from user (eg: are you sure you want to delete this cat? Y/N)
    public static boolean getBool(String prompt) {
        while (true) {
            System.out.print(prompt+" (Y/N): ");
            String input=scanner.nextLine().trim().toUpperCase();
            if (input.equals("Y")) {
                return true;
            }
            else if (input.equals("N")) {
                return false;
            }
            else {
                System.out.println("Invalid input. Enter Y or N.");
            }
        }
    }

    //reads menu choice between min and max (eg: select a menu between 1 and 5)
    public static int getMenuChoice(String prompt, int min, int max) {
        while (true) {
            int choice=getInt(prompt);
            if (choice>=min && choice<=max) {
                return choice;
            }
            else {
                System.out.println("Invalid input. Please enter a number between "+min+" and "+max+": ");
            }
        }
    }

    //to close scanner after menu selection is done
    public static void close() {
        scanner.close();
    }
}
package petadoption;

import java.util.Scanner;

public class InputHelper {

    private static Scanner scanner = new Scanner(System.in);

    public static String getString(String name) {
        System.out.print(name);
        return scanner.nextLine();
    }
}
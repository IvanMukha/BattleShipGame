package org.example.utils;

public class ConsoleUtil {
    public static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("bash", "-c", "clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

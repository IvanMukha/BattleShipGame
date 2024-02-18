package org.example.serverUtils;

import java.net.ServerSocket;
import java.io.IOException;
import java.util.Scanner;

public class ServerUtils {

    public static boolean isPortAvailable(int port) {
        try (ServerSocket ignored = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    public static String readValidInput(Scanner scanner) {
        String input;
        boolean isValidInput;

        do {
            System.out.println("Введите координаты в формате: [A-P][1-9] или [A-P]1[0-6]: ");
            input = scanner.nextLine().trim();
            isValidInput = input.matches("^[A-P][1-9]$|^[A-P]1[0-6]$");
            if (!isValidInput) {
                System.out.println("Некорректный формат ввода. Пожалуйста, повторите попытку.");
            }
        } while (!isValidInput);

        return input;
    }
}



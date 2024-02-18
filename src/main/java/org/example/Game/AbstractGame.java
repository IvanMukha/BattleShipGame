package org.example.Game;

import org.example.models.Board;
import org.example.utils.ConsoleUtil;

public abstract  class AbstractGame {
    public void switchToPlayer(){}
    public void switchToPlayer2(){}
    public static void printGame(Board playerBoard, Board opponentBoard) {
        ConsoleUtil.clearConsole();
        int boardSize = playerBoard.getSize();

        System.out.print("    Ваше игровое поле:                    ");
        System.out.println("                     Поле противника:");;
        System.out.print("   ");
        for (int i = 0; i < boardSize; i++) {
            System.out.print(" " + (char)('A' + i) + " ");
        }

        System.out.print("             ");
        System.out.print("   ");
        for (int i = 0; i < boardSize; i++) {
            System.out.print(" " + (char)('A' + i) + " ");
        }
        System.out.println();

        for (int i = 0; i < boardSize; i++) {
            int rowNumber = i + 1;
            System.out.print((rowNumber < 10 ? " " : "") + rowNumber + " ");

            for (int j = 0; j < boardSize; j++) {
                if(playerBoard.getCell(j,i)=='O'){
                    System.out.print("[O]");
                }
                else if (playerBoard.getCell(j, i) == '.') {
                    System.out.print("[.]");
                } else if(playerBoard.getCell(j,i)==' '||playerBoard.getCell(j,i)=='*'){
                    System.out.print("[ ]");
                }else if(playerBoard.getCell(j,i)=='X'){
                    System.out.print("[X]");
                }
            }

            // Отступ между полями
            System.out.print("             ");

            // Выводим строку поля противника
            System.out.print((rowNumber < 10 ? " " : "") + rowNumber + " ");
            for (int j = 0; j < boardSize; j++) {
                if(opponentBoard.getCell(j,i)=='O'){
                    System.out.print("[ ]");
                }
                else if (opponentBoard.getCell(j, i) == '.') {
                    System.out.print("[.]"); // Если клетка содержит корабль или 'X', выводим '[X]'
                } else if(opponentBoard.getCell(j,i)==' '||opponentBoard.getCell(j,i)=='*'){
                    System.out.print("[ ]"); // В остальных случаях выводим пробел
                }else if(opponentBoard.getCell(j,i)=='X'){
                    System.out.print("[X]");
                }
            }

            System.out.println();
        }
    }
}

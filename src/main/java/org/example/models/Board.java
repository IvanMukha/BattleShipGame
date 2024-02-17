package org.example.models;

import org.example.utils.Converter;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Board {
    private final int SIZE=16;
    private    char[][] cells;
    public Board(){
        cells=new char[SIZE][SIZE];
        initializeBoard();
    }
    public int getSize() {
        return SIZE;
    }
    public char[][] getBoard() {
        return cells;
    }
    public char getCell(int x, int y) {
            return cells[x][y];
    }

    public void initializeBoard(){
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                cells[i][j]=' ';
            }
        }
    }

  public void printBoard(){
      System.out.print("   ");
      for (int i = 0; i < SIZE; i++) {
          System.out.print(" " + (char)('A' + i) + " ");
      }
      System.out.println();

      for (int i = 0; i < SIZE; i++) {
          int rowNumber = i + 1;
          String rowLabel = (rowNumber < 10) ? " " + rowNumber : String.valueOf(rowNumber);
          System.out.print(rowLabel + " ");
          for (int j = 0; j < SIZE; j++) {
              int columnNumber = j + 1;
              String columnLabel = (columnNumber < 10) ? " " + columnNumber : String.valueOf(columnNumber);
              if(getCell(j,i)=='O'){
                  System.out.print("[O]");
              }
              ///
              else if(getCell(j,i)=='.'){
                  System.out.print("[.]");
              }
              /////
              else if (getCell(j, i) == '*') {
                  System.out.print("[ ]"); // Если клетка содержит корабль или 'X', выводим '[X]'
              } else if(getCell(j,i)==' '){
                  System.out.print("[ ]"); // В остальных случаях выводим пробел
              }else if(getCell(j,i)=='X'){
                  System.out.print("[X]");
              }
          }
          System.out.println();
      }
  }

    public void updateCell(int x, int y, char value) {
        cells[x][y] = value;
    }

    public void markNearShips(Board board) {
        int boardSize = board.getSize();

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board.getCell(i, j) == 'O') {
                    markNearCells(board, i, j);
                }
            }
        }
    }


    private void markNearCells(Board board, int x, int y) {
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        int boardSize = board.getSize();

        for (int k = 0; k < dx.length; k++) {
            int nx = x + dx[k];
            int ny = y + dy[k];

            if (nx >= 0 && nx < boardSize && ny >= 0 && ny < boardSize && board.getCell(nx, ny) == ' ') {
                board.updateCell(nx, ny, '*');
            }
        }
    }

    private void markNearDestroyShip(Board board, int x, int y) {
        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

        int boardSize = board.getSize();

        for (int k = 0; k < dx.length; k++) {
            int nx = x + dx[k];
            int ny = y + dy[k];

            if (nx >= 0 && nx < boardSize && ny >= 0 && ny < boardSize && board.getCell(nx, ny) == '*') {
                board.updateCell(nx, ny, '.');
            }
        }
    }

    public void surroundDestroyedShip(Board board,Ship ship) {
        int sx=Converter.convertLetterToNumber(ship.getStartX());
        int sy=Integer.parseInt(ship.getStartY());
        if (ship.getOrientation() == Orientation.HORIZONTAL) {
            for (int j = sx - 1; j < sx + ship.getSize() - 1; j++) {
               int x = j;
              int y = sy - 1;
               markNearDestroyShip(board,x,y);
            }
        } else {

            for (int j = sy - 1; j < sy + ship.getSize() - 1; j++) {
                int x = sx - 1;
                int y = j;
                markNearDestroyShip(board,x,y);


            }
        }

    }

    public void autoPlaceShips(Board board,List<Ship>ships) {
        Random random = new Random();

        for (ShipType shipType : ShipType.values()) {
            int shipSize = shipType.getSize();

            for (int i = 0; i < shipType.getCount(); i++) {
                //  String startX, startY;
                Orientation orientation = random.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;

                // Генерация случайных координат
                int startXNumber = 1+random.nextInt(board.getSize()-1);
                int startYNumber =1+ random.nextInt(board.getSize()-1);

//                 Проверка возможности размещения корабля
                while (!checkShipPlacement(startXNumber, startYNumber, orientation, shipSize,board)) {
                    startXNumber =1+ random.nextInt(board.getSize()-1);
                    startYNumber =1+ random.nextInt(board.getSize()-1);
                }

                // Создание корабля и добавление на игровое поле
                Ship ship = new Ship(Converter.convertNumberToLetter(startXNumber), String.valueOf(startYNumber), orientation, shipSize);
                // System.out.println("Данные по кораблям из атаки"+"Данные по кораблю: "+"startX: "+ship.getStartX()+" startY: "+ship.getStartY()+" orientation "+ship.getOrientation()+" shipsize "+ship.getSize());


                placeShipOnBoard(startXNumber,startYNumber,orientation,shipSize,board,ships);
                board.markNearShips(board);

            }
        }
    }
    public void placeShipsManual(Board board,List<Ship>ships) {
        board.printBoard();
        Scanner scanner = new Scanner(System.in);

        for (ShipType shipType : ShipType.values()) {
            int shipSize = shipType.getSize();
            System.out.println("Расставьте корабли длиной " + shipSize);

            for (int i = 0; i < shipType.getCount(); i++) {
                String startX, startY;

                // Проверка ввода координаты X
                do {
                    System.out.println("Введите координаты начальной точки X (от A до P):");
                    startX = scanner.next().toUpperCase();
                    if (!isValidXCoordinate(startX)) {
                        System.out.println("Некорректная координата Y. Повторите ввод.");
                    }
                } while (!isValidXCoordinate(startX));

                do {
                    System.out.println("Введите координаты начальной точки Y (от 1 до 16):");
                    startY = scanner.next();
                    if (!isValidYCoordinate(startY)) {
                        System.out.println("Некорректная координата Y. Повторите ввод.");
                    }
                } while (!isValidYCoordinate(startY));

                int startXNumber = Converter.convertLetterToNumber(startX);
                int startYNumber = Integer.parseInt(startY);

                System.out.println("Введите ориентацию (H - горизонтальная, V - вертикальная):");
                String orientationInput = scanner.next();
                Orientation orientation;

                if (orientationInput.equalsIgnoreCase("H")) {
                    orientation = Orientation.HORIZONTAL;
                } else if (orientationInput.equalsIgnoreCase("V")) {
                    orientation = Orientation.VERTICAL;
                } else {
                    System.out.println("Некорректная ориентация. Повторите ввод.");
                    i--;
                    continue;
                }


                // Проверка на допустимые значения координат
                if (startXNumber < 1 || startYNumber < 1 || startXNumber > board.getSize() || startYNumber > board.getSize()) {
                    System.out.println("Некорректные координаты. Повторите ввод.");
                    i--;
                    continue;
                }

                // Проверка на перекрытие кораблей и на наличие соседних кораблей
                if (!checkShipPlacement(startXNumber, startYNumber, orientation, shipSize,board)) {
                    System.out.println("Некорректное расположение корабля. Повторите ввод.");
                    i--;
                    continue;
                }


                placeShipOnBoard(startXNumber,startYNumber,orientation,shipSize,board,ships);
                board.markNearShips(board);
                board.printBoard(); // Display the board after placing each ship
                System.out.println("Ship was placed"); // Display the ship that has been placed
            }
        }

    }
    public void placeShipOnBoard(int startXNumber, int startYNumber, Orientation orientation, int shipSize,Board board,List<Ship> ships){
        String startX=Converter.convertNumberToLetter(startXNumber);
        String startY=String.valueOf(startYNumber);
        Ship ship = new Ship(startX, startY, orientation, shipSize);
        ships.add(ship);

        if (orientation == Orientation.HORIZONTAL) {
            for (int j = startXNumber - 1; j < startXNumber + shipSize - 1; j++) {
                int x = j;
                int y = startYNumber - 1;
                board.updateCell(x, y, 'O');
            }
        } else {

            for (int j = startYNumber - 1; j < startYNumber + shipSize - 1; j++) {
                int x = startXNumber - 1;
                int y = j;
                board.updateCell(x, y, 'O');


            }
        }

    }
    private boolean checkShipPlacement(int startXNumber, int startYNumber, Orientation orientation, int shipSize,Board board) {
        // System.out.println("START X: "+startXNumber+" START Y: "+startYNumber);
        // Проверка на пересечение с другими кораблями
        int boardSize = board.getSize(); // Размер игрового поля (пример)

        if (orientation == Orientation.HORIZONTAL) {
            if (startXNumber + shipSize-1 > boardSize) {
                // System.out.println("Корабль выходит за пределы игрового поля по горизонтали");
                return false;
            }
            for (int i = startXNumber-1; i < startXNumber + shipSize-1; i++) {
                if (board.getCell(i, startYNumber-1) != ' ') {
                    return false;
                }
            }
        } else if (orientation == Orientation.VERTICAL) {
            if (startYNumber + shipSize-1 > boardSize) {
                //  System.out.println("Корабль выходит за пределы игрового поля по вертикали");

                return false;
            }
            for (int j = startYNumber-1; j < startYNumber + shipSize-1; j++) {
                // System.out.println("J:"+j+" i: "+(startXNumber-1));

                if (board.getCell(startXNumber-1, j) != ' ') {

                    return false;
                }
            }
        }
        return true;
    }
    private boolean isValidXCoordinate(String x) {
        return x.length() == 1 && x.charAt(0) >= 'A' && x.charAt(0) <= 'P';
    }
    private boolean isValidYCoordinate(String y) {
        try {
            int yNumber = Integer.parseInt(y);
            return yNumber >= 1 && yNumber <= 16;
        } catch (NumberFormatException e) {
            return false;
        }
    }












}




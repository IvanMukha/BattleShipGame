package org.example.models;


import org.example.Game.AbstractGame;
import org.example.Game.SinglePlayer;
import org.example.utils.Converter;

import java.util.*;

public class Player {
    private  String name;
    private Board board;
    private Board opponentBoard;
    private List<Ship> ships;
    private AbstractGame game;
    private int lastAttackedX=-1;
    private int lastAttackedY=-1;
    private int firstAttackX=-1;
    private int firstAttackY=-1;
    private boolean attackfromStartXY;
    private boolean isShipDestroyed = true;
    private boolean isShipDestructionMode = false;
    private List<Ship> opponentShips;


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getOpponentBoard() {
        return opponentBoard;
    }

    public boolean isShipDestructionMode() {
        return isShipDestructionMode;
    }


    private Map<Integer,Integer> attackedCells;

    public void setOpponentShips(List<Ship> opponentShips) {
        this.opponentShips = opponentShips;
    }




    public Player(String name, AbstractGame game) {
        this.name = name;
        this.board = new Board();
        this.ships = new ArrayList<>();
        this.game=game;
        this.attackedCells=new HashMap<>();
        board.initializeBoard();
    }
    public void setOpponentBoard(Board opponentBoard) {
        this.opponentBoard = opponentBoard;
    }
    public Board getBoard() {
        return board;
    }
    public List<Ship> getShips(){
        return ships;
    }

    public void attack(String sx, String sy) {

        int x=Converter.convertLetterToNumber(sx);
        int y=Integer.parseInt(sy);
        if (opponentBoard == null) {
            System.out.println("Ошибка: поле соперника не установлено.");
            game.switchToPlayer();
            return;
        }
        // Проверяем, что координаты находятся в пределах игрового поля соперника
        if (x < 0 || x > opponentBoard.getSize() || y < 0 || y > opponentBoard.getSize()) {
            System.out.println("Некорректные координаты. Введите другие координаты.");
            game.switchToPlayer();
            return;
        }

        // Получаем символ из игрового поля соперника по указанным координатам
        char attackResult = opponentBoard.getCell(x-1, y-1);

        // Обрабатываем результат атаки в зависимости от содержимого клетки
        switch (attackResult) {
            case ' ':
                System.out.println("Мимо.");
                opponentBoard.updateCell(x-1, y-1, '.');
                game.switchToPlayer2();
                break;

            case '*':
                System.out.println("Мимо.");
                opponentBoard.updateCell(x-1, y-1, '.');
                game.switchToPlayer2();
                break;

            case 'O':
                   opponentBoard.updateCell(x-1, y-1, 'X'); // Отмечаем клетку как пораженную
                for (Ship ship : opponentShips) {
                    if (ship.containsPoint(x, y)) {
                        if (ship.isShipDestroyed(opponentBoard)) {
                           opponentBoard.surroundDestroyedShip(opponentBoard,ship);
                            System.out.println("Корабль убит!");
                            game.switchToPlayer();
                            break;
                        } else {
                            System.out.println("Ранил.");
                            game.switchToPlayer();
                            break;
                        }

                    }

                }

                break;

            case 'X':
                System.out.println("Уже атаковано по этим координатам.");
                game.switchToPlayer();
                break;

            case '.':
                System.out.println("Уже атаковано по этим координатам.");
                game.switchToPlayer();
                break;

            default:
                System.out.println("Ошибка: неожиданный символ на игровом поле соперника.");
                game.switchToPlayer();
                break;

        }

    }
    public char attackOpponentOnline(String cell){
        String letterPart = cell.substring(0, 1);
        String numberPart = cell.substring(1);
        int x=Converter.convertLetterToNumber(letterPart);
        int y=Integer.parseInt(numberPart);
        char result=board.getCell(x-1,y-1);
        if(result=='O'){
            board.updateCell(x-1,y-1,'X');
        }else
            board.updateCell(x-1,y-1,'.');
        return result;
    }
    public void botAttack(int x,int y) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Бот атаковал по координатам X:"+x+" Y: "+y);
        char attackResult = opponentBoard.getCell(x, y);
            attackedCells.put(x,y);
        if (!isShipDestructionMode) {
            firstAttackX = x;
            firstAttackY = y;
        }

        switch (attackResult) {
            case ' ':
                System.out.println("Мимо.");
                opponentBoard.updateCell(x , y , '.');
                game.switchToPlayer();
                break;
            case '*':
                System.out.println("Мимо.");
                opponentBoard.updateCell(x, y, '.');
                game.switchToPlayer();
                break;
            case 'O':

                opponentBoard.updateCell(x , y , 'X'); // Отмечаем клетку как пораженную

                for (Ship ship : opponentShips) {

                    if (ship.containsPoint(x+1, y+1)) {
                        if (ship.isShipDestroyed(opponentBoard)) {

                            opponentBoard.surroundDestroyedShip(opponentBoard, ship);
                            System.out.println("Корабль убит!");
                            isShipDestroyed=true;
                            isShipDestructionMode=false;
                            firstAttackX=-1;
                            firstAttackY=-1;
                            attackfromStartXY=false;
                            game.switchToPlayer2();
                            break;
                        } else {
                            System.out.println("Ранил.");
                            isShipDestroyed=false;
                            isShipDestructionMode = true;
                            lastAttackedX=x;
                            lastAttackedY=y;
                            game.switchToPlayer2();

                            break;

                        }

                    }

                }
                    break;

            case 'X':
                game.switchToPlayer2();
                break;
            case '.':
                game.switchToPlayer2();
                break;

            default:
                System.out.println("Ошибка: неожиданный символ на игровом поле соперника.");

        }
    }
    public void destructAttackedShip(){
        if(attackfromStartXY){
            attackfromStartXY=false;
            attackNeighbors(firstAttackX,firstAttackY);
        }else
        attackNeighbors(lastAttackedX,lastAttackedY);
    }

    private void attackNeighbors(int x, int y) {
        // Проверяем соседние клетки на наличие кораблей (X)
        boolean hasLeft = hasShipNeighbor(x, y, -1, 0);
        boolean hasRight = hasShipNeighbor(x, y, 1, 0);
        boolean hasUp = hasShipNeighbor(x, y, 0, -1);
        boolean hasDown = hasShipNeighbor(x, y, 0, 1);

        // Проверяем, что все соседние клетки уже атакованы или находятся за пределами поля
        if (!(hasLeft || hasRight || hasUp || hasDown)) {
            lastAttackedX=x;
            lastAttackedY=y;
            attackAllDirections(x,y);
        } else {
            // Атакуем в соответствии с описанной логикой
            if (hasUp) {
                attackInDirection(x, y, 0, 1);
            } else if (hasDown) {
                attackInDirection(x, y, 0, -1);
            } else if (hasLeft) {
                attackInDirection(x, y, 1, 0);
            } else if (hasRight) {
                attackInDirection(x, y, -1, 0);
            }
        }
    }



    private boolean hasShipNeighbor(int x, int y, int dx, int dy) {
        int newX = x + dx;
        int newY = y + dy;
        return isValidCell(newX, newY, opponentBoard) && opponentBoard.getCell(newX, newY) == 'X';
    }

    //ошибка где-то снизу вроде бы.
    private void attackInDirection(int firstX, int firstY, int dx, int dy) {
        int newX=firstX+dx;
        int newY=firstY+dy;
        if(newX >= 0 && newX < 16 && newY >= 0 && newY < 16&&opponentBoard.getCell(newX,newY)!='O'){
            attackfromStartXY=true;
            game.switchToPlayer2();
        }
        if(isValidCell(newX,newY,opponentBoard)){
            botAttack(newX,newY);
        }
    }
                private void attackAllDirections(int x, int y) {
                    int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}}; // Направления: {влево, вправо, вверх, вниз}
                    for (int[] direction : directions) {
                        int dx = direction[0];
                        int dy = direction[1];
                        int newX = x + dx;
                        int newY = y + dy;
                        if (isValidCell(newX, newY, opponentBoard)) {
                            if(attackedCells.containsKey(newX)){
                                if(attackedCells.get(newX).equals(newY)){
                                    continue;
                                }
                            }
                            botAttack(newX, newY);
                            if (opponentBoard.getCell(newX, newY) != 'O') {
                                game.switchToPlayer();
                                break;

                            }
                        }
                    }
        }
        private boolean isValidCell(int x, int y,Board opponentBoard){
            if (x >= 0 && x < 16 && y >= 0 && y < 16 && opponentBoard.getCell(x, y) != '.')
                return true;
            else return false;

        }
    }



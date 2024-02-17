package org.example.Game;

import org.example.models.Board;
import org.example.models.Player;

import java.util.Random;
import java.util.Scanner;

public class SinglePlayer {
    private Scanner in;
    private Player player;
    private Player botplayer;
    public SinglePlayer(){
        this.in=new Scanner(System.in);

    }
    public void setPlayerSwitchflag(Boolean playerSwitchflag) {
        this.playerSwitchflag = playerSwitchflag;
    }

    public void setBotSwitchflag(Boolean botSwitchflag) {
        this.botSwitchflag = botSwitchflag;
    }

    Boolean playerSwitchflag=true;
    Boolean botSwitchflag=true;

    public void startgame(String name){
        Scanner in=new Scanner(System.in);
        System.out.println("Введите имя игрока");
        this.player=new Player(name,this);
        this.botplayer=new Player("Bot",this);
        player.setBoard(new Board());
        //player.getBoard().autoPlaceShips(player.getBoard(),player.getShips());
        botplayer.setBoard(new Board());
        botplayer.getBoard().autoPlaceShips(botplayer.getBoard(),botplayer.getShips());
        botplayer.setOpponentBoard(player.getBoard());
        botplayer.setOpponentShips(player.getShips());
        player.setOpponentShips(botplayer.getShips());
        player.setOpponentBoard(botplayer.getBoard());
        // printGame(player.getBoard(),botplayer.getBoard());


        System.out.println("Выберите как расставить корабли");
        System.out.println("1-В ручном режиме");
        System.out.println("2-автоматическое заполнение");
        int fillingMode=in.nextInt();
        while (true){
        if(fillingMode==1){
        player.getBoard().placeShipsManual(player.getBoard(),player.getShips());
        break;
        }
        else if (fillingMode==2) {
            player.getBoard().autoPlaceShips(player.getBoard(),player.getShips());
            break;

        }else
            System.out.println("Введено не корректное значение. Повторите ввод");

        }


        while (!allPlayerShipsDestroyed(player.getOpponentBoard())||!allPlayerShipsDestroyed(player.getOpponentBoard())){
            System.out.println("all ships des");
        switchToPlayer();


        }

    
    }
    public void endGame(){
        if(allPlayerShipsDestroyed(player.getBoard())){
            System.out.println("Бот победил!");
        }else if(allOpponentShipsDestroyed(player.getOpponentBoard())){
            System.out.println("Победил игрок: "+player.getName());
        }
    }
    public void switchToPlayer(){
        if(allOpponentShipsDestroyed(player.getOpponentBoard())){
            endGame();
        }
        printGame(player.getBoard(),botplayer.getBoard());
        System.out.println("Введите координату X: (от A до P)");
        String px=in.next();
        System.out.println("Введите координату Y: (от 1 до 16)");
        String py=in.next();
        player.attack(px,py);


    }
    public void switchToPlayer2(){
        if(allPlayerShipsDestroyed(player.getBoard())){
            endGame();

        }
        printGame(player.getBoard(),botplayer.getBoard());
        Random random=new Random();
        if(botplayer.isShipDestructionMode()){
              botplayer.destructAttackedShip();
        }else{
        int px= random.nextInt(16);
        int py= random.nextInt(16);
        botplayer.botAttack(px,py);}
    }

    public static boolean allOpponentShipsDestroyed(Board opponentBoard){
        int amountOfCellShips=0;
        for(int i=0;i<16;i++){
            for(int j=0;j<16;j++){
                if(opponentBoard.getCell(i,j)=='O'){
                amountOfCellShips++;}
            }
            }
        if(amountOfCellShips==0){
        return true;}else
        return false;
    }public static boolean allPlayerShipsDestroyed(Board board){
        int amountOfCellShips=0;
        for(int i=0;i<16;i++){
            for(int j=0;j<16;j++){
                if(board.getCell(i,j)=='O');
                amountOfCellShips++;
            }
            if(amountOfCellShips==0){
                return true;

            }
        }
        return false;
    }
    public static void printGame(Board playerBoard, Board opponentBoard) {
        int boardSize = playerBoard.getSize();

        // Выводим заголовок для своего поля
        System.out.print("    Ваше игровое поле:                    ");
        System.out.println("                     Поле противника:");;
        // Выводим буквы от A до P с отступом
        System.out.print("   ");
        for (int i = 0; i < boardSize; i++) {
            System.out.print(" " + (char)('A' + i) + " ");
        }
        System.out.print("             ");

        // Выводим буквы сверху поля противника с отступом
        System.out.print("   ");
        for (int i = 0; i < boardSize; i++) {
            System.out.print(" " + (char)('A' + i) + " ");
        }
        System.out.println();

        // Выводим строки и клетки своего поля и поля противника
        for (int i = 0; i < boardSize; i++) {
            // Выводим номер строки слева
            int rowNumber = i + 1;
            System.out.print((rowNumber < 10 ? " " : "") + rowNumber + " ");

            // Выводим строку своего поля
            for (int j = 0; j < boardSize; j++) {
                if(playerBoard.getCell(j,i)=='O'){
                    System.out.print("[O]");
                }
                else if (playerBoard.getCell(j, i) == '.') {
                    System.out.print("[.]"); // Если клетка содержит корабль или 'X', выводим '[X]'
                } else if(playerBoard.getCell(j,i)==' '||playerBoard.getCell(j,i)=='*'){
                    System.out.print("[ ]"); // В остальных случаях выводим пробел
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





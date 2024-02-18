package org.example.Game;

import org.example.models.Board;
import org.example.models.Player;

import java.util.Random;
import java.util.Scanner;

public class SinglePlayer extends AbstractGame {
    private Scanner in;
    private Player player;
    private Player botplayer;
    public SinglePlayer(){
        this.in=new Scanner(System.in);

    }
    public void startgame(String name){
        this.player=new Player(name,this);
        this.botplayer=new Player("Bot",this);
        player.setBoard(new Board());
        botplayer.setBoard(new Board());
        botplayer.getBoard().autoPlaceShips(botplayer.getBoard(),botplayer.getShips());
        botplayer.setOpponentBoard(player.getBoard());
        botplayer.setOpponentShips(player.getShips());
        player.setOpponentShips(botplayer.getShips());
        player.setOpponentBoard(botplayer.getBoard());



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

        if(!endGame()){
            switchToPlayer();
        }else break;

        }

    
    }
    public boolean endGame(){
        if(allPlayerShipsDestroyed(player.getBoard())){
            System.out.println("Бот победил!");
            return true;
        }else if(allOpponentShipsDestroyed(player.getOpponentBoard())){
            System.out.println("Победил игрок: "+player.getName());
            return true;
        }
       return false;
    }
    @Override
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


    }@Override
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



}





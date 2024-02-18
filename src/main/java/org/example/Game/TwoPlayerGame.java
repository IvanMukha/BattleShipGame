package org.example.Game;

import jakarta.websocket.DeploymentException;
import org.example.models.Board;
import org.example.serverUtils.ServerUtils;

import java.util.Scanner;
import org.example.models.Player;
import org.example.webSocket.WebSocketClientEndPoint;
import org.example.webSocket.WebSocketServerEndPoint;
import org.glassfish.tyrus.server.Server;

import static org.example.serverUtils.ServerUtils.isPortAvailable;

public class TwoPlayerGame  extends AbstractGame{

    private Player player1;
    private Player player2;
    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }



    Scanner in;

public TwoPlayerGame(){
    in=new Scanner(System.in);
    player1=new Player("Player1",this);
    player2=new Player("Player2",this);
    player1.setBoard(new Board());
    player2.setBoard(new Board());

}
    public  boolean allPlayerShipsDestroyed(Board board){
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

    public void startGame(String name) {
    int port;
        do {
            System.out.println("Введите порт (5000-8000): ");
            while (!in.hasNextInt()) {
                System.out.println("Пожалуйста, введите целое число.");
                in.next(); // Считываем и пропускаем некорректный ввод
            }
            port = in.nextInt();
            if (port < 5000 || port > 8000) {
                System.out.println("Порт должен быть в диапазоне от 5000 до 8000.");
            }
        } while (port < 5000 || port > 8000);
        if (isPortAvailable(port)) {
            player1.setName(name);
            WebSocketServerEndPoint.startServer(port,this);

        } else {
            player2.setName(name);
            System.out.println(player2.getName());
            WebSocketClientEndPoint.startClient(port,this);
        }
    }


public void startForFirstPlayer(){
    player1.setOpponentBoard(player2.getBoard());
    player1.setOpponentShips(player2.getShips());
    System.out.println("Выберите как расставить корабли");
    System.out.println("1-В ручном режиме");
    System.out.println("2-автоматическое заполнение");
    int fillingMode=in.nextInt();
    while (true){
        if(fillingMode==1){
            player1.getBoard().placeShipsManual(player1.getBoard(),player1.getShips());
            break;
        }
        else if (fillingMode==2) {
            player1.getBoard().autoPlaceShips(player1.getBoard(),player1.getShips());
            player1.getBoard().printBoard();

            break;

        }else
            System.out.println("Введено не корректное значение. Повторите ввод");

    }
    }


public void startForSecondPlayer(){
player2.setOpponentBoard(player1.getBoard());
player2.setOpponentShips(player1.getShips());
    System.out.println("Выберите как расставить корабли");
    System.out.println("1-В ручном режиме");
    System.out.println("2-автоматическое заполнение");
    int fillingMode=in.nextInt();
    while (true){
        if(fillingMode==1){
            player2.getBoard().placeShipsManual(player2.getBoard(),player2.getShips());
            break;
        }
        else if (fillingMode==2) {
            player2.getBoard().autoPlaceShips(player2.getBoard(),player2.getShips());
            player2.getBoard().printBoard();
            break;

        }else
            System.out.println("Введено не корректное значение. Повторите ввод");

    }
}
}








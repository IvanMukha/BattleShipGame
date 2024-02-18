package org.example.webSocket;

import jakarta.websocket.*;
import org.example.Game.AbstractGame;
import org.example.Game.TwoPlayerGame;
import org.example.models.Ship;
import org.glassfish.tyrus.client.ClientManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
public class WebSocketClientEndPoint {
    private static CountDownLatch latch;
    private  static TwoPlayerGame twoPlayerGame;
    String opponentName;


    private static Scanner in;

    public static void setTwoPlayerGame(TwoPlayerGame twoPlayerGame) {
        WebSocketClientEndPoint.twoPlayerGame = twoPlayerGame;
         in=new Scanner(System.in);
    }

    public void connectToServer(String serverUri) throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, new URI(serverUri));
    }
    @OnOpen
    public void onOpen(Session session) {
        try {
            session.getBasicRemote().sendText("Name_"+twoPlayerGame.getPlayer2().getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        twoPlayerGame.startForSecondPlayer();
    }
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
            if (message.length() < 50) {
                if (message.startsWith("NAME_")) {
                    opponentName = message.substring(5);
                } else if (message.equals("LOST")) {
                    handleLostMessage(session);
                } else if (message.matches("^[A-P][1-9]$|^[A-P]1[0-6]$")) {
                    handleAttackMessage(message, session);
                } else if (message.startsWith("Result_")) {
                    switchTurn(message, session);
                } else {
                    handleDefaultMessage(message, session);
                }
            }
        }

    private void switchTurn(String message, Session session) throws IOException {

        char resultChar = message.substring(message.indexOf("_") + 1).charAt(0);
        if (resultChar=='O') {
            String attackcell=in.next();
            twoPlayerGame.getPlayer2().attackOpponentOnline(attackcell);;
            session.getBasicRemote().sendText(attackcell);
        } else {
            System.out.println("Вы не попали");
            System.out.printf(" Ход переходит к другому игроку \n", opponentName);
            session.getBasicRemote().sendText("END_TURN");


        }
    }
    private void handleAttackMessage(String message, Session session) throws IOException {
        String cell =message;
        System.out.println("Борд 2 игрока через getboard");
        twoPlayerGame.getPlayer2().getBoard().printBoard();
        System.out.println("Борд 2 игрока через opponentgetboard");
        twoPlayerGame.getPlayer2().getOpponentBoard().printBoard();


        AbstractGame.printGame(twoPlayerGame.getPlayer2().getBoard(),twoPlayerGame.getPlayer1().getBoard());
        System.out.printf("%s's turn\n", twoPlayerGame.getPlayer2().getName());
        System.out.printf("%s attacked %s\n", twoPlayerGame.getPlayer2().getName(), message);
        processAttackResult(cell, session);
    }
    private void processAttackResult(String cell, Session session) throws IOException {
         char result=twoPlayerGame.getPlayer2().attackOpponentOnline(cell);
        if (result=='O') {
            System.out.printf("%s Противник попал \n", opponentName);
        }
        else {
            System.out.printf(twoPlayerGame.getPlayer1().getName()+"Промахнулся");
        }
        AbstractGame.printGame(twoPlayerGame.getPlayer2().getBoard(),twoPlayerGame.getPlayer1().getBoard());
        session.getBasicRemote().sendText("Result_"+result);

    }
    private void handleDefaultMessage(String message, Session session) throws IOException {
        AbstractGame.printGame(twoPlayerGame.getPlayer2().getBoard(),twoPlayerGame.getPlayer1().getBoard());
            if ("END_TURN".equals(message)) {
                System.out.printf("%s Не попал!\n", opponentName);
            }
            System.out.println("Ваш ход");
            String attackcell=in.next();
            twoPlayerGame.getPlayer1().attackOpponentOnline(attackcell);;
            System.out.println(attackcell);
            session.getBasicRemote().sendText(attackcell);
        }

    private void handleLostMessage(Session session) {
        System.out.println("Quitting the game");
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Игра закончена. " + twoPlayerGame.getPlayer1().getName() + " Победил."));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void startClient(int port,TwoPlayerGame twoPlayerGame) {
        setTwoPlayerGame(twoPlayerGame);
        System.out.println(twoPlayerGame.getPlayer1().getName());
        System.out.println(twoPlayerGame.getPlayer1().getBoard().getSize());
       twoPlayerGame.getPlayer1().getBoard().printBoard();
        System.out.println(twoPlayerGame.getPlayer2().getName());
         latch = new CountDownLatch(1);
        ClientManager client = ClientManager.createClient();
        try {
            String args = String.format("ws://localhost:%s/websockets/battleship", port);
            client.connectToServer(WebSocketClientEndPoint.class, new URI(args));
             latch.await();

        } catch (DeploymentException | URISyntaxException | IOException | InterruptedException e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
        } finally {
            latch.countDown();

        }
    }
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.printf("Session %s closed because of %s\n", session.getId(), closeReason);
        latch.countDown();
    }


}
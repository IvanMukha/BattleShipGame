package org.example.webSocket;

import jakarta.websocket.*;
import org.example.Game.AbstractGame;
import org.example.Game.TwoPlayerGame;
import org.example.models.Ship;
import org.example.serverUtils.ServerUtils;
import org.example.utils.Converter;
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
    private String opponentName;
    private String currentCell;

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
        int x=0;
        int y=0;
        if(currentCell!=null){String letterPart = currentCell.substring(0, 1);
            String numberPart = currentCell.substring(1);
            x=Converter.convertLetterToNumber(letterPart);
            y=Integer.parseInt(numberPart);
        }
        char resultChar = message.substring(message.indexOf("_") + 1).charAt(0);
        if (resultChar=='O') {
            if(currentCell!=null)  {twoPlayerGame.getPlayer1().getBoard().updateCell(x-1,y-1,'X');}
            System.out.println("Попадание по кораблю, можете атаковать ещё раз");
            AbstractGame.printGame(twoPlayerGame.getPlayer2().getBoard(),twoPlayerGame.getPlayer1().getBoard());

            String attackcell= ServerUtils.readValidInput(in);
            currentCell=attackcell;
            session.getBasicRemote().sendText(attackcell);
        } else {
            if(currentCell!=null)  {
                twoPlayerGame.getPlayer1().getBoard().updateCell(x-1,y-1,'.');}
            AbstractGame.printGame(twoPlayerGame.getPlayer2().getBoard(),twoPlayerGame.getPlayer1().getBoard());
            System.out.println("Вы не попали");
            System.out.printf(" Ход переходит к другому игроку \n", opponentName);
            session.getBasicRemote().sendText("END_TURN");


        }
    }
    private void handleAttackMessage(String message, Session session) throws IOException {
        currentCell =message;
        AbstractGame.printGame(twoPlayerGame.getPlayer2().getBoard(),twoPlayerGame.getPlayer1().getBoard());
        System.out.printf("%s's turn\n", twoPlayerGame.getPlayer2().getName());
        System.out.printf("%s attacked %s\n", twoPlayerGame.getPlayer2().getName(), message);
        processAttackResult(currentCell, session);
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
            String cellAttack=ServerUtils.readValidInput(in);
            currentCell=cellAttack;
            session.getBasicRemote().sendText(cellAttack);
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
    package org.example.webSocket;

    import jakarta.websocket.*;
    import jakarta.websocket.server.ServerEndpoint;
    import org.example.Game.AbstractGame;
    import org.example.Game.TwoPlayerGame;
    import org.example.models.Player;
    import org.glassfish.tyrus.server.Server;

    import java.io.IOException;
    import java.net.URI;
    import java.util.Scanner;

    @ServerEndpoint(value = "/battleship")
    public class WebSocketServerEndPoint extends Endpoint {
        private  Player player;
        private static String opponentName;
        private static volatile boolean isFinished = false;
        private static Scanner in;

        public static void setTwoPlayerGame(TwoPlayerGame twoPlayerGame) {
            WebSocketServerEndPoint.twoPlayerGame = twoPlayerGame;
            in=new Scanner(System.in);

        }

        private static TwoPlayerGame twoPlayerGame;



        public static void startServer(int port,TwoPlayerGame twoPlayerGame) {
            setTwoPlayerGame(twoPlayerGame);
            Server server;
            server = new Server("localhost", port, "/websockets", null, WebSocketServerEndPoint.class);
            try {
                server.start();
                System.out.println("---server is running and waiting players");
                twoPlayerGame.startForFirstPlayer();

                while (!isFinished) {
                    Thread.onSpinWait();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                server.stop();
            }
        }

        @OnMessage
        public void onMessage(String message, Session session) throws IOException {
            if (message.length() < 100) {
                if (message.startsWith("NAME_")) {
                    opponentName = message.substring(5);
                } else if (message.equals("LOST")) {
                    handleLostMessage(session);
                } else if (message.matches("^[A-P][1-9]$|^[A-P]1[0-6]$")) {
                    System.out.println("handleAttack " + message);
                    handleAttackMessage(message, session);
                } else if (message.startsWith("Result_")) {
                    System.out.println("message" + message);
                    switchTurn(message, session);
                } else {
                    handleDefaultMessage(message, session);
                }
            }
        }

        private void handleAttackMessage(String message, Session session) throws IOException {
            AbstractGame.printGame(twoPlayerGame.getPlayer1().getBoard(),twoPlayerGame.getPlayer2().getBoard());
            String cell =message;
            System.out.printf("Ход второго игрока \n",
            System.out.printf( "Второй игрок атаковал  %s\n",  message);
            processAttackResult(cell, session);
        }
        private void processAttackResult(String cell, Session session) throws IOException {
            char result=twoPlayerGame.getPlayer1().attackOpponentOnline(cell);
            if (result=='O') {
                System.out.printf("%s Противник попал \n", opponentName);
            }
             else {
            System.out.printf("Противник промахнулся");

            }
            AbstractGame.printGame(twoPlayerGame.getPlayer1().getBoard(),twoPlayerGame.getPlayer2().getBoard());
            session.getBasicRemote().sendText("Result_"+result);

        }
        private void handleLostMessage(Session session) {
            System.out.println("Quitting the game");
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Game finished. " + twoPlayerGame.getPlayer2().getName() + " win the battle."));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        private void switchTurn(String message, Session session) throws IOException {
            char resultChar = message.substring(message.indexOf("_") + 1).charAt(0);
            if (resultChar=='O') {
                System.out.println("Попадание по кораблю, можете атаковать ещё раз");;
                AbstractGame.printGame(twoPlayerGame.getPlayer1().getBoard(),twoPlayerGame.getPlayer2().getBoard());
                String attackcell=in.next();
                twoPlayerGame.getPlayer2().attackOpponentOnline(attackcell);;
                session.getBasicRemote().sendText(attackcell);
            } else {
                System.out.println("Вы не попали");
                System.out.printf( "Ход переходит к другому игроку \n");
                session.getBasicRemote().sendText("END_TURN");
                AbstractGame.printGame(twoPlayerGame.getPlayer1().getBoard(),twoPlayerGame.getPlayer2().getBoard());

            }
        }
        private void handleDefaultMessage(String message, Session session) throws IOException {
            AbstractGame.printGame(twoPlayerGame.getPlayer1().getBoard(),twoPlayerGame.getPlayer2().getBoard());
            if ("END_TURN".equals(message)) {
                System.out.printf("%s Не попал!\n", opponentName);
            }
            System.out.println("Ваш ход");
            System.out.println("default");
            String cellAttack=in.next();
            System.out.println(cellAttack);
            session.getBasicRemote().sendText(cellAttack);
        }
        @Override
        public void onOpen(Session session, EndpointConfig config) {

        }

        @Override
        public void onClose(Session session, CloseReason closeReason) {
            // Обработка закрытия соединения WebSocket
        }

        @Override
        public void onError(Session session, Throwable throwable) {
            // Обработка ошибок WebSocket
        }
        private void sendToOpponent(String message) {
            // Отправка сообщения оппоненту
            // Например:
            // opponentSession.getBasicRemote().sendText(message);
        }
            private void handleNameMessage(String message, Session session) throws IOException {
                opponentName = message.substring(5);
                System.out.println("handlename");
                session.getBasicRemote().sendText("NAME_" +twoPlayerGame.getPlayer1().getName() );
                System.out.printf("Ход второго игрока \n");
            }

    }


    package org.example.webSocket;

    import jakarta.websocket.*;
    import jakarta.websocket.server.ServerEndpoint;
    import org.example.Game.AbstractGame;
    import org.example.Game.TwoPlayerGame;
    import org.example.models.Player;
    import org.example.serverUtils.ServerUtils;
    import org.example.utils.Converter;
    import org.glassfish.tyrus.server.Server;

    import java.io.IOException;
    import java.net.URI;
    import java.util.Collections;
    import java.util.HashSet;
    import java.util.Scanner;
    import java.util.Set;

    @ServerEndpoint(value = "/battleship")
    public class WebSocketServerEndPoint  {

        private  Player player;
        private static String opponentName;
        private static volatile boolean isFinished = false;
        private static Scanner in;
        private String currentCell;
        private static TwoPlayerGame twoPlayerGame;


        public static void setTwoPlayerGame(TwoPlayerGame twoPlayerGame) {
            WebSocketServerEndPoint.twoPlayerGame = twoPlayerGame;
            in=new Scanner(System.in);

        }

        public static void startServer(int port,TwoPlayerGame twoPlayerGame) {
            setTwoPlayerGame(twoPlayerGame);
            Server server;
            server = new Server("localhost", port, "/websockets", null, WebSocketServerEndPoint.class);
            try {
                server.start();
                System.out.println("---Сервер запущен и ожидает игрока");
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
            if (message.length() < 50) {
                if (message.startsWith("NAME_")) {
                    System.out.println("Игрок успешно подключился");
                    opponentName = message.substring(5);
                } else if (message.equals("Lose")) {
                    handleLoseMessage(session);
                } else if (message.matches("^[A-P][1-9]$|^[A-P]1[0-6]$")) {
                    handleAttackMessage(message, session);
                } else if (message.startsWith("Result_")) {
                    switchTurn(message, session);
                } else {
                    handleDefaultMessage(message, session);
                }
            }
        }


        private void handleAttackMessage(String message, Session session) throws IOException {
            AbstractGame.printGame(twoPlayerGame.getPlayer1().getBoard(),twoPlayerGame.getPlayer2().getBoard());
            currentCell =message;
            System.out.printf("Ход второго игрока \n");
            System.out.printf( "Второй игрок атаковал  %s\n",  message);
            processAttackResult(currentCell, session);
        }
        private void processAttackResult(String cell, Session session) throws IOException {
            char result=twoPlayerGame.getPlayer1().attackOpponentOnline(cell);
            AbstractGame.printGame(twoPlayerGame.getPlayer1().getBoard(),twoPlayerGame.getPlayer2().getBoard());

            if (result=='O') {
                System.out.printf("%s Противник попал по вашему кораблю \n", opponentName);
            }
             else {
            System.out.printf("Противник не попопал в ваши корабли");

            }
            session.getBasicRemote().sendText("Result_"+result);

        }
        private void handleLoseMessage(Session session) {
            System.out.println("Выход из игры");
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Игра закончена. " + twoPlayerGame.getPlayer2().getName() + " Победил в игре."));
            } catch (IOException e) {
                throw new RuntimeException(e);
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
              if(currentCell!=null)  {
                  twoPlayerGame.getPlayer2().getBoard().updateCell(x-1,y-1,'X');}
                AbstractGame.printGame(twoPlayerGame.getPlayer1().getBoard(),twoPlayerGame.getPlayer2().getBoard());
                System.out.println("Попадание по кораблю, можете атаковать ещё раз");
                String attackcell= ServerUtils.readValidInput(in);
                currentCell=attackcell;
                session.getBasicRemote().sendText(attackcell);
            } else {
                if(currentCell!=null)  {twoPlayerGame.getPlayer2().getBoard().updateCell(x-1,y-1,'.');}
                AbstractGame.printGame(twoPlayerGame.getPlayer1().getBoard(),twoPlayerGame.getPlayer2().getBoard());
                System.out.println("Вы не попали");
                System.out.printf( "Ход переходит к другому игроку \n");
                session.getBasicRemote().sendText("END_TURN");


            }
        }
        private void handleDefaultMessage(String message, Session session) throws IOException {
            AbstractGame.printGame(twoPlayerGame.getPlayer1().getBoard(),twoPlayerGame.getPlayer2().getBoard());
            if ("END_TURN".equals(message)) {
                System.out.printf("%s Не попал!\n", opponentName);
            }
            System.out.println("Ваш ход");
            String cellAttack=ServerUtils.readValidInput(in);
            currentCell=cellAttack;
            session.getBasicRemote().sendText(cellAttack);
        }
        public void onOpen(Session session, EndpointConfig config) {

        }

       // @Override
        public void onClose(Session session, CloseReason closeReason) {
            System.out.printf("Сессия  %s была закрыта потому что  %s\n", session.getId(), closeReason);
            isFinished = true;
        }


            private void handleNameMessage(String message, Session session) throws IOException {
                opponentName = message.substring(5);
                session.getBasicRemote().sendText("NAME_" +twoPlayerGame.getPlayer1().getName() );
                System.out.printf("Ход второго игрока \n");
            }

    }


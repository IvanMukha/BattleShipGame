import org.example.Game.SinglePlayer;
import org.example.Game.TwoPlayerGame;


import java.net.URISyntaxException;
import java.util.Scanner;

public class SeaBattleGame {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        System.out.println("Введите имя игрока: ");
        String name=in.nextLine();
        System.out.println("Игра Морской бой");
        System.out.println("Выберете режим игры:");
        System.out.println("1. Игра с напарником.");
        System.out.println("2. Игра с компьютером.");

        while (true) {
            int gameMode = in.nextInt();
            if(gameMode==1){
                TwoPlayerGame twoPlayerGame=new TwoPlayerGame();
                twoPlayerGame.startGame(name);
                break;

            }else if(gameMode==2){
                SinglePlayer singlePlayer=new SinglePlayer();
                singlePlayer.startgame(name);
                break;
            }else System.out.println("Ошибка, повторите ввод");

        }
     }
}

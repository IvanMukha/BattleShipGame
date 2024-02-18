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















/*
6КЛX1 5КЛX2 4КЛX3 3КЛX4 2КЛX5 1КЛX6
B1 VERTICAL OR HORIZONTAL KOL-VO KLETOK
1-16A 1-16B
16*16=256 UNIQUE VALUE
ARRAY [][] 16X16
13I=
HASHMAP=1P=0 -korablya net
HASHMAP=2K=1 korabl est





    A   B   C   D   E   F   G   H   I   J   K   L   M   N   O   P
1
2   0   1   1   1   1   0   1   0
3   0   0   0   0   0   0   0   0
4
5
6
7
8
9
10
11
12
13
14
15
16
 */
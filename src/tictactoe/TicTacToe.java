package tictactoe;

import java.awt.Color;
import java.awt.event.MouseEvent;
import studijosKTU.ScreenKTU;
import java.util.Random;

//[y,x]
//
// 00 | 01 | 02 
//-------------- 
// 10 | 11 | 12 
//-------------- 
// 20 | 21 | 22
/**
 ** @author Deividas Patalauskas
 */
public class TicTacToe extends ScreenKTU {

    public static void main(String[] args) {
        new TicTacToe();
    }
    private static final char[] Symbols = new char[]{'✖', '◯'};
    private byte board[][];
    private static int Game = 0;
    private static int Player_Win = 0;
    private static int AI_Win = 0;
    private static String Won = "";
    private static boolean GameEnd = false;
    private static int AttackTactic;

    public TicTacToe() {
        super(60, 60, 7, 5, Grid.ON);
        CreatNewGame();
    }

    public void CreatNewGame() {
        fillRect(0, 0, 8, 5, Color.WHITE);
        fillRect(0, 0, 5, 5, Color.BLACK);
        fillRect(1, 1, 3, 3, Color.WHITE);
        setColors(Color.BLACK, Color.WHITE);
        print(6, 0, "AI: ");
        int a = 4;
        if (Game > 9) {
            a--;
        }
        print(0, a, Game);
        print(4, 2, ":");
        setColors(Color.BLACK, Color.RED);
        print(4, 0, Player_Win);
        setColors(Color.BLACK, Color.BLUE);
        int b = 4;
        if (AI_Win > 9) {
            b--;
        }
        print(4, b, AI_Win);
        setColors(Color.RED, Color.WHITE);
        print(5, 0, "Reset");
        if (level_0) {
            print(6, 4, "L");
        } else if (level_1) {
            print(6, 4, "V");
        } else if (level_2) {
            print(6, 4, "S");
        }
        board = new byte[3][3];
        GameEnd = false;
        Random random = new Random();
        AttackTactic = random.nextInt(99) % 2;
        if (Game % 2 == 1) {
            BotTurn();
        }
        refresh(1);
    }

    private static boolean level_0 = false; //Easy
    private static boolean level_1 = false; //Medium
    private static boolean level_2 = true;  //Imposible

    @Override
    public void mouseClicked(MouseEvent e) {
        int y = e.getY() / cellH;
        int x = e.getX() / cellW;
        setColors(Color.WHITE, Color.RED);
        boolean Base = CheckField(x, y);
        boolean Reset = CheckResetField(x, y);
        boolean Level = CheckChangeAIField(x, y);
        int suma = CheckTurns(1) + CheckTurns(2);
        if (Base && board[y - 1][x - 1] == 0 && !GameEnd) {
            print(y, x, Symbols[Game % 2]);
            board[y - 1][x - 1] = 1;
            CheckGameStatus();
            if (!GameEnd) {
                BotTurn();
                CheckGameStatus();
            }
        } else if (Reset) {
            CreatNewGame();
        } else if (Level && !GameEnd && suma == 0) {
            ChangeLevel();
        } else if (GameEnd) {
            CreatNewGame();
        }
        refresh(1);
    }

    public void ChangeLevel() {
        setColors(Color.RED, Color.WHITE);
        if (level_0) {
            level_0 = false;
            level_1 = true;
            level_2 = false;
            print(6, 4, "V");
        } else if (level_1) {
            level_0 = false;
            level_1 = false;
            level_2 = true;
            print(6, 4, "S");
        } else if (level_2) {
            level_0 = true;
            level_1 = false;
            level_2 = false;
            print(6, 4, "L");
        }
    }

    public boolean CheckField(int x, int y) {
        boolean check = false;
        if (x >= 1 && x <= 3 && y >= 1 && y <= 3) {
            check = true;
        }
        return check;
    }

    public boolean CheckResetField(int x, int y) {
        boolean check = false;
        if (x >= 0 && x <= 5 && y >= 5 && y <= 5) {
            check = true;
        }
        return check;
    }

    public boolean CheckChangeAIField(int x, int y) {
        boolean check = false;
        if (x >= 4 && x <= 4 && y >= 6 && y <= 6) {
            check = true;
        }
        return check;
    }

    public void BotTurn() {
        setColors(Color.WHITE, Color.BLUE);
        int turn_0 = CheckTurns(1); // player
        int turn_1 = CheckTurns(2); //bot
        if (level_2) {
            if (turn_0 > turn_1) {
                Defend(turn_0);
            } else {
                Attack(turn_1);
            }
        } else if (level_0) {
            PutRandom();
        } else if (level_1) {
            if (turn_0 == 0 && turn_1 == 0) {
                PutRandom();
            } else {
                CheckAllLines();
            }
        }
    }

    //Attack Tactic
    public void Attack(int turn_1) {
        boolean corner = CheckCorners_Enemy();
        if (AttackTactic == 0) { // corners
            if (turn_1 == 0) { // take corner
                TacticTurn_2();
            } else if (turn_1 == 1 && !corner) { //diagonal = false, take corner
                TacticTurn_4();
            } else if (turn_1 == 1 && corner) {//take center, diagonal = true 
                TacticTurn_0();
            } else if (turn_1 == 2) {//line
                TacticTurn_6();//buggy
            } else {
                CheckAllLines();
            }
        } else if (AttackTactic == 1) { // center
            if (turn_1 == 0) {
                TacticTurn_0();
            } else if (turn_1 == 1) {
                TacticTurn_5();
            } else if (turn_1 == 2) {//line
                TacticTurn_6();
            } else {
                CheckAllLines();
            }
        } else {
            CheckAllLines();
        }
    }
    //Defend Tactics
    private boolean CornerDeffend = false;
    private boolean CenterDeffend = false;
    private boolean StraightDeffend = false;

    public void Defend(int turn_0) {
        if (turn_0 == 1) {
            CornerDeffend = CheckCorners_Enemy();
            CenterDeffend = CheckCenter_Enemy();
            StraightDeffend = CheckStraight_Enemy();
        }

        if (CornerDeffend) {
            if (turn_0 == 1) {
                TacticTurn_0();
            } else if (turn_0 == 2) {
                TacticTurn_1();
            } else {
                CheckAllLines();
            }
        } else if (CenterDeffend) {
            if (turn_0 == 1) {
                TacticTurn_2();
            } else if (turn_0 == 2) {
                TacticTurn_3();
            } else {
                CheckAllLines();
            }
        } else if (StraightDeffend) {
            if (turn_0 == 1) {
                TacticTurn_0();
            } else if (turn_0 == 2) {
                TacticTurn_7();
            } else {
                CheckAllLines();
            }
        } else {
            CheckAllLines();
        }
    }

    public int CheckTurns(int x) {
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == x) {
                    count++;
                }
            }
        }
        return count;
    }

    public void PutRandom() {
        Random random_0 = new Random();
        Random random_1 = new Random();
        int x = random_0.nextInt(99) % 3;
        int y = random_1.nextInt(99) % 3;
        if (board[y][x] == 0) {
            board[y][x] = 2;
            print(y + 1, x + 1, Symbols[(Game + 1) % 2]);
            return;
        } else {
            PutRandom();
        }
    }

    //Advanced AI tactics
    public boolean CheckCorners_Enemy() {
        if (board[0][0] == 1) {
            return true;
        } else if (board[0][2] == 1) {
            return true;
        } else if (board[2][0] == 1) {
            return true;
        } else if (board[2][2] == 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean CheckCenter_Enemy() {
        if (board[1][1] == 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean CheckStraight_Enemy() {
        if (board[0][1] == 1) {
            return true;
        } else if (board[1][2] == 1) {
            return true;
        } else if (board[2][1] == 1) {
            return true;
        } else if (board[1][0] == 1) {
            return true;
        } else {
            return false;
        }
    }

    //Deffend/Attack Take a Center
    public void TacticTurn_0() {
        if (board[1][1] == 0) {
            print(1 + 1, 1 + 1, Symbols[(Game + 1) % 2]);
            board[1][1] = 2;
        } else {
            CheckAllLines();
        }
    }

    //Deffend Take straight diagonal line
    public void TacticTurn_1() {
        boolean check = CheckCorners_Enemies();
        Random random_0 = new Random();
        int x = random_0.nextInt(99) % 4;
        if (board[1][1] == 2 && check) {
            if (x == 0) {
                print(0 + 1, 1 + 1, Symbols[(Game + 1) % 2]);
                board[0][1] = 2;
            } else if (x == 1) {
                print(1 + 1, 2 + 1, Symbols[(Game + 1) % 2]);
                board[1][2] = 2;
            } else if (x == 2) {
                print(2 + 1, 1 + 1, Symbols[(Game + 1) % 2]);
                board[2][1] = 2;
            } else if (x == 3) {
                print(1 + 1, 0 + 1, Symbols[(Game + 1) % 2]);
                board[1][0] = 2;
            }
        } else {
            TacticTurn_7();
        }
    }

    //Deffend/Attack Take a Corner
    public void TacticTurn_2() {
        Random random_0 = new Random();
        int x = random_0.nextInt(99) % 4;
        if (x == 0) {
            print(0 + 1, 0 + 1, Symbols[(Game + 1) % 2]);
            board[0][0] = 2;
        } else if (x == 1) {
            print(0 + 1, 2 + 1, Symbols[(Game + 1) % 2]);
            board[0][2] = 2;
        } else if (x == 2) {
            print(2 + 1, 2 + 1, Symbols[(Game + 1) % 2]);
            board[2][2] = 2;
        } else if (x == 3) {
            print(2 + 1, 0 + 1, Symbols[(Game + 1) % 2]);
            board[2][0] = 2;
        } else {
            CheckAllLines();
        }
    }

    //Deffend anoter Corner
    public void TacticTurn_3() {
        Random random_0 = new Random();
        int x = random_0.nextInt(99) % 4;
        if (board[0][0] == 1 && board[2][2] == 2) {
            print(0 + 1, 2 + 1, Symbols[(Game + 1) % 2]);
            board[0][2] = 2;
        } else if (board[0][2] == 1 && board[2][0] == 2) {
            print(2 + 1, 2 + 1, Symbols[(Game + 1) % 2]);
            board[2][2] = 2;
        } else if (board[2][2] == 1 && board[0][0] == 2) {
            print(2 + 1, 0 + 1, Symbols[(Game + 1) % 2]);
            board[2][0] = 2;
        } else if (board[2][0] == 1 && board[0][2] == 2) {
            print(2 + 1, 2 + 1, Symbols[(Game + 1) % 2]);
            board[2][2] = 2;
        } else {
            CheckAllLines();
        }
    }

    //Attack Take another Corner
    public void TacticTurn_4() {
        if (board[2][2] == 2 && board[0][0] == 0) {
            print(0 + 1, 0 + 1, Symbols[(Game + 1) % 2]);
            board[0][0] = 2;
        } else if (board[2][0] == 2 && board[0][2] == 0) {
            print(0 + 1, 2 + 1, Symbols[(Game + 1) % 2]);
            board[0][2] = 2;
        } else if (board[0][2] == 2 && board[2][0] == 0) {
            print(2 + 1, 0 + 1, Symbols[(Game + 1) % 2]);
            board[2][0] = 2;
        } else if (board[0][0] == 2 && board[2][2] == 0) {
            print(2 + 1, 2 + 1, Symbols[(Game + 1) % 2]);
            board[2][2] = 2;
        } else {
            CheckAllLines();
        }
    }

    //Attack Take Corner
    public void TacticTurn_5() {
        boolean corner = CheckCorners_Enemy();
        if (corner) {
            if (board[2][2] == 1) {
                print(0 + 1, 0 + 1, Symbols[(Game + 1) % 2]);
                board[0][0] = 2;
            } else if (board[2][0] == 1) {
                print(0 + 1, 2 + 1, Symbols[(Game + 1) % 2]);
                board[0][2] = 2;
            } else if (board[0][2] == 1) {
                print(2 + 1, 0 + 1, Symbols[(Game + 1) % 2]);
                board[2][0] = 2;
            } else if (board[0][0] == 1) {
                print(2 + 1, 2 + 1, Symbols[(Game + 1) % 2]);
                board[2][2] = 2;
            } else {
                CheckAllLines();
            }
        } else {
            CheckAllLines();
        }
    }

    public void TacticTurn_6() {
        if (board[0][1] == 1 && board[2][0] == 1) {
            print(2 + 1, 2 + 1, Symbols[(Game + 1) % 2]);
            board[2][2] = 2;
        } else if (board[0][1] == 1 && board[2][2] == 1) {
            print(2 + 1, 0 + 1, Symbols[(Game + 1) % 2]);
            board[2][0] = 2;
        } else if (board[1][2] == 1 && board[0][0] == 1) {
            print(2 + 1, 0 + 1, Symbols[(Game + 1) % 2]);
            board[2][0] = 2;
        } else if (board[1][2] == 1 && board[2][0] == 1) {
            print(0 + 1, 0 + 1, Symbols[(Game + 1) % 2]);
            board[0][0] = 2;
        } else if (board[2][1] == 1 && board[0][0] == 1) {
            print(0 + 1, 2 + 1, Symbols[(Game + 1) % 2]);
            board[0][2] = 2;
        } else if (board[2][1] == 1 && board[0][2] == 1) {
            print(0 + 1, 0 + 1, Symbols[(Game + 1) % 2]);
            board[0][0] = 2;
        } else if (board[1][0] == 1 && board[0][2] == 1) {
            print(2 + 1, 2 + 1, Symbols[(Game + 1) % 2]);
            board[2][2] = 2;
        } else if (board[1][0] == 1 && board[2][2] == 1) {
            print(0 + 1, 2 + 1, Symbols[(Game + 1) % 2]);
            board[0][2] = 2;
        } else {
            CheckAllLines();
        }
    }

    public void TacticTurn_7() {
        if (board[2][1] == 1 && board[0][2] == 1) {
            print(2 + 1, 2 + 1, Symbols[(Game + 1) % 2]);
            board[2][2] = 2;
        } else if (board[2][1] == 1 && board[0][0] == 1) {
            print(2 + 1, 0 + 1, Symbols[(Game + 1) % 2]);
            board[2][0] = 2;
        } else {
            CheckAllLines();
        }
    }

    public boolean CheckCorners_Enemies() {
        if (board[0][0] == 1 && board[2][2] == 1) {
            return true;
        } else if (board[0][2] == 1 && board[2][0] == 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean CheckStraightLine_Enemy() {
        if (board[0][1] == 1 && board[2][0] == 1) {
            return true;
        } else if (board[0][1] == 1 && board[2][2] == 1) {
            return true;
        } else if (board[1][2] == 1 && board[0][0] == 1) {
            return true;
        } else if (board[1][2] == 1 && board[2][0] == 1) {
            return true;
        } else if (board[2][1] == 1 && board[0][0] == 1) {
            return true;
        } else if (board[2][1] == 1 && board[0][2] == 1) {
            return true;
        } else if (board[1][0] == 1 && board[0][2] == 1) {
            return true;
        } else if (board[1][0] == 1 && board[2][2] == 1) {
            return true;
        } else {
            return false;
        }
    }

    //Simple Tactics
    //Attack
    //Defend
    //Smart Move
    public void CheckAllLines() {
        int[] Lines = new int[8];
        // X Lines
        Lines[0] = CheckLine(board[0][0], board[0][1], board[0][2]);
        Lines[1] = CheckLine(board[1][0], board[1][1], board[1][2]);
        Lines[2] = CheckLine(board[2][0], board[2][1], board[2][2]);
        // Y Lines
        Lines[3] = CheckLine(board[0][0], board[1][0], board[2][0]);
        Lines[4] = CheckLine(board[0][1], board[1][1], board[2][1]);
        Lines[5] = CheckLine(board[0][2], board[1][2], board[2][2]);
        // Diagonals
        Lines[6] = CheckLine(board[0][0], board[1][1], board[2][2]);
        Lines[7] = CheckLine(board[0][2], board[1][1], board[2][0]);
        DoTactics(Lines);
    }

    public int CheckLine(int a, int b, int c) {
        int[] Line = new int[3];
        Line[0] = a;
        Line[1] = b;
        Line[2] = c;
        int Count_Player = 0;
        int Count_AI = 0;

        for (int i = 0; i < 3; i++) {
            if (Line[i] == 1) {
                Count_Player++;
            }
            if (Line[i] == 2) {
                Count_AI++;
            }
        }
        //Creating AI simple Tactics
        if (Count_AI == 2 && Count_Player == 0) {
            return 1; //Attack
        } else if (Count_AI == 0 && Count_Player == 2) {
            return 2; //Defend
        } else if (Count_AI == 1 && Count_Player == 0) {
            return 4; //Smart Move
        } else if (Count_AI == 0 && Count_Player == 1) {
            return 3; //Smart Move
        } else if (Count_AI == 1 && Count_Player == 1) {
            return 4; //Last Move
        } else {
            return 0;
        }
    }

    public void DoTactics(int[] Lines) {
        int FirstAction = 99;
        for (int j = 1; j < 5; j++) {
            for (int i = 0; i < 8; i++) {
                if (Lines[i] == j) {
                    FirstAction = i;
                    break;
                }
            }
            if (FirstAction != 99) {
                break;
            }
        }
        CurrectLine(FirstAction);
    }

    public void CurrectLine(int i) {
        if (i >= 0 && 2 >= i) {
            PutSymbol_XLine(i);
        } else if (i >= 3 && 5 >= i) {
            PutSymbol_YLines(i - 3);
        } else if (i == 6) {
            Diagonal_0();
        } else if (i == 7) {
            Diagonal_1();
        }
    }

    public void PutSymbol_XLine(int i) {
        for (int j = 0; j < 3; j++) {
            if (board[i][j] == 0) {
                print(i + 1, j + 1, Symbols[(Game + 1) % 2]);
                board[i][j] = 2;
                break;
            }
        }
    }

    public void PutSymbol_YLines(int i) {
        for (int j = 0; j < 3; j++) {
            if (board[j][i] == 0) {
                print(j + 1, i + 1, Symbols[(Game + 1) % 2]);
                board[j][i] = 2;
                break;
            }
        }
    }

    public void Diagonal_0() {
        for (int j = 0; j < 3; j++) {
            if (board[j][j] == 0) {
                print(j + 1, j + 1, Symbols[(Game + 1) % 2]);
                board[j][j] = 2;
                break;
            }
        }
    }

    public void Diagonal_1() {
        for (int i = 0, j = 2; i < 3; i++, j--) {
            if (board[i][j] == 0) {
                print(i + 1, j + 1, Symbols[(Game + 1) % 2]);
                board[i][j] = 2;
                break;
            }
        }
    }

    //Checking Game status after turn
    //Lose
    //Win
    //Tie
    public void CheckGameStatus() {
        int[] Lines = new int[8];
        boolean check = true;
        CheckTie();
        CheckWinLines(Lines);
        Won = Win(Lines);
        if (Won != "" || GameEnd) {
            if (Won != "") {
                if (Won == "You Win") {
                    Player_Win++;
                    GameEnd = true;
                }
                if (Won == "AI Wins") {
                    AI_Win++;
                    GameEnd = true;
                }
                Game++;
            } else if (GameEnd) {
                Game++;
            }
        }
    }

    public void CheckWinLines(int[] Lines) {
        // X Lines
        Lines[0] = WinLine(board[0][0], board[0][1], board[0][2]);
        Lines[1] = WinLine(board[1][0], board[1][1], board[1][2]);
        Lines[2] = WinLine(board[2][0], board[2][1], board[2][2]);
        // Y Lines
        Lines[3] = WinLine(board[0][0], board[1][0], board[2][0]);
        Lines[4] = WinLine(board[0][1], board[1][1], board[2][1]);
        Lines[5] = WinLine(board[0][2], board[1][2], board[2][2]);
        // Diagonals
        Lines[6] = WinLine(board[0][0], board[1][1], board[2][2]);
        Lines[7] = WinLine(board[0][2], board[1][1], board[2][0]);
    }

    public void CheckTie() {
        int Count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] != 0) {
                    Count++;
                }
            }
        }
        if (Count == 9) {
            GameEnd = true;
        }
    }

    public int WinLine(int a, int b, int c) {
        int[] Line = new int[3];
        Line[0] = a;
        Line[1] = b;
        Line[2] = c;
        int Count_Player = 0;
        int Count_AI = 0;

        for (int i = 0; i < 3; i++) {
            if (Line[i] == 1) {
                Count_Player++;
            }
            if (Line[i] == 2) {
                Count_AI++;
            }
        }
        if (Count_AI == 3 && Count_Player == 0) {
            return 1; //AI Wins
        } else if (Count_AI == 0 && Count_Player == 3) {
            return 2; //Player Wins
        } else {
            return 0;
        }
    }

    public String Win(int[] Lines) {
        String win = "";
        for (int i = 0; i < 8; i++) {
            if (Lines[i] == 1) {
                win = "AI Wins";
            }
            if (Lines[i] == 2) {
                win = "You Win";
            }
        }
        return win;
    }
}

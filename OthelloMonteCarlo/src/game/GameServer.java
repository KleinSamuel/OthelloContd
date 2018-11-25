package game;

import ai.AI_MCTS;
import ai.AI_Random;
import szte.mi.Move;
import szte.mi.Player;
import utils.Utils;

import java.util.Scanner;

//import old.OthelloAI2;

public class GameServer {

    private Board board;

    public GameServer(){
        initGame();
    }

    public void initGame(){
        this.board = new Board();
    }

    public void playGameCLI(){

        int currentPlayer = 1;

        while(board.isGameOver() == -1){

            System.out.println("###");

            PossibleMoves pMoves = new PossibleMoves(currentPlayer, board.BOARD_BLACK, board.BOARD_WHITE);

            Utils.printBoardWithMoves(board.BOARD_BLACK, board.BOARD_WHITE, pMoves.moves);
            System.out.println("Current Player: "+(currentPlayer==1 ? "BLACK" : "WHITE"));
            System.out.println(Utils.printLong(pMoves.moves));
            System.out.println("Possible Moves:");
            pMoves.printMoves();

            System.out.print("Enter your next move (x y): ");
            Scanner sc = new Scanner(System.in);
            String[] lineArray = sc.nextLine().split(" ");
            int x = Integer.parseInt(lineArray[0]);
            int y = Integer.parseInt(lineArray[1]);

            System.out.println("Entered Move: ["+x+","+y+"]");

            if(!board.makeMove(currentPlayer, x, y)){
                System.out.println("ILLEGAL MOVE!");
                continue;
            }

            currentPlayer = 3 - currentPlayer;
        }

        System.out.println("### GAME OVER ###");
        GameResult result = board.determineWinner();
        System.out.println(result.toString());

    }

    public GameResult playGameAI(Player p1, Player p2, long timePerGame, boolean quiet){

        initGame();

        int color = 1;
        Move prevMove = null;

        long time_player_1 = timePerGame;
        long time_player_2 = timePerGame;

        p1.init(0, time_player_1, null);
        p2.init(1, time_player_2, null);

        if(!quiet){
            System.out.println("## STARTED GAME ##");
            board.printCurrentBoard();
            System.out.println("## ##\n");
        }

        /* 0: game ended normally; 1: player1 timeout; 2: player2 timeout; 3: player1 illegal move; 4: player2 illegal move */
        int status = 0;

        while(board.isGameOver() == -1){

            if(color == 1){
                long start = System.currentTimeMillis();
                prevMove = p1.nextMove(prevMove, time_player_2, time_player_1);
                long timeNeeded = System.currentTimeMillis()-start;
                time_player_1 -= timeNeeded;

                if(time_player_1 < 0){
                    status = 1;
                    break;
                }

            }else{
                long start = System.currentTimeMillis();
                prevMove = p2.nextMove(prevMove, time_player_1, time_player_2);
                long timeNeeded = System.currentTimeMillis()-start;
                time_player_2 -= timeNeeded;

                if(time_player_2 < 0){
                    status = 2;
                    break;
                }
            }

            PossibleMoves serverMoves = new PossibleMoves(color, board.BOARD_BLACK, board.BOARD_WHITE);

            if(!quiet){
                System.out.println("## NEW TURN ##");
                System.out.println("Current Player:\t"+(color==1 ? "BLACK" : "WHITE"));
                System.out.println("Possible Moves:");
                serverMoves.printMoves();
                System.out.println("Selected Move:\t"+((prevMove != null) ? "["+prevMove.x+","+prevMove.y+"]" : "No Move Selected"));
            }


            if(prevMove != null){
                int movePos = Utils.moveToPosition(prevMove.x, prevMove.y);
                if(!serverMoves.results.containsKey(movePos)){
                    status = color==1 ? 3 : 4;
                    break;
                }else{
                    board.makeMove(color, prevMove.x, prevMove.y);
                }
            }else if(serverMoves.results.size() > 0){
                status = color==1 ? 3 : 4;
                break;
            }

            if(!quiet){
                System.out.println("## CURRENT BOARD STATE ##");
                board.printCurrentBoard();
                System.out.println("## END OF TURN ##");
            }

            color = 3 - color;
        }

        GameResult result = null;

        switch(status){
            case 0:
                result = board.determineWinner();
                break;
            case 1:
                result = new GameResult(2, -1,1);
                break;
            case 2:
                result = new GameResult(1, -1, 2);
                break;
            case 3:
                result = new GameResult(2, 1, -1);
                break;
            case 4:
                result = new GameResult(1, 2, -1);
                break;
        }

//        System.out.println("Remaining time for:\nPlayer1: " + time_player_1 + " ms\nPlayer2: " + time_player_2 + " ms");

        return result;

    }

    public void startGameSeries(Player p1, Player p2, int roundsPerSide, long timePerGame, boolean quiet){

        if(!quiet){
            System.out.println("### ### ### ### ### ### ### ### ### ### ### ### ###");
            System.out.println("###             Started Game Series             ###");
            System.out.println("### ### ### ### ### ### ### ### ### ### ### ### ###\n");

            System.out.println("## Settings ##");
            System.out.println("Rounds:\t\t\t\t" + (roundsPerSide * 2));
            System.out.println("Time per Game:\t\t" + timePerGame + " ms");
            System.out.println("## ## ## ## ##\n");

            System.out.println("## Started First Half <Player1=Black;Player2=White> ##");
        }

        int h1_black = 0, h2_black = 0;
        int h1_white = 0, h2_white = 0;
        int h1_draw = 0, h2_draw = 0;
        int current = 0;
        int total = roundsPerSide*2;
        int percent = 0;

        int black_illegalMoves = 0;
        int white_illegalMoves = 0;
        int black_timeouts = 0;
        int white_timeouts = 0;

        for (int i = 1; i <= roundsPerSide; i++) {
            GameResult result = playGameAI(p1, p2, timePerGame,true);
            if(result.winner == 1){
                h1_black++;
            }else if(result.winner == 2){
                h1_white++;
            }else{
                h1_draw++;
            }

            if(result.illegalMove == 1){
                black_illegalMoves++;
            }else if(result.illegalMove == 2){
                white_illegalMoves++;
            }

            if(result.timeout == 1){
                black_timeouts++;
            }else if(result.timeout == 2){
                white_timeouts++;
            }

            current++;
            int newPercent = (int)((current*1.0/total*1.0)*100);
            if(newPercent > percent) {
                if(!quiet) {
                    System.out.print("█");
                }
                percent = newPercent;
            }
        }

        if(!quiet) {
            System.out.println("\n## Finished First Half ##\n");

            System.out.println("## Started Second Half <Player1=White;Player2=Black> ##");
        }

        for (int i = 1; i <= roundsPerSide; i++) {
            GameResult result = playGameAI(p2, p1, timePerGame,true);
            if(result.winner == 1){
                h2_black++;
            }else if(result.winner == 2){
                h2_white++;
            }else{
                h2_draw++;
            }

            if(result.illegalMove == 2){
                black_illegalMoves++;
            }else if(result.illegalMove == 1){
                white_illegalMoves++;
            }

            if(result.timeout == 2){
                black_timeouts++;
            }else if(result.timeout == 1){
                white_timeouts++;
            }

            current++;
            int newPercent = (int)((current*1.0/total*1.0)*100);
            if(newPercent > percent){
                if(!quiet) {
                    System.out.print("█");
                }
                percent = newPercent;
            }
        }

        if(!quiet) {
            System.out.println("\n## Finished Second Half ##");
            System.out.println("\n");
        }

        double win_percent_p1 = (((h1_black+h2_white)*1.0)/(roundsPerSide*2.0))*100;
        double win_percent_p2 = (((h1_white+h2_black)*1.0)/(roundsPerSide*2.0))*100;
        double draw_percent = (((h1_draw+h2_draw)*1.0)/(roundsPerSide*2.0))*100;

        String win_percent_p1_string = String.format("%.2f", win_percent_p1);
        String win_percent_p2_string = String.format("%.2f", win_percent_p2);
        String draw_percent_string = String.format("%.2f", draw_percent);

        if(!quiet) {
            System.out.println("### ### ### ### ### ### ### ### ### ### ### ### ###");
            System.out.println("###                   Results                   ###");
            System.out.println("### ### ### ### ### ### ### ### ### ### ### ### ###\n");

            String titleTemplate = "%-10s %9s %9s %9s %11s %9s %9s %n";
            String template = "%-10s %9d %9d %9d %11s %9d %9d %n";
            System.out.printf(titleTemplate, "Player", "1/2", "2/2", "Total", "Percent", "Timeouts", "Illegal Moves");
            System.out.println();
            System.out.printf(template, "Player 1", h1_black, h2_white, (h1_black + h2_white), win_percent_p1_string + " %", black_timeouts, black_illegalMoves);
            System.out.printf(template, "Player 2", h1_white, h2_black, (h1_white + h2_black), win_percent_p2_string + " %", white_timeouts, white_illegalMoves);
            System.out.printf(template, "Draw", h1_draw, h2_draw, (h1_draw + h2_draw), draw_percent_string + " %", 0, 0);

            System.out.println("\nPlayer 1\t\t\t\t\t\t\t\t  Player 2");
            for (int i = 0; i < ((int) win_percent_p1); i += 2) {
                System.out.print("█");
            }
            for (int i = 0; i < ((int) draw_percent); i += 2) {
                System.out.print("▒");
            }
            for (int i = 0; i < ((int) win_percent_p2); i += 2) {
                System.out.print("▓");
            }
            System.out.println();
        }

    }

//    public void tweakParams(int roundsPerSide){
//
//        float start = 0f;
//        float end = 1f;
//        float step = 0.25f;
//
//        for (float w_1 = start; w_1 <= end; w_1+=step) {
//            for (float w_2 = start; w_2 <= end; w_2+=step) {
//                for (float w_3 = start; w_3 <= end; w_3+=step) {
//                    for (float w_4 = start; w_4 <= end; w_4+=step) {
//
//                        int wins = 0;
//
//                        Player a_1 = new AI_MinMax(3, new float[]{w_1, w_2, w_3, w_4});
//                        Player a_2 = new AI_Random();
//
//                        for (int i = 0; i < roundsPerSide; i++) {
//                            GameResult result = playGameAI(a_1, a_2, 20000, true);
//                            if(result.winner == 0){
//                                wins++;
//                            }
//                        }
//                        for (int i = 0; i < roundsPerSide; i++) {
//                            GameResult result = playGameAI(a_2, a_1, 20000, true);
//                            if(result.winner == 1){
//                                wins++;
//                            }
//                        }
//
//                        System.out.println(wins+"\tof\t"+(roundsPerSide*2)+"\t"+w_1+" "+w_2+" "+w_3+" "+w_4);
//
//                    }
//                }
//            }
//        }
//    }

    public void printQuiet(String s, boolean quiet){
        if(!quiet){
            System.out.println(s);
        }
    }

    public static long counter = 0;
    public static int turns = 0;

    public static void main(String[] args) throws InterruptedException {

        Integer rounds = 10;
        rounds = Integer.parseInt(args[0]);

        GameServer server = new GameServer();

        Player p_random = new AI_Random();
        Player p_random2 = new AI_Random();
        Player p_mcts = new AI_MCTS();

        server.startGameSeries(p_mcts, p_random, rounds, 4000, false);

//        GameResult result = server.playGameAI(p_random, p_mcts, 8000, false);

//        GameResult result = server.playGameAI(p_random, p_mcts,4000, false);
//        System.out.println(result);

//        System.out.println(counter);

    }

}

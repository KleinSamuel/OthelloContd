package armin.ai;

import szte.mi.Move;
import szte.mi.Player;
import utils.Coordinate;
import utils.Utils;
import version2.PossibleMoves;

import java.util.ArrayList;
import java.util.Random;

public class AI_Birinci implements Player {

    private boolean black;
    private Board board;
    private Random rand;

    @Override
    public void init(int order, long t, Random rnd) {
        this.black = (order == 0);
        this.board = new Board();
        this.rand = new Random();
    }

    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {

        updateChips(prevMove);

        PossibleMoves pMoves = new PossibleMoves(black, board.BOARD_BLACK, board.BOARD_WHITE);

        ArrayList<Integer> moveList = new ArrayList<>();
        moveList.addAll(pMoves.results.keySet());

        if(moveList.size() > 1) {
//            int selectedMove = moveList.get(rand.nextInt(moveList.size() - 1));
//            Coordinate coord = Utils.positionToCoordinate(selectedMove);
//            model.makeMove(black, coord.x, coord.y);
//            return new Move(coord.x, coord.y);

            //here need to get which position was played
            System.out.println("hello, calculating move now :)");
            System.out.println(black);

            MonteCarloTreeSearch mcts = new MonteCarloTreeSearch();
            Coordinate coord = calculateCoord(board, mcts.findNextMove(board, black));
            board.makeMove(black, coord.x, coord.y);
            return new Move(coord.x, coord.y);

        }else if(moveList.size() == 1){
            int selectedMove = moveList.get(0);
            Coordinate coord = Utils.positionToCoordinate(selectedMove);
            board.makeMove(black, coord.x, coord.y);
            return new Move(coord.x, coord.y);
        }else{
            return null;
        }
    }

    private Coordinate calculateCoord(Board current, Board next) {


        long x = (next.BOARD_BLACK ^ next.BOARD_WHITE) ^ (current.BOARD_BLACK ^ current.BOARD_WHITE);
        System.out.println("next");
        System.out.println(Long.toBinaryString(next.BOARD_BLACK));
        System.out.println(Long.toBinaryString(next.BOARD_WHITE));
        System.out.println("current");
        System.out.println(Long.toBinaryString(current.BOARD_BLACK));
        System.out.println(Long.toBinaryString(current.BOARD_WHITE));

        System.out.println("\nresult");
        System.out.println(Long.toBinaryString(x));
        int n = 0;
        while (((x >> 1) & 1) != 0) { n++; }
        System.out.println("bit found at " + n);

        return Utils.positionToCoordinate(n);
    }

    private void updateChips(Move enemyMove){
        if(enemyMove == null){
            return;
        }
        board.makeMove(!black, enemyMove.x, enemyMove.y);
    }
}

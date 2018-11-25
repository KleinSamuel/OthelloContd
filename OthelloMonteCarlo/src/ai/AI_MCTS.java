package ai;

import game.Board;
import game.GameServer;
import game.PossibleMoves;
import mct.MonteCarloTreeSearch;
import szte.mi.Move;
import szte.mi.Player;
import utils.Utils;

import java.util.ArrayList;
import java.util.Random;

public class AI_MCTS implements Player {

    private int color;
    private Board board;
    private Random rand;
    private int turns;

    @Override
    public void init(int order, long t, Random rnd) {
        this.color = order + 1;
        this.board = new Board();
        this.rand = new Random();
        this.turns=0;
    }

    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {
        turns+=1;
        updateChips(prevMove);

        PossibleMoves pMoves = new PossibleMoves(color, board.BOARD_BLACK, board.BOARD_WHITE);

        ArrayList<Integer> moveList = new ArrayList<>();
        moveList.addAll(pMoves.results.keySet());

//        System.out.println("\nmovesList size " + moveList.size());
//        moveList.forEach(x -> {
//            System.out.println("["+Utils.positionToMove(x).x + "," + Utils.positionToMove(x).y+"]");
//        });
//
//        board.printCurrentBoard();

        if(moveList.size() > 1) {
            MonteCarloTreeSearch mcts = new MonteCarloTreeSearch();
            mcts.setLevel(turns);
            Board res = mcts.findNextMove(board, color);

            if (res != null) {
                Move coord = calculateMove(board, res);

                board.makeMove(color, coord.x, coord.y);
                return new Move(coord.x, coord.y);
            } else {
                int selectedMove = moveList.get(rand.nextInt(moveList.size()));
                Move coord = Utils.positionToMove(selectedMove);
                board.makeMove(color, coord.x, coord.y);
                return new Move(coord.x, coord.y);
            }

        } else if(moveList.size() == 1){

//            System.out.println("\nCase2: >>>>>>>>>>>>>>>>>>>>>>>> this is very bad <<<<<<<<<<<<<<<<<<<<<\n\n");

            int selectedMove = moveList.get(0);
            Move coord = Utils.positionToMove(selectedMove);
            board.makeMove(color, coord.x, coord.y);
            return new Move(coord.x, coord.y);
        } else {
            return null;
        }
    }

    private Move calculateMove(Board current, Board next) {
//        long x = (next.BOARD_BLACK ^ next.BOARD_WHITE) ^ (current.BOARD_BLACK ^ current.BOARD_WHITE);
        long x =(next.BOARD_BLACK | next.BOARD_WHITE) ^ (current.BOARD_BLACK | current.BOARD_WHITE);

        int n = 0;

        for(int i =0; i<64;i++){
            if(((x>>i) & 1) == 1){
                n=i;
                break;
            }
        }

//        System.out.println(Utils.printLong(x));
//        System.out.println("bit found at " + n);
//
//        Coordinate coord = Utils.positionToCoordinate(63-n);
//
//        System.out.println(Utils.printLong(Utils.positionToLong(Utils.coordinateToPosition(coord))));

        return Utils.positionToMove(n);
    }

    private void updateChips(Move enemyMove){
        if(enemyMove == null){
            return;
        }
        board.makeMove(3 - color, enemyMove.x, enemyMove.y);
    }
}

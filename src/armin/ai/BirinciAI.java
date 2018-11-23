package armin.ai;

import szte.mi.Move;
import szte.mi.Player;
import utils.Coordinate;
import utils.Utils;
import version2.PossibleMoves;

import java.util.ArrayList;
import java.util.Random;

public class BirinciAI implements Player {

    private int black;
    private Board board;
    private Random rand;

    @Override
    public void init(int order, long t, Random rnd) {
        this.black = 3 - (order +1);
        this.board = new Board();
        this.rand = new Random();
    }

    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {

//        System.out.println(black);
//        System.out.println("new call of nextMove");
//        if(prevMove != null) System.out.println("lastMove is "+ prevMove.x + " " + prevMove.y);
//        board.printCurrentBoard();
        updateChips(prevMove);

//        board.printCurrentBoard();


        PossibleMoves pMoves = new PossibleMoves(3-black, board.BOARD_BLACK, board.BOARD_WHITE);

        ArrayList<Integer> moveList = new ArrayList<>();
        moveList.addAll(pMoves.results.keySet());

//        System.out.println("\nmovesList size " + moveList.size());
//        moveList.forEach(x -> {
//            System.out.println(Utils.positionToCoordinate(x));
//        });
//
//        board.printCurrentBoard();

        if(moveList.size() > 1) {
//            int selectedMove = moveList.get(rand.nextInt(moveList.size() - 1));
//            Coordinate coord = Utils.positionToCoordinate(selectedMove);
//            model.makeMove(black, coord.x, coord.y);
//            return new Move(coord.x, coord.y);

            //here need to get which position was played
//            System.out.println("hello, calculating move now :)");
//            System.out.println(black);

            MonteCarloTreeSearch mcts = new MonteCarloTreeSearch();
            Board res = mcts.findNextMove(board, black);
            if (res != null) {
                Coordinate coord = calculateCoord(board, res);

//                System.out.println(coord.x + " " + coord.y);
                if (board.makeMove(black, coord.x, coord.y, true)) {
//                    System.out.println("worked");
                } else {
//                    System.out.println("not worked");
                }

//                System.out.println("now move should be played on board!!");
//                board.printCurrentBoard();
                return new Move(coord.x, coord.y);
            } else {
                int selectedMove = moveList.get(rand.nextInt(moveList.size()));
                Coordinate coord = Utils.positionToCoordinate(selectedMove);
                board.makeMove(black, coord.x, coord.y, true);
                return new Move(coord.x, coord.y);
            }

        }else if(moveList.size() == 1){
            int selectedMove = moveList.get(0);
            Coordinate coord = Utils.positionToCoordinate(selectedMove);
            board.makeMove(black, coord.x, coord.y, true);
            return new Move(coord.x, coord.y);
        }else{
            return null;
        }
    }

    private Coordinate calculateCoord(Board current, Board next) {
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

        return Utils.positionToCoordinate(n);
    }

    private void updateChips(Move enemyMove){
        if(enemyMove == null){
            return;
        }
        board.makeMove(3-black, enemyMove.x, enemyMove.y, true);
    }
}

package version2;

import szte.mi.mi.Move;
import szte.mi.mi.Player;
import utils.Coordinate;
import utils.Utils;

import java.util.Random;

public class AI_MinMax implements Player {

    private boolean black;
    private Model model;

    private int depth;
    private float[] weights;

    private TranspositionTable transpositionTable;

    public AI_MinMax(){
        this(3);
    }

    public AI_MinMax(int depth){
        this(depth, new float[]{1f, 1f, 1f, 1f});
    }

    public AI_MinMax(int depth, float[] weights){
        this.depth = depth;
        this.weights = weights;
    }

    @Override
    public void init(int order, long t, Random rnd) {
        this.black = (order == 0);
        this.model = new Model();

        transpositionTable = new TranspositionTable();
    }

    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {

        updateChips(prevMove);

        int selectedMove = minmax(this.depth);

        if(selectedMove != -1){
            Coordinate coord = Utils.positionToCoordinate(selectedMove);
            model.makeMove(black, coord.x, coord.y);
            return new Move(coord.x, coord.y);
        }

        return null;
    }

    private void updateChips(Move enemyMove){
        if(enemyMove == null){
            return;
        }
        model.makeMove(!black, enemyMove.x, enemyMove.y);
    }

    private int minmax(int depth){

        PossibleMoves pMoves = new PossibleMoves(black, model.BOARD_BLACK, model.BOARD_WHITE);

        float score = -10000f;
        int selectedMove = -1;
        for(Integer move : pMoves.results.keySet()){

            long[] newBoard = MoveFactory.setChip(move, black, model.BOARD_BLACK, model.BOARD_WHITE);
            float tmpScore = _minmax(new BoardState(!black, newBoard[0], newBoard[1]), depth-1, false);

            if(tmpScore > score){
                score = tmpScore;
                selectedMove = move;
            }
        }

        return selectedMove;
    }

    private float _minmax(BoardState state, int depth, boolean maximizingPlayer){

        if(depth == 0 || state.isTerminalNode()){
            return state.getScore(transpositionTable, this.weights);
        }
        if(maximizingPlayer){
            float score = -10000f;
            PossibleMoves pMoves = new PossibleMoves(state.black, state.BOARD_BLACK, state.BOARD_WHITE);
            for(Integer move : pMoves.results.keySet()){
                long[] newBoard = MoveFactory.setChip(move, state.black, state.BOARD_BLACK, state.BOARD_WHITE);
                score = Float.max(score, _minmax(new BoardState(!state.black, newBoard[0], newBoard[1]), depth-1, false));
            }
            return score;
        }else{
            float score = 10000f;
            PossibleMoves pMoves = new PossibleMoves(state.black, state.BOARD_BLACK, state.BOARD_WHITE);
            for(Integer move : pMoves.results.keySet()){
                long[] newBoard = MoveFactory.setChip(move, state.black, state.BOARD_BLACK, state.BOARD_WHITE);
                score = Float.min(score, _minmax(new BoardState(!state.black, newBoard[0], newBoard[1]), depth-1, true));
            }
            return score;
        }
    }

    public static void main(String[] args) {

        AI_MinMax ai = new AI_MinMax();

        long BOARD_BLACK = 34628173824L;
        long BOARD_WHITE = 68853694464L;

        ai.init(0, 4000, null);

        ai.minmax(3);

    }

}

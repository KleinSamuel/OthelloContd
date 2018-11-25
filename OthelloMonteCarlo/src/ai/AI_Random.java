package ai;


import game.Board;
import game.PossibleMoves;
import szte.mi.Move;
import szte.mi.Player;
import utils.Utils;

import java.util.ArrayList;
import java.util.Random;

public class AI_Random implements Player {

    private int color;
    private Board board;
    private Random rand;

    @Override
    public void init(int order, long t, Random rnd) {
        this.color = order+1;
        this.board = new Board();
        this.rand = new Random();
    }

    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {

        updateChips(prevMove);

        PossibleMoves pMoves = new PossibleMoves(color, board.BOARD_BLACK, board.BOARD_WHITE);

        ArrayList<Integer> moveList = new ArrayList<>();
        moveList.addAll(pMoves.results.keySet());

        if(moveList.size() > 1) {
            int selectedMove = moveList.get(rand.nextInt(moveList.size()));
            Move coord = Utils.positionToMove(selectedMove);
            board.makeMove(color, coord.x, coord.y);
            return new Move(coord.x, coord.y);
        }else if(moveList.size() == 1){
            int selectedMove = moveList.get(0);
            Move coord = Utils.positionToMove(selectedMove);
            board.makeMove(color, coord.x, coord.y);
            return new Move(coord.x, coord.y);
        }else{
            return null;
        }
    }

    private void updateChips(Move enemyMove){
        if(enemyMove == null){
            return;
        }
        board.makeMove(3 - color, enemyMove.x, enemyMove.y);
    }

}

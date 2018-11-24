package version2;

import szte.mi.mi.Move;
import szte.mi.mi.Player;
import utils.Coordinate;
import utils.Utils;

import java.util.ArrayList;
import java.util.Random;

public class AI_Matrix implements Player {

    private boolean black;
    private Model model;
    private Random rand;

    private int[] posMatrix = new int[] {
            20, -3, 11,  8,  8, 11, -3, 20,
            -3, -7, -4,  1,  1, -4, -7, -3,
            11, -4,  2,  2,  2,  2, -4, 11,
            8,  1,  2, -3, -3,  2,  1,  8,
            8,  1,  2, -3, -3,  2,  1,  8,
            11, -4,  2,  2,  2,  2, -4, 11,
            -3, -7, -4,  1,  1, -4, -7, -3,
            20, -3, 11,  8,  8, 11, -3, 20 };

    @Override
    public void init(int order, long t, Random rnd) {
        this.black = (order == 0);
        this.model = new Model();
        this.rand = new Random();
    }

    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {

        updateChips(prevMove);

        PossibleMoves pMoves = new PossibleMoves(black, model.BOARD_BLACK, model.BOARD_WHITE);

        ArrayList<Integer> moveList = new ArrayList<>();
        moveList.addAll(pMoves.results.keySet());

        if(moveList.size() > 1) {

            int max = Integer.MIN_VALUE;
            int selectedMove = -1;
            for(Integer move : moveList){
                int matrixValue = posMatrix[63-move];
                if(matrixValue > max){
                    selectedMove = move;
                    max = matrixValue;
                }
            }

            Coordinate coord = Utils.positionToCoordinate(selectedMove);
            model.makeMove(black, coord.x, coord.y);
            return new Move(coord.x, coord.y);
        }else if(moveList.size() == 1){
            int selectedMove = moveList.get(0);
            Coordinate coord = Utils.positionToCoordinate(selectedMove);
            model.makeMove(black, coord.x, coord.y);
            return new Move(coord.x, coord.y);
        }else{
            return null;
        }
    }

    private void updateChips(Move enemyMove){
        if(enemyMove == null){
            return;
        }
        model.makeMove(!black, enemyMove.x, enemyMove.y);
    }

}

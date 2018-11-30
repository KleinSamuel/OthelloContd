package version2;

import szte.mi.Move;
import szte.mi.Player;
import util.Coordinate;
import util.Utils;

import java.util.ArrayList;
import java.util.Random;

public class AI_Greedy implements Player {

    private boolean black;
    private Model model;
    private Random rand;

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
            int max = 0;
            int selectedMove = -1;
            for(Integer m : moveList){
                int countToFlip = countToFLip(pMoves.results.get(m));
                if(countToFlip > max){
                    selectedMove = m;
                    max = countToFlip;
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

    private int countToFLip(long toFlip){
        int counter = 0;
        for(int i = 63; i >= 0; i--) {
            if(((toFlip >> i) & 1) == 1){
                counter++;
            }
        }
        return counter;
    }

    private void updateChips(Move enemyMove){
        if(enemyMove == null){
            return;
        }
        model.makeMove(!black, enemyMove.x, enemyMove.y);
    }

}

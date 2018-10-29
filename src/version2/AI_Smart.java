package version2;

import szte.mi.Move;
import szte.mi.Player;

import java.util.Random;

public class AI_Smart implements Player {

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

        return null;
    }

    private void updateChips(Move enemyMove){
        if(enemyMove == null){
            return;
        }
        model.makeMove(!black, enemyMove.x, enemyMove.y);
    }

}

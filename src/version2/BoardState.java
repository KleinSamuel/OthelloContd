package version2;

public class BoardState {

    int level;
    int hashCode;
    boolean black;
    long BOARD_BLACK;
    long BOARD_WHITE;

    public BoardState(boolean black, long BOARD_BLACK, long BOARD_WHITE){
        this.black = black;
        this.BOARD_BLACK = BOARD_BLACK;
        this.BOARD_WHITE = BOARD_WHITE;
        this.level = level();
        this.hashCode = hashCode();
    }

    public boolean isTerminalNode(){
        /* if board is full */
        if((BOARD_BLACK | BOARD_WHITE) == -1){
            return true;
        }
        /* if both players are out of moves */
        PossibleMoves pMoves = new PossibleMoves(black, BOARD_BLACK, BOARD_WHITE);
        if(pMoves.results.size() == 0){
            return true;
        }
        return false;
    }

    public float getScore(TranspositionTable table, float[] weights){
        /* fetch score from table if table is given and score is in table */
        if(table != null) {
            Float score = table.getScore(black, level, hashCode);
            if(score != null){
                return score;
            }
        }
        /* compute new score and store it to table */
        float score;
        if(weights == null){
            score = new Heuristic(black, BOARD_BLACK, BOARD_WHITE).getUnweightedScore();
        }else {
            score = new Heuristic(black, BOARD_BLACK, BOARD_WHITE).getWeightedScore(weights[0], weights[1], weights[2], weights[3]);
        }

        if(table != null) {
            table.addScore(black, level, hashCode, score);
        }
        return score;
    }

    public int level(){
        int amount = 0;
        for (int i = 63; i >= 0; i--) {
            if((((BOARD_BLACK | BOARD_WHITE) >> i) & 1) == 1){
                amount++;
            }
        }
        return amount;
    }

    public int hashCode(){
        return new long[]{BOARD_BLACK, BOARD_WHITE}.hashCode();
    }

}

package version2;

import utils.Utils;

public class Heuristic {

    private boolean black;
    private long blackChips;
    private long whiteChips;
    private long ownChips;
    private long otherChips;
    private PossibleMoves pMovesOwn, pMovesOther;

    public Heuristic(boolean black, long blackChips, long whiteChips){
        this.black = black;
        this.blackChips = blackChips;
        this.whiteChips = whiteChips;
        this.ownChips = black ? blackChips : whiteChips;
        this.otherChips = black ? whiteChips : blackChips;
        this.pMovesOwn = new PossibleMoves(black, blackChips, whiteChips);
        this.pMovesOther = new PossibleMoves(!black, blackChips, whiteChips);
    }

    public float getUnweightedScore(){
        return getScoreMoves() + getScoreAmountChips() + getScoreStable() + getScoreCorners();
    }

    public float getWeightedScore(float weight_moves, float weight_chips, float weight_stable, float weight_corner){
        return (weight_moves * getScoreMoves()) + (weight_chips * getScoreAmountChips()) + (weight_stable * getScoreStable() + (weight_corner * getScoreCorners()));
    }

    public float getScoreMoves(){
        int own = amountMoves(true);
        int other = amountMoves(false);
        if(own + other != 0){
            return (100f * (own - other) / (own + other));
        }else{
            return 0f;
        }
    }

    public int amountMoves(boolean own){
        return own ? pMovesOwn.results.size() : pMovesOther.results.size();
    }

    public float getScoreAmountChips(){
        int own = amountChips(true);
        int other = amountChips(false);
        return 100f * (own - other) / (own + other);
    }

    public int amountChips(boolean own){
        int amount = 0;
        for (int i = 63; i >= 0; i--) {
            if((((own ? ownChips : otherChips) >> i) & 1) == 1){
                amount++;
            }
        }
        return amount;
    }

    public float getScoreCorners(){
        int own = amountCorners(true);
        int other = amountCorners(false);
        if(own + other != 0){
            return (100f * (own - other) / (own + other));
        }else{
            return 0f;
        }
    }

    public int amountCorners(boolean own){
        int amount = 0;
        amount += ((((own ? ownChips : otherChips) >> 63) & 1) == 1) ? 1 : 0;
        amount += ((((own ? ownChips : otherChips) >> 56) & 1) == 1) ? 1 : 0;
        amount += ((((own ? ownChips : otherChips) >> 7) & 1) == 1) ? 1 : 0;
        amount += ((((own ? ownChips : otherChips) >> 0) & 1) == 1) ? 1 : 0;
        return amount;
    }

    public float getScoreStable(){
        int own = stableChips(true);
        int other = stableChips(false);
        if(own + other != 0){
            return (100f * (own - other) / (own + other));
        }else{
            return 0f;
        }
    }

    public int stableChips(boolean own){

        long stable = 0L;

        /* check bottom left corner */
        int row = 7;
        int col = 0;
        int maxRow = 0;
        int maxCol = 7;
        for(int i = 0; i < 4; i++) {
            for (int j = 0; j <= 7-(2*i); j++) {
                int row_1 = row-i;
                int col_1 = col+i+j;
                if(col_1 >= maxCol || row_1 <= maxRow){
                    continue;
                }
                int pos = Utils.coordinateToPosition(row_1, col_1);
                if((((own ? ownChips : otherChips) >> pos) & 1) != 1){
                    maxCol = col_1;
                    continue;
                }
                stable = stable | (1L << pos);
            }
            for (int j = 0; j <= 7-(2*i); j++) {
                int row_1 = row-i-j;
                int col_1 = col+i;
                if(col_1 >= maxCol || row_1 <= maxRow){
                    continue;
                }
                int pos = Utils.coordinateToPosition(row_1, col_1);
                if((((own ? ownChips : otherChips) >> pos) & 1) != 1){
                    maxRow = row_1;
                    continue;
                }
                stable = stable | (1L << pos);
            }
        }

        /* check bottom right corner */
        row = 7;
        col = 7;
        maxRow = 0;
        maxCol = 0;
        for(int i = 0; i < 4; i++) {
            for (int j = 0; j <= 7-(2*i); j++) {
                int row_1 = row-i;
                int col_1 = col-i-j;
                if(col_1 <= maxCol || row_1 <= maxRow){
                    continue;
                }
                int pos = Utils.coordinateToPosition(row_1, col_1);
                if((((own ? ownChips : otherChips) >> pos) & 1) != 1){
                    maxCol = col_1;
                    continue;
                }
                stable = stable | (1L << pos);
            }
            for (int j = 0; j <= 7-(2*i); j++) {
                int row_1 = row-i-j;
                int col_1 = col-i;
                if(col_1 <= maxCol || row_1 <= maxRow){
                    continue;
                }
                int pos = Utils.coordinateToPosition(row_1, col_1);
                if((((own ? ownChips : otherChips) >> pos) & 1) != 1){
                    maxRow = row_1;
                    continue;
                }
                stable = stable | (1L << pos);
            }
        }

        /* check top left corner */
        row = 0;
        col = 0;
        maxRow = 7;
        maxCol = 7;
        for(int i = 0; i < 4; i++) {
            for (int j = 0; j <= 7-(2*i); j++) {
                int row_1 = row+i;
                int col_1 = col+i+j;
                if(col_1 >= maxCol || row_1 >= maxRow){
                    continue;
                }
                int pos = Utils.coordinateToPosition(row_1, col_1);
                if((((own ? ownChips : otherChips) >> pos) & 1) != 1){
                    maxCol = col_1;
                    continue;
                }
                stable = stable | (1L << pos);
            }
            for (int j = 0; j <= 7-(2*i); j++) {
                int row_1 = row+i+j;
                int col_1 = col+i;
                if(col_1 <= maxCol || row_1 <= maxRow){
                    continue;
                }
                int pos = Utils.coordinateToPosition(row_1, col_1);
                if((((own ? ownChips : otherChips) >> pos) & 1) != 1){
                    maxRow = row_1;
                    continue;
                }
                stable = stable | (1L << pos);
            }
        }

        /* check top right corner */
        row = 0;
        col = 7;
        maxRow = 7;
        maxCol = 0;
        for(int i = 0; i < 4; i++) {
            for (int j = 0; j <= 7-(2*i); j++) {
                int row_1 = row+i;
                int col_1 = col-i-j;
                if(col_1 <= maxCol || row_1 >= maxRow){
                    continue;
                }
                int pos = Utils.coordinateToPosition(row_1, col_1);
                if((((own ? ownChips : otherChips) >> pos) & 1) != 1){
                    maxCol = col_1;
                    continue;
                }
                stable = stable | (1L << pos);
            }
            for (int j = 0; j <= 7-(2*i); j++) {
                int row_1 = row+i+j;
                int col_1 = col-i;
                if(col_1 <= maxCol || row_1 >= maxRow){
                    continue;
                }
                int pos = Utils.coordinateToPosition(row_1, col_1);
                if((((own ? ownChips : otherChips) >> pos) & 1) != 1){
                    maxRow = row_1;
                    continue;
                }
                stable = stable | (1L << pos);
            }
        }

        int amount = 0;
        for(int i = 63; i >= 0; i--) {
            if(((stable >> i) & 1) == 1){
                amount++;
            }
        }
        return amount;
    }

}

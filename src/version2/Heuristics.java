package version2;

import utils.Utils;

public class Heuristics {

    private boolean black;
    private long blackChips;
    private long whiteChips;
    private long ownChips;
    private long otherChips;
    private PossibleMoves pMoves;

    public Heuristics(boolean black, long blackChips, long whiteChips){
        this.black = black;
        this.blackChips = blackChips;
        this.whiteChips = whiteChips;
        this.ownChips = black ? blackChips : whiteChips;
        this.otherChips = black ? whiteChips : blackChips;
        this.pMoves = new PossibleMoves(black, blackChips, whiteChips);
    }

    public int amountMoves(){
        return pMoves.results.size();
    }

    public int amountChips(){
        int amount = 0;
        for (int i = 63; i >= 0; i--) {
            if(((ownChips >> i) & 1) == 1){
                amount++;
            }
        }
        return amount;
    }

    public int stableChips(){

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
                if(((ownChips >> pos) & 1) != 1){
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
                if(((ownChips >> pos) & 1) != 1){
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
                if(((ownChips >> pos) & 1) != 1){
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
                if(((ownChips >> pos) & 1) != 1){
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
                if(((ownChips >> pos) & 1) != 1){
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
                if(((ownChips >> pos) & 1) != 1){
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
                if(((ownChips >> pos) & 1) != 1){
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
                if(((ownChips >> pos) & 1) != 1){
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

    private boolean isStable_W(int pos){
        boolean isStable = true;
        /* is stable if chip is on the right */
        if((pos%8) == 7){
            return isStable;
        }
        /* check if every chip to the left is own chip */
        pos += 1;
        while((pos%8) > 0){
            if(((ownChips >> pos) & 1) != 1){
                isStable = false;
                break;
            }
            pos += 1;
        }
        return isStable;
    }

    private boolean isStable_E(int pos){
        boolean isStable = true;
        /* is stable if chip is on the right */
        if((pos%8) == 0){
            return isStable;
        }
        /* check if every chip to the right is own chip */
        pos -= 1;
        while((pos%8) < 7){
            if(((ownChips >> pos) & 1) != 1){
                isStable = false;
                break;
            }
            pos -= 1;
        }
        return isStable;
    }

    private boolean isStable_N(int pos){
        boolean isStable = true;
        /* is stable if chip is on the top */
        if(pos > 55){
            return isStable;
        }
        /* check if every chip to the top is own chip */
        pos += 8;
        while(pos < 64){
            if(((ownChips >> pos) & 1) != 1){
                isStable = false;
                break;
            }
            pos += 8;
        }
        return isStable;
    }

    private boolean isStable_S(int pos){
        boolean isStable = true;
        /* is stable if chip is on the bottom */
        if(pos < 8){
            return isStable;
        }
        /* check if every chip to the bottom is own chip */
        pos -= 8;
        while(pos >= 0){
            if(((ownChips >> pos) & 1) != 1){
                isStable = false;
                break;
            }
            pos -= 8;
        }
        return isStable;
    }

    private boolean isStable_NE(int pos){
        boolean isStable = true;
        /* is stable if chip is on the top or on the right*/
        if(pos > 55 || (pos%8) == 0){
            return isStable;
        }
        /* check if every chip to the top-right is own chip */
        pos += 7;
        while(pos < 64 && (pos%8) < 7){
            if(((ownChips >> pos) & 1) != 1){
                isStable = false;
                break;
            }
            pos += 7;
        }
        return isStable;
    }

    private boolean isStable_NW(int pos){
        boolean isStable = true;
        /* is stable if chip is on the top or on the right*/
        if(pos > 55 || (pos%8) == 7){
            return isStable;
        }
        /* check if every chip to the top-right is own chip */
        pos += 9;
        while(pos < 64 && (pos%8) > 0){
            if(((ownChips >> pos) & 1) != 1){
                isStable = false;
                break;
            }
            pos += 9;
        }
        return isStable;
    }

    private boolean isStable_SE(int pos){
        boolean isStable = true;
        /* is stable if chip is on the top or on the right*/
        if(pos < 8 || (pos%8) == 0){
            return isStable;
        }
        /* check if every chip to the top-right is own chip */
        pos -= 9;
        while(pos >= 0 && (pos%8) < 7){
            if(((ownChips >> pos) & 1) != 1){
                isStable = false;
                break;
            }
            pos -= 9;
        }
        return isStable;
    }

    private boolean isStable_SW(int pos){
        boolean isStable = true;
        /* is stable if chip is on the top or on the right*/
        if(pos < 8 || (pos%8) == 7){
            return isStable;
        }
        /* check if every chip to the top-right is own chip */
        pos -= 7;
        while(pos >= 0 && (pos%8) > 0){
            if(((ownChips >> pos) & 1) != 1){
                isStable = false;
                break;
            }
            pos -= 7;
        }
        return isStable;
    }

}

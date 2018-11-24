package game;

import szte.mi.Move;
import utils.Utils;

import java.util.HashMap;

public class PossibleMoves {

    public long moves;
    public HashMap<Integer, Long> results;

    public PossibleMoves(boolean playerBlack, long black, long white){
        this.moves = 0L;
        this.results = new HashMap<>();
        compute(playerBlack, black, white);
    }

    public PossibleMoves(int playerBlack, long black, long white){
        this.moves = 0L;
        this.results = new HashMap<>();
        if(playerBlack == 1) {
            compute(true, black, white);
        } else {
            compute(false, black, white);
        }
    }

    public void compute(boolean playerBlack, long black, long white){
        long ownChips = black;
        long otherChips = white;
        if(!playerBlack){
            ownChips = white;
            otherChips = black;
        }

        for (int i = 63; i >= 0; i--) {
            if(((ownChips >> i) & 1) == 1){
                addResult(check_N(i, ownChips, otherChips));
                addResult(check_S(i, ownChips, otherChips));
                addResult(check_E(i, ownChips, otherChips));
                addResult(check_W(i, ownChips, otherChips));
                addResult(check_NE(i, ownChips, otherChips));
                addResult(check_NW(i, ownChips, otherChips));
                addResult(check_SE(i, ownChips, otherChips));
                addResult(check_SW(i, ownChips, otherChips));
            }
        }
    }

    public void printMoves(){
        for(Integer i : results.keySet()){
            Move c = Utils.positionToMove(i);
            System.out.println("["+c.x+","+c.y+"]");
        }
    }

    public void addResult(PositionCheckResult result){
        if(result != null){
            /* move already in set -> merge chips to flip */
            if(this.results.containsKey(result.pos)){
                long merged = this.results.get(result.pos) | result.toFlip;
                this.results.put(result.pos, merged);
            }
            /* new move */
            else{
                this.results.put(result.pos, result.toFlip);
                this.moves = this.moves ^ (1L << result.pos);
            }
        }
    }

    private PositionCheckResult check_N(int pos, long ownChips, long otherChips){

        /* cant be in top two rows and next chip must be other */
        if(pos > 47 || ((otherChips >> (pos+8)) & 1) != 1){
            return null;
        }

        /* save next chip as flippable */
        long toFlip = 1L << (pos+8);

        /* next position of interest is two positions away from initial position */
        pos += 16;

        while(pos < 64){
            /* break if next chip is own chip */
            if(((ownChips >> pos) & 1) == 1){
                return null;
            }
            /* return if next field is empty */
            else if(((otherChips >> pos) & 1) != 1){
                return new PositionCheckResult(pos, toFlip);
            }
            /* continue if next chip is other chip */
            else{
                toFlip = toFlip | (1L << pos);
                pos += 8;
            }
        }

        return null;
    }

    private PositionCheckResult check_S(int pos, long ownChips, long otherChips){

        /* cant be in bottom two rows and next chip must be other */
        if(pos < 16 || ((otherChips >> (pos-8)) & 1) != 1){
            return null;
        }

        /* save next chip as flippable */
        long toFlip = 1L << (pos-8);

        /* next position of interest is two positions away from initial position */
        pos -= 16;

        while(pos >= 0){
            /* break if next chip is own chip */
            if(((ownChips >> pos) & 1) == 1){
                return null;
            }
            /* return if next field is empty */
            else if(((otherChips >> pos) & 1) != 1){
                return new PositionCheckResult(pos, toFlip);
            }
            /* continue if next chip is other chip */
            else{
                toFlip = toFlip | (1L << pos);
                pos -= 8;
            }
        }

        return null;
    }

    private PositionCheckResult check_E(int pos, long ownChips, long otherChips){

        /* cant be in right two colums and next chip must be other */
        if((pos%8) < 2 || ((otherChips >> (pos-1)) & 1) != 1){
            return null;
        }

        /* save next chip as flippable */
        long toFlip = 1L << (pos-1);

        /* next position of interest is two positions away from initial position */
        pos -= 2;

        while((pos%8) < 7 && pos >= 0){
            /* break if next chip is own chip */
            if(((ownChips >> pos) & 1) == 1){
                return null;
            }
            /* return if next field is empty */
            else if(((otherChips >> pos) & 1) != 1){
                return new PositionCheckResult(pos, toFlip);
            }
            /* continue if next chip is other chip */
            else{
                toFlip = toFlip | (1L << pos);
                pos -= 1;
            }
        }

        return null;
    }

    private PositionCheckResult check_W(int pos, long ownChips, long otherChips){

        /* cant be in left two colums and next chip must be other */
        if((pos%8) > 5 || ((otherChips >> (pos+1)) & 1) != 1){
            return null;
        }

        /* save next chip as flippable */
        long toFlip = 1L << (pos+1);

        /* next position of interest is two positions away from initial position */
        pos += 2;

        while((pos%8) > 0 && pos < 64){
            /* break if next chip is own chip */
            if(((ownChips >> pos) & 1) == 1){
                return null;
            }
            /* return if next field is empty */
            else if(((otherChips >> pos) & 1) != 1){
                return new PositionCheckResult(pos, toFlip);
            }
            /* continue if next chip is other chip */
            else{
                toFlip = toFlip | (1L << pos);
                pos += 1;
            }
        }

        return null;
    }

    private PositionCheckResult check_NE(int pos, long ownChips, long otherChips){

        /* cant be in top or right two colums and next chip must be other */
        if(pos > 47 || (pos%8) < 2 || ((otherChips >> (pos+7)) & 1) != 1){
            return null;
        }

        /* save next chip as flippable */
        long toFlip = 1L << (pos+7);

        /* next position of interest is two positions away from initial position */
        pos += 14;

        while(pos < 64 && (pos%8) < 7){
            /* break if next chip is own chip */
            if(((ownChips >> pos) & 1) == 1){
                return null;
            }
            /* return if next field is empty */
            else if(((otherChips >> pos) & 1) != 1){
                return new PositionCheckResult(pos, toFlip);
            }
            /* continue if next chip is other chip */
            else{
                toFlip = toFlip | (1L << pos);
                pos += 7;
            }
        }

        return null;
    }

    private PositionCheckResult check_NW(int pos, long ownChips, long otherChips){

        /* cant be in top or left two colums and next chip must be other */
        if(pos > 47 || (pos%8) > 5 || ((otherChips >> (pos+9)) & 1) != 1){
            return null;
        }

        /* save next chip as flippable */
        long toFlip = 1L << (pos+9);

        /* next position of interest is two positions away from initial position */
        pos += 18;

        while(pos < 64 && (pos%8) > 0){
            /* break if next chip is own chip */
            if(((ownChips >> pos) & 1) == 1){
                return null;
            }
            /* return if next field is empty */
            else if(((otherChips >> pos) & 1) != 1){
                return new PositionCheckResult(pos, toFlip);
            }
            /* continue if next chip is other chip */
            else{
                toFlip = toFlip | (1L << pos);
                pos += 9;
            }
        }

        return null;
    }

    private PositionCheckResult check_SE(int pos, long ownChips, long otherChips){

        /* cant be in bottom or right two colums and next chip must be other */
        if(pos < 16 || (pos%8) < 2 || ((otherChips >> (pos-9)) & 1) != 1){
            return null;
        }

        /* save next chip as flippable */
        long toFlip = 1L << (pos-9);

        /* next position of interest is two positions away from initial position */
        pos -= 18;

        while(pos >= 0 && (pos%8) < 7){
            /* break if next chip is own chip */
            if(((ownChips >> pos) & 1) == 1){
                return null;
            }
            /* return if next field is empty */
            else if(((otherChips >> pos) & 1) != 1){
                return new PositionCheckResult(pos, toFlip);
            }
            /* continue if next chip is other chip */
            else{
                toFlip = toFlip | (1L << pos);
                pos -= 9;
            }
        }

        return null;
    }

    private PositionCheckResult check_SW(int pos, long ownChips, long otherChips){

        /* cant be in bottom or left two colums and next chip must be other */
        if(pos < 16 || (pos%8) > 5 || ((otherChips >> (pos-7)) & 1) != 1){
            return null;
        }

        /* save next chip as flippable */
        long toFlip = 1L << (pos-7);

        /* next position of interest is two positions away from initial position */
        pos -= 14;

        while(pos >= 0 && (pos%8) > 0){
            /* break if next chip is own chip */
            if(((ownChips >> pos) & 1) == 1){
                return null;
            }
            /* return if next field is empty */
            else if(((otherChips >> pos) & 1) != 1){
                return new PositionCheckResult(pos, toFlip);
            }
            /* continue if next chip is other chip */
            else{
                toFlip = toFlip | (1L << pos);
                pos -= 7;
            }
        }
        return null;
    }
}

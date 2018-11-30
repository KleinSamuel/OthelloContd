package version2;

public class MoveFactory {

    /**
     * Get all chip positions that are flipped when chip is set
     * at given position.
     *
     * @param pos
     * @param ownChips
     * @param otherChips
     * @return
     */
    public static long getChipsToFlip(int pos, long ownChips, long otherChips){
        long toFlip = 0L;
        PositionCheckResult result_N = checkToFlip_N(pos, ownChips, otherChips);
        if(result_N != null){
            toFlip = toFlip | result_N.toFlip;
        }
        PositionCheckResult result_S = checkToFlip_S(pos, ownChips, otherChips);
        if(result_S != null){
            toFlip = toFlip | result_S.toFlip;
        }
        PositionCheckResult result_E = checkToFlip_E(pos, ownChips, otherChips);
        if(result_E != null){
            toFlip = toFlip | result_E.toFlip;
        }
        PositionCheckResult result_W = checkToFlip_W(pos, ownChips, otherChips);
        if(result_W != null){
            toFlip = toFlip | result_W.toFlip;
        }
        PositionCheckResult result_NE = checkToFlip_NE(pos, ownChips, otherChips);
        if(result_NE != null){
            toFlip = toFlip | result_NE.toFlip;
        }
        PositionCheckResult result_NW = checkToFlip_NW(pos, ownChips, otherChips);
        if(result_NW != null){
            toFlip = toFlip | result_NW.toFlip;
        }
        PositionCheckResult result_SE = checkToFlip_SE(pos, ownChips, otherChips);
        if(result_SE != null){
            toFlip = toFlip | result_SE.toFlip;
        }
        PositionCheckResult result_SW = checkToFlip_SW(pos, ownChips, otherChips);
        if(result_SW != null){
            toFlip = toFlip | result_SW.toFlip;
        }
        return toFlip;
    }

    /**
     * Return own and other chips after a given chip is set
     *
     * @param pos
     * @param black
     * @param BOARD_BLACK
     * @param BOARD_WHITE
     * @return
     */
    public static long[] setChip(int pos, boolean black, long BOARD_BLACK, long BOARD_WHITE){

        long toFlip = getChipsToFlip(pos, BOARD_BLACK, BOARD_WHITE);

        long own = BOARD_BLACK | (1L << pos) | toFlip;
        long other = BOARD_WHITE ^ toFlip;

        return new long[]{own, other};
    }

    /**
     * Determine enemy chips to flip when a chip is set on given position.
     * Direction: North
     * Can only be called on a true position, no error handling when called
     * with a wrong position!
     *
     * @param pos
     * @param ownChips
     * @param otherChips
     * @return
     */
    private static PositionCheckResult checkToFlip_N(int pos, long ownChips, long otherChips){
        /* save next chip as flippable */
        long toFlip = 1L << (pos+8);
        /* next position of interest is two positions away from initial position */
        pos += 16;
        while(pos < 64){
            /* return if next field is own chip */
            if(((ownChips >> pos) & 1) == 1){
                return new PositionCheckResult(pos, toFlip);
            }
            /* break if next field is empty */
            else if(((otherChips >> pos) & 1) != 1){
                return null;
            }
            /* continue if next chip is other chip */
            else{
                toFlip = toFlip | (1L << pos);
                pos += 8;
            }
        }
        return null;
    }

    /**
     * Determine enemy chips to flip when a chip is set on given position.
     * Direction: South
     * Can only be called on a true position, no error handling when called
     * with a wrong position!
     *
     * @param pos
     * @param ownChips
     * @param otherChips
     * @return
     */
    private static PositionCheckResult checkToFlip_S(int pos, long ownChips, long otherChips){
        /* save next chip as flippable */
        long toFlip = 1L << (pos-8);
        /* next position of interest is two positions away from initial position */
        pos -= 16;
        while(pos >= 0){
            /* return if next field is own chip */
            if(((ownChips >> pos) & 1) == 1){
                return new PositionCheckResult(pos, toFlip);
            }
            /* break if next field is empty */
            else if(((otherChips >> pos) & 1) != 1){
                return null;
            }
            /* continue if next chip is other chip */
            else{
                toFlip = toFlip | (1L << pos);
                pos -= 8;
            }
        }
        return null;
    }

    /**
     * Determine enemy chips to flip when a chip is set on given position.
     * Direction: East
     * Can only be called on a true position, no error handling when called
     * with a wrong position!
     *
     * @param pos
     * @param ownChips
     * @param otherChips
     * @return
     */
    private static PositionCheckResult checkToFlip_E(int pos, long ownChips, long otherChips){
        /* save next chip as flippable */
        long toFlip = 1L << (pos-1);
        /* next position of interest is two positions away from initial position */
        pos -= 2;
        while((pos%8) < 7 && pos >= 0){
            /* return if next field is own chip */
            if(((ownChips >> pos) & 1) == 1){
                return new PositionCheckResult(pos, toFlip);
            }
            /* break if next field is empty */
            else if(((otherChips >> pos) & 1) != 1){
                return null;
            }
            /* continue if next chip is other chip */
            else{
                toFlip = toFlip | (1L << pos);
                pos -= 1;
            }
        }
        return null;
    }

    /**
     * Determine enemy chips to flip when a chip is set on given position.
     * Direction: West
     * Can only be called on a true position, no error handling when called
     * with a wrong position!
     *
     * @param pos
     * @param ownChips
     * @param otherChips
     * @return
     */
    private static PositionCheckResult checkToFlip_W(int pos, long ownChips, long otherChips){
        /* save next chip as flippable */
        long toFlip = 1L << (pos+1);
        /* next position of interest is two positions away from initial position */
        pos += 2;
        while((pos%8) > 0 && pos < 64){
            /* return if next field is own chip */
            if(((ownChips >> pos) & 1) == 1){
                return new PositionCheckResult(pos, toFlip);
            }
            /* break if next field is empty */
            else if(((otherChips >> pos) & 1) != 1){
                return null;
            }
            /* continue if next chip is other chip */
            else{
                toFlip = toFlip | (1L << pos);
                pos += 1;
            }
        }
        return null;
    }

    /**
     * Determine enemy chips to flip when a chip is set on given position.
     * Direction: North-East
     * Can only be called on a true position, no error handling when called
     * with a wrong position!
     *
     * @param pos
     * @param ownChips
     * @param otherChips
     * @return
     */
    private static PositionCheckResult checkToFlip_NE(int pos, long ownChips, long otherChips){
        /* save next chip as flippable */
        long toFlip = 1L << (pos+7);
        /* next position of interest is two positions away from initial position */
        pos += 14;
        while(pos < 64 && (pos%8) < 7){
            /* return if next field is own chip */
            if(((ownChips >> pos) & 1) == 1){
                return new PositionCheckResult(pos, toFlip);
            }
            /* break if next field is empty */
            else if(((otherChips >> pos) & 1) != 1){
                return null;
            }
            /* continue if next chip is other chip */
            else{
                toFlip = toFlip | (1L << pos);
                pos += 7;
            }
        }
        return null;
    }

    /**
     * Determine enemy chips to flip when a chip is set on given position.
     * Direction: North-West
     * Can only be called on a true position, no error handling when called
     * with a wrong position!
     *
     * @param pos
     * @param ownChips
     * @param otherChips
     * @return
     */
    private static PositionCheckResult checkToFlip_NW(int pos, long ownChips, long otherChips){
        /* save next chip as flippable */
        long toFlip = 1L << (pos+9);
        /* next position of interest is two positions away from initial position */
        pos += 18;
        while(pos < 64 && (pos%8) > 0){
            /* return if next field is own chip */
            if(((ownChips >> pos) & 1) == 1){
                return new PositionCheckResult(pos, toFlip);
            }
            /* break if next field is empty */
            else if(((otherChips >> pos) & 1) != 1){
                return null;
            }
            /* continue if next chip is other chip */
            else{
                toFlip = toFlip | (1L << pos);
                pos += 9;
            }
        }
        return null;
    }

    /**
     * Determine enemy chips to flip when a chip is set on given position.
     * Direction: South-East
     * Can only be called on a true position, no error handling when called
     * with a wrong position!
     *
     * @param pos
     * @param ownChips
     * @param otherChips
     * @return
     */
    private static PositionCheckResult checkToFlip_SE(int pos, long ownChips, long otherChips){
        /* save next chip as flippable */
        long toFlip = 1L << (pos-9);
        /* next position of interest is two positions away from initial position */
        pos -= 18;
        while(pos >= 0 && (pos%8) < 7){
            /* return if next field is own chip */
            if(((ownChips >> pos) & 1) == 1){
                return new PositionCheckResult(pos, toFlip);
            }
            /* break if next field is empty */
            else if(((otherChips >> pos) & 1) != 1){
                return null;
            }
            /* continue if next chip is other chip */
            else{
                toFlip = toFlip | (1L << pos);
                pos -= 9;
            }
        }
        return null;
    }

    /**
     * Determine enemy chips to flip when a chip is set on given position.
     * Direction: South-West
     * Can only be called on a true position, no error handling when called
     * with a wrong position!
     *
     * @param pos
     * @param ownChips
     * @param otherChips
     * @return
     */
    private static PositionCheckResult checkToFlip_SW(int pos, long ownChips, long otherChips){
        /* save next chip as flippable */
        long toFlip = 1L << (pos-7);
        /* next position of interest is two positions away from initial position */
        pos -= 14;
        while(pos >= 0 && (pos%8) > 0){
            /* return if next field is own chip */
            if(((ownChips >> pos) & 1) == 1){
                return new PositionCheckResult(pos, toFlip);
            }
            /* break if next field is empty */
            else if(((otherChips >> pos) & 1) != 1){
                return null;
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

package version2;

import utils.Utils;

public class PositionCheckResult {
    public int pos;
    public long toFlip;

    public PositionCheckResult(int pos, long toFlip){
        this.pos = pos;
        this.toFlip = toFlip;
    }

    @Override
    public String toString(){
        return "<PositionCheckResult: "+ Utils.positionToCoordinate(pos)+">";
    }
}

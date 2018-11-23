package version2;

public class GameResult {
    public int winner;
    int black;
    int white;
    int illegalMove = -1;
    int timeout = -1;

    public GameResult(int winner, int illegalMove, int timeout){
        this.winner = winner;
        this.illegalMove = illegalMove;
        this.timeout = timeout;
    }

    public GameResult(int black, int white){
        this.black = black;
        this.white = white;

        if(black > white){
            this.winner = 1;
        }else if(black < white){
            this.winner = 2;
        }else{
            this.winner = 0;
        }
    }

    @Override
    public String toString(){
        return "<GameResult; Winner="+winner+" Black="+black+" White="+white+" Timeout:"+timeout+" IllegalMove:"+illegalMove+">";
    }
}

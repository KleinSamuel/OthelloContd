package version2;

import utils.Utils;

public class Model {

    public long BOARD_BLACK;
    public long BOARD_WHITE;

    public Model(){
        init();
    }

    public void init(){
        this.BOARD_BLACK = 34628173824L;
        this.BOARD_WHITE = 68853694464L;
    }

    public boolean makeMove(boolean playerBlack, int x, int y){
        PossibleMoves pMoves = new PossibleMoves(playerBlack, BOARD_BLACK, BOARD_WHITE);

        int currentPos = Utils.coordinateToPosition(x, y);

        /* check if move is legal */
        if(((pMoves.moves >> currentPos) & 1) != 1){
            return false;
        }

        long playerChips = (playerBlack) ? BOARD_BLACK : BOARD_WHITE;
        long otherChips = (playerBlack) ? BOARD_WHITE : BOARD_BLACK;

        /* set chip at position */
        playerChips = playerChips ^ Utils.positionToLong(currentPos);

        /* flip chips of opponent */
        long toFlip = pMoves.results.get(currentPos);
        playerChips = playerChips ^ toFlip;
        otherChips = otherChips ^ toFlip;

        /* update main chips */
        BOARD_BLACK = (playerBlack) ? playerChips : otherChips;
        BOARD_WHITE = (playerBlack) ? otherChips : playerChips;

        return true;
    }

    public boolean isBoardFull(){
        return ((this.BOARD_BLACK | this.BOARD_WHITE) == -1);
    }

    public boolean isGameOver(){
        /* check if board is full */
        if(isBoardFull()){
            return true;
        }
        /* check if both players are out of moves */
        PossibleMoves pMovesBlack = new PossibleMoves(true, this.BOARD_BLACK, this.BOARD_WHITE);
        PossibleMoves pMovesWhite = new PossibleMoves(false, this.BOARD_BLACK, this.BOARD_WHITE);
        return (pMovesBlack.results.size() == 0 && pMovesWhite.results.size() == 0);
    }

    public GameResult determineWinner(){
        int numBlack = 0;
        int numWhite = 0;
        for (int i = 63; i >= 0; i--) {
            if(((this.BOARD_BLACK >> i) & 1) == 1){
                numBlack++;
            }
            if(((this.BOARD_WHITE >> i) & 1) == 1){
                numWhite++;
            }
        }
        return new GameResult(numBlack, numWhite);
    }

    public void printCurrentBoard(){
        Utils.printBoard(this.BOARD_BLACK, this.BOARD_WHITE);
    }

}

package game;


import utils.Utils;

public class Board {
    public long BOARD_BLACK;
    public long BOARD_WHITE;

    public static final int IN_PROGRESS = -1;
    public static final int DRAW = 0;
    public static final int P1 = 1;
    public static final int P2 = 2;

    public Board(){
        init();
    }

    public Board(Board board) {
        this.BOARD_BLACK = board.BOARD_BLACK;
        this.BOARD_WHITE = board.BOARD_WHITE;
    }

    public void init(){
        this.BOARD_BLACK = 34628173824L;
        this.BOARD_WHITE = 68853694464L;
    }

    public boolean makeMove(int color, int x, int y){
        PossibleMoves pMoves = new PossibleMoves(color, BOARD_BLACK, BOARD_WHITE);

        int currentPos = Utils.moveToPosition(x, y);

        /* check if move is legal */
        if(((pMoves.moves >> currentPos) & 1) != 1){
            System.out.println("info: " + color + "cant make move, x " + x+ " y " + y);         //FIXME testing purposes
            return false;
        }

        long playerChips = (color == 1) ? BOARD_BLACK : BOARD_WHITE;
        long otherChips = (color == 1) ? BOARD_WHITE : BOARD_BLACK;

        /* set chip at position */
        playerChips = playerChips ^ Utils.positionToLong(currentPos);

        /* flip chips of opponent */
        long toFlip = pMoves.results.get(currentPos);
        playerChips = playerChips ^ toFlip;
        otherChips = otherChips ^ toFlip;

        /* update main chips */
        BOARD_BLACK = (color == 1) ? playerChips : otherChips;
        BOARD_WHITE = (color == 1) ? otherChips : playerChips;

        return true;
    }

    public boolean isBoardFull(){
        return ((this.BOARD_BLACK | this.BOARD_WHITE) == -1);
    }

    public int isGameOver(){
        /* check if board is full */
        if(isBoardFull()){
            return determineWinner().winner;
        }

        /* check if both players are out of moves */
        PossibleMoves pMovesBlack = new PossibleMoves(true, this.BOARD_BLACK, this.BOARD_WHITE);
        PossibleMoves pMovesWhite = new PossibleMoves(false, this.BOARD_BLACK, this.BOARD_WHITE);

        if (pMovesBlack.results.size() == 0 && pMovesWhite.results.size() == 0) {
            return determineWinner().winner;
        } else {
            return -1;
        }
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
//        System.out.println(numBlack + " : " + numWhite);
        GameServer.counter++;
        return new GameResult(numBlack, numWhite);
    }

    public void printCurrentBoard(){
        Utils.printBoard(this.BOARD_BLACK, this.BOARD_WHITE);
    }

}

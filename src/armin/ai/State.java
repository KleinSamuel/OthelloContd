package armin.ai;

import utils.Coordinate;
import utils.Utils;
import version2.PossibleMoves;

import java.util.ArrayList;
import java.util.List;



public class State {
    private Board board;
    private boolean black;
    private int visitCount;
    private double winScore;

    public State() {
        board = new Board();
    }

    public State(State state) {
        this.board = new Board(state.getBoard());
        this.black = state.getPlayerNo();
        this.visitCount = state.getVisitCount();
        this.winScore = state.getWinScore();
    }

    public State(Board board) {
        this.board = new Board(board);
    }

    Board getBoard() {
        return board;
    }

    void setBoard(Board board) {
        this.board = board;
    }

    boolean getPlayerNo() {
        return black;
    }

    void setPlayerNo(boolean playerNo) {
        this.black = playerNo;
    }

    boolean getOpponent() {
        return !black;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    double getWinScore() {
        return winScore;
    }

    void setWinScore(double winScore) {
        this.winScore = winScore;
    }

    public List<State> getAllPossibleStates() {
        List<State> possibleStates = new ArrayList<>();

        PossibleMoves pm = new PossibleMoves(black, board.BOARD_BLACK, board.BOARD_WHITE);
        List<Integer> availablePositions = new ArrayList<>();
        availablePositions.addAll(pm.results.keySet());

        availablePositions.forEach(p -> {
            State newState = new State(this.board);
            newState.setPlayerNo(!black);
            Coordinate coord = Utils.positionToCoordinate(p);
            newState.getBoard().makeMove(newState.getPlayerNo(), coord.x, coord.y);
//            newState.getBoard().performMove(newState.getPlayerNo(), p);
            possibleStates.add(newState);
        });
        return possibleStates;
    }

    void incrementVisit() {
        this.visitCount++;
    }

    void addScore(double score) {
        if (this.winScore != Integer.MIN_VALUE)
            this.winScore += score;
    }

    void randomPlay() {
        PossibleMoves pm = new PossibleMoves(black, board.BOARD_BLACK, board.BOARD_WHITE);
        List<Integer> availablePositions = new ArrayList<>();
        availablePositions.addAll(pm.results.keySet());

        int totalPossibilities = availablePositions.size();
        int selectRandom = (int) (Math.random() * totalPossibilities);
//        this.board.performMove(this.playerNo, availablePositions.get(selectRandom));

        Coordinate coord = Utils.positionToCoordinate(availablePositions.get(selectRandom));
        this.board.makeMove(black, coord.x, coord.y);
    }

    void togglePlayer() {
        this.black = !this.black;
    }
}
package mct;

import game.Board;
import game.PossibleMoves;
import szte.mi.Move;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class State {
    private Board board;
    private int playerNo;
    private int visitCount;
    private double winScore;

    public State() {
        board = new Board();
    }

    public State(State state) {
        this.board = new Board(state.getBoard());
        this.playerNo = state.getPlayerNo();
        this.visitCount = state.getVisitCount();
        this.winScore = state.getWinScore();
    }

    public List<State> getAllPossibleStates() {
        List<State> possibleStates = new ArrayList<>();

        PossibleMoves pm = new PossibleMoves(playerNo, board.BOARD_BLACK, board.BOARD_WHITE);
        List<Integer> availablePositions = new ArrayList<>();
        availablePositions.addAll(pm.results.keySet());

        availablePositions.forEach(p -> {
            State newState = new State(this.board);
            newState.setPlayerNo(playerNo);
            Move coord = Utils.positionToMove(p);

            newState.getBoard().makeMove(newState.getPlayerNo(), coord.x, coord.y);
//            newState.getBoard().performMove(newState.getPlayerNo(), p);
            possibleStates.add(newState);
        });

//        possibleStates.forEach(x -> x.getBoard().printCurrentBoard());
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
        PossibleMoves pm = new PossibleMoves(playerNo, board.BOARD_BLACK, board.BOARD_WHITE);
        List<Integer> availablePositions = new ArrayList<>();
        availablePositions.addAll(pm.results.keySet());

        Random rand = new Random();


        int totalPossibilities = availablePositions.size();
//        int selectRandom = (int) (Math.random() * totalPossibilities);
        if (totalPossibilities > 1) {
            int selectRandom = rand.nextInt(totalPossibilities-1) + 1;
//        this.board.performMove(this.playerNo, availablePositions.get(selectRandom));

//            System.out.println("size of available positions "+availablePositions.size());
//            availablePositions.forEach(x -> System.out.println(Utils.positionToCoordinate(x)));

            Move coord = Utils.positionToMove(availablePositions.get(selectRandom));
            this.board.makeMove(playerNo, coord.x, coord.y);
        }
        if (totalPossibilities == 1) {
//            System.out.println("size of available positions "+availablePositions.size());
//            availablePositions.forEach(x -> System.out.println(Utils.positionToCoordinate(x)));

            Move coord = Utils.positionToMove(availablePositions.get(0));
            this.board.makeMove(playerNo, coord.x, coord.y);
        }
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

    int getPlayerNo() {
        return playerNo;
    }

    void setPlayerNo(int playerNo) {
        this.playerNo = playerNo;
    }

    int getOpponent() {
        return 3 - playerNo;
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

    void togglePlayer() {
        this.playerNo = 3 - playerNo;
    }
}
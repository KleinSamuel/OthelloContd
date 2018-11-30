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

    private int[] posMatrix = new int[] {
            1, 5, 3, 3,  3, 3, 5, 1,
            5, 5, 4, 4,  4, 4, 5, 5,
            3, 4, 2, 2,  2, 2, 4, 3,
            3, 4, 2, 0,  0, 2, 4, 3,
            3, 4, 2, 0,  0, 2, 4, 3,
            3, 4, 2, 2,  2, 2, 4, 3,
            5, 5, 4, 4,  4, 4, 5, 5,
            1, 5, 3, 3,  3, 3, 5, 1 };

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

        PossibleMoves pm = new PossibleMoves(3 - playerNo, board.BOARD_BLACK, board.BOARD_WHITE);   //FIXME this was important

        List<Integer> availablePositions = new ArrayList<>();
        availablePositions.addAll(pm.results.keySet());

        availablePositions.forEach(p -> {
            State newState = new State(this.board);
            newState.setPlayerNo(3 - playerNo);
            Move coord = Utils.positionToMove(p);

            newState.getBoard().makeMove(newState.getPlayerNo(), coord.x, coord.y);
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
        PossibleMoves pm = new PossibleMoves(playerNo, board.BOARD_BLACK, board.BOARD_WHITE);
        List<Integer> availablePositions = new ArrayList<>();
        availablePositions.addAll(pm.results.keySet());
//        int min = 10;
//        List<Integer> pos = new ArrayList<>();
//        for (int i = 0; i < availablePositions.size(); i++) {
//            if (min > posMatrix[availablePositions.get(i)]) {
//                min = posMatrix[availablePositions.get(i)];
//                pos.clear();
////                for (int j = 0; j <= 5 - posMatrix[availablePositions.get(i)]; j++) {
//                    pos.add(availablePositions.get(i));
////                }
//            } else {
//                pos.add(availablePositions.get(i));
//            }
//        }
//        availablePositions = pos;

        Random rand = new Random();

        int totalPossibilities = availablePositions.size();
        if (totalPossibilities > 1) {
            int selectRandom = rand.nextInt(totalPossibilities);
            Move coord = Utils.positionToMove(availablePositions.get(selectRandom));
            this.board.makeMove(playerNo, coord.x, coord.y);
        }
        if (totalPossibilities == 1) {
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
package mct;

import game.Board;

import java.util.List;


public class MonteCarloTreeSearch {

    private static final int WIN_SCORE = 100;
    private int level;
    private int opponent;

    public MonteCarloTreeSearch() {
        this.level = 8;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

//    private int getMillisForCurrentLevel() {
//        return 2 * (this.level - 1) + 1;
//    }

//    private int getMillisForCurrentLevel() {
//        return 80;
//    }

    private int getMillisForCurrentLevel() {
        if (level < 5) {
            return 30;
        } else if (level < 10) {
            return 40;
        } else if (level < 15) {
            return 65;   //this seems to be good
        } else if (level < 23) {
            return 40;
        } else
            return 60;
    }

    public Board findNextMove(Board board, int playerNo) {
        long start = System.currentTimeMillis();
        long end = start + getMillisForCurrentLevel();

        opponent = 3 - playerNo;

        Tree tree = new Tree();
        Node rootNode = tree.getRoot();
        rootNode.getState().setBoard(board);
        rootNode.getState().setPlayerNo(opponent);


        while (System.currentTimeMillis() < end) {
            // Phase 1 - Selection
            Node promisingNode = selectPromisingNode(rootNode);


            // Phase 2 - Expansion
            if (promisingNode.getState().getBoard().isGameOver() == Board.IN_PROGRESS)
                expandNode(promisingNode);

            // Phase 3 - Simulation
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildArray().size() > 0) {
//                nodeToExplore = promisingNode.getRandomChildNode();
                nodeToExplore = promisingNode.getMatrixChildNode();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);
            // Phase 4 - Update
            backPropogation(nodeToExplore, playoutResult);
        }

        try {
            Node winnerNode = rootNode.getChildWithMaxScore();
            tree.setRoot(winnerNode);
            return winnerNode.getState().getBoard();
        } catch (Exception e) {
            return null;
        }
    }

    private Node selectPromisingNode(Node rootNode) {
        Node node = rootNode;
        while (node.getChildArray().size() != 0) {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }

    private void expandNode(Node node) {
        List<State> possibleStates = node.getState().getAllPossibleStates();
        possibleStates.forEach(state -> {
            Node newNode = new Node(state);
            newNode.setParent(node);
            newNode.getState().setPlayerNo(node.getState().getOpponent());
            node.getChildArray().add(newNode);
        });
    }

    private void backPropogation(Node nodeToExplore, int playerNo) {
        Node tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.getState().incrementVisit();
            if (tempNode.getState().getPlayerNo() == playerNo)
                tempNode.getState().addScore(WIN_SCORE);
            tempNode = tempNode.getParent();
        }
    }

    private int simulateRandomPlayout(Node node) {
        Node tempNode = new Node(node);
        State tempState = tempNode.getState();
        int boardStatus = tempState.getBoard().isGameOver();

        if (boardStatus == opponent) {
            tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
            return boardStatus;
        }

        while (boardStatus == Board.IN_PROGRESS) {
            tempState.togglePlayer();
            tempState.randomPlay();
            boardStatus = tempState.getBoard().isGameOver();
        }
        return boardStatus;
    }

}

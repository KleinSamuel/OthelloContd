package mct;

import game.Board;

import java.util.List;


public class MonteCarloTreeSearch {

    private static final int WIN_SCORE = 10;
    private int level;
    private int opponent;

    public MonteCarloTreeSearch() {
        this.level = 3;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    private int getMillisForCurrentLevel() {
        return 2 * (this.level - 1) + 1;
    }

    public Board findNextMove(Board board, int playerNo) {
        long start = System.currentTimeMillis();
        long end = start + 50 * getMillisForCurrentLevel();

        opponent = 3 - playerNo;

        Tree tree = new Tree();
        Node rootNode = tree.getRoot();
        rootNode.getState().setBoard(board);
        rootNode.getState().setPlayerNo(playerNo);

        System.out.println("me: " + playerNo +"\topponen: " + opponent);


        while (System.currentTimeMillis() < end) {
            // Phase 1 - Selection
            Node promisingNode = selectPromisingNode(rootNode);
//            System.out.println("possible promissing board");
//            promisingNode.getState().getBoard().printCurrentBoard();

            // Phase 2 - Expansion
//            if (promisingNode.getState().getBoard().checkStatus() == Board.IN_PROGRESS)
            if (promisingNode.getState().getBoard().isGameOver() == Board.IN_PROGRESS)
                expandNode(promisingNode);

            // Phase 3 - Simulation
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildArray().size() > 0) {
                nodeToExplore = promisingNode.getRandomChildNode();
//                nodeToExplore.getState().getBoard().printCurrentBoard();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);
            // Phase 4 - Update
            backPropogation(nodeToExplore, playoutResult);
        }

        try {
            Node winnerNode = rootNode.getChildWithMaxScore();
            tree.setRoot(winnerNode);

//            winnerNode.getState().getBoard().printCurrentBoard();

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

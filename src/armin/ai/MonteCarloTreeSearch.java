package armin.ai;

import mct.Node;
import mct.Tree;

import java.util.List;


public class MonteCarloTreeSearch {

    private static final int WIN_SCORE = 10;
    private int level;
    private boolean opponent;

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

    public Board findNextMove(Board board, boolean black) {
        long start = System.currentTimeMillis();
        long end = start + 60 * getMillisForCurrentLevel();

        opponent = !black;
        Tree tree = new Tree();
        Node rootNode = tree.getRoot();
        rootNode.getState().setBoard(board);
        rootNode.getState().setPlayerNo(opponent);

        while (System.currentTimeMillis() < end) {
            // Phase 1 - Selection
            Node promisingNode = selectPromisingNode(rootNode);
//            System.out.println("possible promissing board");
//            promisingNode.getState().getBoard().printCurrentBoard();

            // Phase 2 - Expansion
//            if (promisingNode.getState().getBoard().checkStatus() == Board.IN_PROGRESS)
            if (!promisingNode.getState().getBoard().isGameOver())
                expandNode(promisingNode);

            // Phase 3 - Simulation
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildArray().size() > 0) {
                nodeToExplore = promisingNode.getRandomChildNode();
            }
            boolean playoutResult = simulateRandomPlayout(nodeToExplore);
            // Phase 4 - Update
            backPropogation(nodeToExplore, playoutResult);
        }

        Node winnerNode = rootNode.getChildWithMaxScore();
        tree.setRoot(winnerNode);
        return winnerNode.getState().getBoard();
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

    private void backPropogation(Node nodeToExplore, boolean playerNo) {
        Node tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.getState().incrementVisit();
            if (tempNode.getState().getPlayerNo() == playerNo)
                tempNode.getState().addScore(WIN_SCORE);
            tempNode = tempNode.getParent();
        }
    }

    private boolean simulateRandomPlayout(Node node) {
        Node tempNode = new Node(node);
        State tempState = tempNode.getState();
        boolean boardStatus = tempState.getBoard().isGameOver();

        if (boardStatus == opponent) {
            tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
            return boardStatus;
        }
        while (!boardStatus) {
            tempState.togglePlayer();
            tempState.randomPlay();
            boardStatus = tempState.getBoard().isGameOver();
        }

        return boardStatus;
    }

}

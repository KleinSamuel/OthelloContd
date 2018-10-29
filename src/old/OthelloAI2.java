package old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import szte.mi.Move;
import szte.mi.Player;

/**
 **************************************************************************************
 *  ▄██████▄      ███        ▄█    █▄       ▄████████  ▄█        ▄█        ▄██████▄  ** 
 * ███    ███ ▀█████████▄   ███    ███     ███    ███ ███       ███       ███    ███ ** 
 * ███    ███    ▀███▀▀██   ███    ███     ███    █▀  ███       ███       ███    ███ ** 
 * ███    ███     ███   ▀  ▄███▄▄▄▄███▄▄  ▄███▄▄▄     ███       ███       ███    ███ ** 
 * ███    ███     ███     ▀▀███▀▀▀▀███▀  ▀▀███▀▀▀     ███       ███       ███    ███ **
 * ███    ███     ███       ███    ███     ███    █▄  ███       ███       ███    ███ **
 * ███    ███     ███       ███    ███     ███    ███ ███▌    ▄ ███▌    ▄ ███    ███ **
 *  ▀██████▀     ▄████▀     ███    █▀      ██████████ █████▄▄██ █████▄▄██  ▀██████▀  **      
 * ************************************************************************************
 * ******************OTHELLO/REVERSI ARTIFICIAL INTELLIGENCE***************************
 * ************************************************************************************
 * ******************************BY SAMUEL KLEIN***************************************
 * ************************************************************************************
 * 																				     **
 * This implementation of an othello/reversi AI is based on the MiniMax-Algorithm    **
 * combined with alpha-beta-pruning and an own way to calculate a score for a        **
 * specific state of the game, which is described in the comment of the method.	     **
 * It uses an BitBoard implementation for better runtime, resulting in higher 	     **
 * depth for the search in the gametree.										     **
 * 																				     **
 * Specialities:																     **
 * - Init method (called at the beginning of each game, sets the color)			     **
 * - nextMove method (gets the enemy move and return the own move)				     **
 * 																			         **
 * Modification:																     **
 * It is designed to play a game of othello in under 4 seconds. The search depth     **
 * of its gametree can be modified in the corresponding method.					     **
 * 																				     **
 * @author Samuel Klein															     **
 * @contact Samuel.Klein@campus.lmu.de											     **
 **************************************************************************************
 */
public class OthelloAI2 implements Player {

	// my chips on the board
	private long ownChips;
	// enemy chips on the board
	private long otherChips;
	// enemys last move
	private int enemyMove;
	
	// 0 = black, 1 = white
	private int whoAmI;

	// counts how full the board is
	private int disksPlaced;

	// game tree for minimax and a-b-pruning
	private GameTree tree2;
	
	// time management
	private long time;
	// time AI started with
	private long startTime;
	// time available for the whole remaining game
	private long timeAvailable;
	// time available for this particular move
	private long timeRemaining;
	// time enemy had one move ago
	private long storedEnemyTimeOld;
	// time enemy used in his last move
	private long storedEnemyTimeUsed;
	
	// Longs that contain the disc-coordinates for the corresponding directions
	private long upLONG = 0L;
	private long leftLONG = 0L;
	private long rightLONG = 0L;
	private long downLONG = 0L;
	private long upLeftLONG = 0L;
	private long upRightLONG = 0L;
	private long downLeftLONG = 0L;
	private long downRightLONG = 0L;
	
	// check for possible moves
	private boolean gegnerDazwischen;
	
	private int oldScoreMatrix;
	
	// criteria for heuristic evaluation
	int stabilityWeight = 850;
	int mobilityWeight = 250;
	int cornerWeight = 1900;
	int closeToCornerWeight = 700;
	
	// bounds which are used to determine which tactic is used in which state of game
	int firstStage;
	int secondStage;
	int thirdStage;
	
	// bounds to determine if enemy is a greedy motherfucker
	private int hatGreedyGezogen = 0;
	private int upperBoundGreedy = 7;
	private boolean isEnemyGreedy = false;
	// bounds to determine if enemy is as fast as flash
	private int hasPlayedFastInRow = 0;
	private int fastPlayUpperBound = 5;
	private boolean isFastAsFuck = false;
	// bounds to determine if enemy wants to be NEO and loves the matrix
	private int hasUsedMatrix = 0;
	private int upperBoundMatrix = 7;
	private boolean isEnemyMatrix = false;
	
	GameTree treeToAppend;
	GameTreeNode currentNode = null;

	/*
	 *  matrix from "Playing Othello With Artificial Intelligence" by Michael. J . Korman
	 *  Source: http://mkorman.org/othello.pdf
	 *  
	 *  EDIT: This matrix is not used due to bad results
	 *  this matrix is so bad... how did this guy get a PhD for this crap??
	 */
	private int[] posMatrixArray2 = new int[] { 
			20, -3, 11,  8,  8, 11, -3, 20, 
			-3, -7, -4,  1,  1, -4, -7, -3,
			11, -4,  2,  2,  2,  2, -4, 11,
			 8,  1,  2, -3, -3,  2,  1,  8,
			 8,  1,  2, -3, -3,  2,  1,  8, 
			11, -4,  2,  2,  2,  2, -4, 11,
			-3, -7, -4,  1,  1, -4, -7, -3, 
			20, -3, 11,  8,  8, 11, -3, 20 };
	
	/*
	 * matrix created by naive thoughts
	 * 
	 * EDIT: This matrix is not used either due to bad results
	 */
	private int[] posMatrixArray = new int[] {
		 100,  -50, 50, 20, 20, 50,  -50, 100 ,
		 -50, -100, 10,  5,  5, 10, -100, -50 ,
		  50,   10,  8,  1,  1,  8,   10,  50 ,
		  20,    5,  1,  0,  0,  1,    5,  20 ,
		  20,    5,  1,  0,  0,  1,    5,  20 ,
		  50,   10,  8,  1,  1,  8,   10,  50 ,
		 -50, -100, 10,  5,  5, 10, -100, -50 ,
		 100,  -50, 50, 20, 20, 50,  -50, 100  };

	/*
	 *  Method is called on creation of this class, sets the color of the discs an creates a new GameTree which stores the game states(non-Javadoc)
	 *  
	 *  @see szte.mi.Player#init(int, long, java.util.Random)
	 */
	@Override
	public void init(int order, long t, Random rnd){

		genOwnChips(order);

		whoAmI = order;
		
		// trying to use different bounds when AI is starting because it is vulnerable when it hast to make the first move
//		if(whoAmI == 0){
//			firstStage = 16;
//			secondStage = 49;
//			thirdStage = 58;
//			
//			mobilityWeight = 400;
//			
//		}else{
			firstStage = 13;
			secondStage = 47;
			thirdStage = 56;
//		}
	}

	int dezimalEnemy;
	
	/**
	 *  method gets called everytime the other player made a move and return the own move
	 *  
	 *  @see Player#nextMove(Move, long, long)
	 */
	@Override
	public Move nextMove(Move prevMove, long tOpponent, long t) {

		long timeStart = System.currentTimeMillis();
		startTime = System.currentTimeMillis();
		timeRemaining = t;
		
		if(storedEnemyTimeOld == 0){
			storedEnemyTimeOld = tOpponent;			
		}else{
			storedEnemyTimeUsed = storedEnemyTimeOld - tOpponent;
			storedEnemyTimeOld = tOpponent;
		}
		
		int x = 0;
		int y = 0;

		if (prevMove != null) {
			// update the board for the enemy move
			x = prevMove.x;
			y = prevMove.y;

			dezimalEnemy = y * 8 + x;

			update(dezimalEnemy);
		}else{
			dezimalEnemy = -1;
		}

		if (isBeendet(ownChips, otherChips)) {
			return null;
		}

		int out = makeMoveAI();

		if (out == -1) {
			return null;
		}

		int xOut = out % 8;
		int yOut = out / 8;

		long timeEnd = System.currentTimeMillis();

		time += timeEnd - timeStart;
		
		return new Move(xOut, yOut);
	}

	/**
	 * method gets called in the nextMove() method and calculates the best move for the AI
	 * 
	 * @return Integer between 0 and 63 being the calculated move
	 */
	public int makeMoveAI() {

		ConcurrentHashMap<Integer, Long> toTurn = possibleMovesLONG(getOwnChips(), getOtherChips());

		if (toTurn.size() == 0) {
			return -1;
		}

		GameTreeNode startNode;
	
		oldScoreMatrix = 0;
		
		startNode = new GameTreeNode(getOwnChips(), getOtherChips(), 0, oldScoreMatrix, false);		
		
		disksPlaced = 0;

		for (int i = 0; i < 64; i++) {
			if (((getOwnChips() >> i) & 1) == 1) {
				disksPlaced++;
			}
			if (((getOtherChips() >> i) & 1) == 1) {
				disksPlaced++;
			}
		}
		
		int matrixBest = 0;

		timeAvailable = timeRemaining;
			
		if (disksPlaced <= firstStage) {

			matrixBest = MiniMaxWithAlphaBetaPruningFirstStage(3, startNode, false, false);
			
		}else if(disksPlaced < secondStage){

			matrixBest = MiniMaxWithAlphaBetaPruningFirstStage(4, startNode, false, false);
			
		}else if(disksPlaced < thirdStage){
			
			matrixBest = MiniMaxWithAlphaBetaPruningFirstStage(5, startNode, false, false);				
			
		}else{
			
//			long tTMP = (timeRemaining / 20);
//			timeAvailable = tTMP * (64-disksPlaced);

//			miniMaxIterative(5, startNode, false);
//
//			matrixBest = traverseExistingTree1(startNode);
				
			if(timeAvailable < 300){
				matrixBest = MiniMaxWithAlphaBetaPruningFirstStage(3, startNode, false, false);
			}else{
				matrixBest = MiniMaxWithAlphaBetaPruningFirstStage(8, startNode, false, false);
			}
			
		}
		return makeMove(matrixBest, toTurn);
	}
	
	/*
	 * --------------------- recognition pattern---------------------------
	 * Methods detect what kind of strategy the enemy is using
	 * 
	 *  Detectable pattern:
	 * - Enemy uses just a few milliseconds -> Greedy, Random or Matrix
	 * - Greedy
	 * - Matrix
	 * - Random (not implemented yet)
	 */
	
	/**
	 * method detects if enemy is using a greedy strategy
	 * 
	 * @param enemyMove , enemys last move to update own board
	 * @param enemyMoves , HashMap containing all possible enemy moves
	 * @return true if enemy is greedy
	 */
	public boolean isGreedy(Move enemyMove, ConcurrentHashMap<Integer, Long> enemyMoves){
		int x = enemyMove.x;
		int y = enemyMove.y;
		
		int highest = 0;
		int highestInt = 0;
		int counter = 0;
		
		for (Integer i : enemyMoves.keySet()){
			counter = 0;
			for (int j = 0; j < 64; j++) {
				if(((enemyMoves.get(i) >> 1) & 1) == 1){
					counter += 1;
				}
			}
			if(counter >= highest){
				highest = counter;
				highestInt = i;
			}
		}
		
		if(highestInt == (x+y*8)){
			hatGreedyGezogen += 1;
		}else{
			hatGreedyGezogen = 0;
		}
		
		if(hatGreedyGezogen >= upperBoundGreedy){
			return true;
		}
		return false;
	}
	
	/**
	 * Method detects if enemy is using only a few milliseconds in a row
	 * 
	 * @param enemyTime
	 * @return true if enemy makes a lot of fast moves
	 */
	public boolean isVeryFast(long enemyTime){
		
		if(enemyTime <= 100){
			hasPlayedFastInRow += 1;
		}else{
			hasPlayedFastInRow = 0;
		}
		
		if(hasPlayedFastInRow >= fastPlayUpperBound){
			return true;
		}
		return false;
	}
	
	/**
	 * method detects if enemy is using a matrix to score
	 * 
	 * @param enemyMove
	 * @param enemyMoves
	 * @return true if enemy is using a matrix
	 */
	public boolean isMatrixSingle(Move enemyMove, ConcurrentHashMap<Integer, Long> enemyMoves){
		
		int x = enemyMove.x;
		int y = enemyMove.y;
		int highest = 0;
		ArrayList<Integer> liste = new ArrayList<Integer>();
		
		for (Integer i : enemyMoves.keySet()){
			if(posMatrixArray[i] >= highest){ 
				highest = posMatrixArray[i];
			}
		}
		
		for (int i = 0; i < posMatrixArray.length; i++) {
			if(posMatrixArray[i] == highest){
				liste.add(i);
			}
		}
		
		if(liste.contains(x+y*8)){
			hasUsedMatrix += 1;
		}else{
			hasUsedMatrix = 0;
		}
		
		if(hasUsedMatrix >= upperBoundMatrix){
			return true;
		}
		return false;
	}
	
	/*
	 * --------------------------end of recognition pattern---------------------------------------
	 */

	public void appendTree(int depth){
		ArrayList<GameTreeNode> liste = new ArrayList<GameTreeNode>();
		
		getLeafs(currentNode, liste);
		
		for(GameTreeNode n : liste){
			miniMax(0, depth, n, !n.getWasMax());
		}
	}
	
	public void getLeafs(GameTreeNode node, ArrayList<GameTreeNode> liste){
		if(node.getChildren().size() == 0){
			liste.add(node);
		}
		for(GameTreeNode n : node.getChildren()){
			getLeafs(n, liste);
		}
	}
	
	public int traverseExistingTree1(GameTreeNode startNode){
		int score = Integer.MIN_VALUE;
		int out = 0;
		
		for(GameTreeNode n : startNode.getChildren()){
			
			int score2 = traverseExistingTree2(n, false);
			
			if(score2 > score){
				score = score2;
				out = n.getDezimal();
			}
		}
		return out;
	}
	
	public int traverseExistingTree2(GameTreeNode startNode, boolean max){
		
		if(startNode.getChildren().size() == 0){

			return countTreshold(startNode.getOwnChips(), startNode.getOtherChips());
		}

		if(max){
			
			int best = Integer.MIN_VALUE;
			
			for(GameTreeNode n : startNode.getChildren()){
				
				int score = traverseExistingTree2(n, false);
				
				best = Math.max(score, best);
			}
			return best;
			
		}else{
			
			int best = Integer.MAX_VALUE;
			
			for(GameTreeNode n : startNode.getChildren()){
				
				int score = traverseExistingTree2(n, true);
				
				best = Math.min(score, best);
			}
			
			return best;
		}
	}

	/*************************************************************************************************************************************************
	 * 	__/\\\\____________/\\\\__/\\\\\\\\\\\__/\\\\\_____/\\\__/\\\\\\\\\\\__/\\\\____________/\\\\_____/\\\\\\\\\_____/\\\_______/\\\_        	**
 	 *	 _\/\\\\\\________/\\\\\\_\/////\\\///__\/\\\\\\___\/\\\_\/////\\\///__\/\\\\\\________/\\\\\\___/\\\\\\\\\\\\\__\///\\\___/\\\/__       	**
  	 *	  _\/\\\//\\\____/\\\//\\\_____\/\\\_____\/\\\/\\\__\/\\\_____\/\\\_____\/\\\//\\\____/\\\//\\\__/\\\/////////\\\___\///\\\\\\/____      	**
   	 *	   _\/\\\\///\\\/\\\/_\/\\\_____\/\\\_____\/\\\//\\\_\/\\\_____\/\\\_____\/\\\\///\\\/\\\/_\/\\\_\/\\\_______\/\\\_____\//\\\\______     	**
     *	    _\/\\\__\///\\\/___\/\\\_____\/\\\_____\/\\\\//\\\\/\\\_____\/\\\_____\/\\\__\///\\\/___\/\\\_\/\\\\\\\\\\\\\\\______\/\\\\______    	**
     * 	     _\/\\\____\///_____\/\\\_____\/\\\_____\/\\\_\//\\\/\\\_____\/\\\_____\/\\\____\///_____\/\\\_\/\\\/////////\\\______/\\\\\\_____   	**
     * 		  _\/\\\_____________\/\\\_____\/\\\_____\/\\\__\//\\\\\\_____\/\\\_____\/\\\_____________\/\\\_\/\\\_______\/\\\____/\\\////\\\___  	**
     *  	   _\/\\\_____________\/\\\__/\\\\\\\\\\\_\/\\\___\//\\\\\__/\\\\\\\\\\\_\/\\\_____________\/\\\_\/\\\_______\/\\\__/\\\/___\///\\\_ 	**
     *   		_\///______________\///__\///////////__\///_____\/////__\///////////__\///______________\///__\///________\///__\///_______\///__	**
	 * 																																				**
	 * 																																				**
	 * ***************USED ALGORITHM TO CALCULATE THE BEST POSSIBLE MOVE******************************************************************************
	 * 																																				**
	 * method creates a new node for the GameTree an starts the recursive MiniMac algorithm, returns the best current move.							**
	 * 																																				**
	 * For more details on MiniMax and/or Alpha-Beta-Pruning see: http://www3.ntu.edu.sg/home/ehchua/programming/java/javagame_tictactoe_ai.html	**
	 * 																																				**
	 * first stage: iterates over each possible move the startnode has and gets the best result														**
	 * second stage: recursive part of minimax																										**
	 * 																																				**
	 * @param depth , search depth																													**
	 * @param startNode , first node of tree (startpoint)																							**
	 * @return moveToMake , int between -1 and 63																									**
	 * 																																				**
	 * ************************************************************************************************************************************************
	 */
	public int MiniMaxWithAlphaBetaPruningFirstStage(int depth, GameTreeNode startNode, boolean append, boolean fastScoring) {
		
		int max = Integer.MIN_VALUE;
		int out = 0;
		
		ConcurrentHashMap<Integer, Long> p = possibleMovesLONG(startNode.getOwnChips(), startNode.getOtherChips());
		
		if(p.keySet().size() == 0){
			return -1;
		}
		
		for(Integer i : p.keySet()){
			
			
			Long[] tmp1 = updateForTreeBlackLONG(i, p.get(i), startNode.getOwnChips(), startNode.getOtherChips());
			
			int scoreOLD = 0;
			
			if(fastScoring){
				scoreOLD = getFastTreshold(tmp1[0], getCurrentMatrix(tmp1[0]));				
			}

			GameTreeNode nodeToAdd = new GameTreeNode(tmp1[0], tmp1[1], i, scoreOLD, true);
			
			if(append){
				startNode.addChildren(nodeToAdd);
			}
			
			if(countChips(tmp1[1]) == 0){
				return i;
			}
			
			ConcurrentHashMap<Integer, Long> possEnemy = possibleMovesLONG(tmp1[1], tmp1[0]);
			if(possEnemy.keySet().size() == 0){
				return i;
			}

			if(i == 0 || i == 7 || i == 56 || i == 63){
				return i;
			}
			
			int score = MiniMaxWithAlphaBetaPruningSecondStage(1, depth, nodeToAdd, false, Integer.MIN_VALUE, Integer.MAX_VALUE, append, fastScoring);				
			
			if(score > max){
				max = score;
				out = i;
			}
		}
		return out;
	}
	
	/**
	 * recursive part of minimax algorithm
	 * 
	 * @param depth , current searchdepth
	 * @param maxDepth , maximum search depth
	 * @param startNode , parent node to compute all possible moves from
	 * @param max , boolean if its the maximizing players turn
	 * @param alpha , alpha value for maximizing player
	 * @param beta , beta value for minimizing player
	 * @return
	 */
	public int MiniMaxWithAlphaBetaPruningSecondStage(int depth, int maxDepth, GameTreeNode startNode, boolean max, int alpha, int beta, boolean append, boolean fastScoring){
		
		ConcurrentHashMap<Integer, Long> possibleMoves;
		
		if(max){
			possibleMoves = possibleMovesLONG(startNode.getOwnChips(), startNode.getOtherChips());
		}else{
			possibleMoves = possibleMovesLONG(startNode.getOtherChips(), startNode.getOwnChips());
		}
		
		long timeUsed = System.currentTimeMillis() - startTime;
		
		if(possibleMoves.keySet().size() == 0 || depth >= maxDepth|| timeAvailable < timeUsed){

			if(possibleMoves.keySet().size() == 0){
				
				if(feldIstVoll(startNode.getOwnChips(), startNode.getOtherChips())){
					int blacks = countChips(startNode.getOwnChips());
					int whites = countChips(startNode.getOtherChips());
					if(blacks > whites){
						return Integer.MAX_VALUE-(whites)-1;
					}else if(whites > blacks){
						return Integer.MIN_VALUE+(blacks)+1;
					}else{
						return 0;
					}
				}
				ConcurrentHashMap<Integer, Long> pTMP;
				if(max){
					pTMP = possibleMovesLONG(startNode.getOtherChips(), startNode.getOwnChips());
				}else{
					pTMP = possibleMovesLONG(startNode.getOwnChips(), startNode.getOtherChips());
				}
				if(pTMP.keySet().size() == 0){
					int blacks = countChips(startNode.getOwnChips());
					int whites = countChips(startNode.getOtherChips());
					if(blacks > whites){
						return Integer.MAX_VALUE-(whites)-1;
					}else if(whites > blacks){
						return Integer.MIN_VALUE+(blacks)+1;
					}else{
						return 0;
					}
				}else{
					
					GameTreeNode nodeToAdd = new GameTreeNode(startNode.getOwnChips(), startNode.getOtherChips(), -1, startNode.getOldScore(), !max);
					
					if(append){
						startNode.addChildren(nodeToAdd);
					}
					
					return MiniMaxWithAlphaBetaPruningSecondStage(depth + 1, maxDepth, nodeToAdd, !max, alpha, beta, append, fastScoring);
				}
			}
			
			return countTreshold(startNode.getOwnChips(), startNode.getOtherChips());
		}
		
		if(max){
			int maxWert = alpha;
			int amount = 0;
			
			for(Integer i : possibleMoves.keySet()){
				
				Long[] tmp1 = updateForTreeBlackLONG(i, possibleMoves.get(i), startNode.getOwnChips(), startNode.getOtherChips());
				
				int scoreOLD = 0;
				if(fastScoring){
					scoreOLD = getFastTreshold(tmp1[0], getCurrentMatrix(tmp1[0]));				
				}

				GameTreeNode nodeToAdd = new GameTreeNode(tmp1[0], tmp1[1], i, scoreOLD, true);
				
				if(append){
					startNode.addChildren(nodeToAdd);
				}
				
				int score = 0;
				
				if(fastScoring){
					if(startNode.getOldScore()*0.2 > scoreOLD){
						score = MiniMaxWithAlphaBetaPruningSecondStage(maxDepth, maxDepth, nodeToAdd, false, alpha, beta, append, fastScoring);
					}else if(startNode.getOldScore()*10 < scoreOLD){
						return Integer.MAX_VALUE-1;
					}else{
						score = MiniMaxWithAlphaBetaPruningSecondStage(depth + 1, maxDepth, nodeToAdd, false, maxWert, beta, append, fastScoring);
					}
				}else{
					score = MiniMaxWithAlphaBetaPruningSecondStage(depth + 1, maxDepth, nodeToAdd, false, maxWert, beta, append, fastScoring);
				}

				if(score >= Integer.MAX_VALUE-10000){
					amount += 1;
				}else{
				
				if(score > maxWert){
					maxWert = score;
					if(maxWert >= beta){
						break;
					}
				}
				}
			}
			if(amount > 0){
				return Integer.MAX_VALUE-10000+amount;
			}else{
				return maxWert;				
			}
		}else{
			
			int minWert = beta;
			
			for(Integer i : possibleMoves.keySet()){
				
				Long[] tmp1 = updateForTreeWhiteLONG(i, possibleMoves.get(i), startNode.getOwnChips(), startNode.getOtherChips());
				
				int scoreOLD = 0;
				
				if(fastScoring){
					
					scoreOLD = getFastTreshold(tmp1[0], getCurrentMatrix(tmp1[0]));					
				}

				GameTreeNode nodeToAdd = new GameTreeNode(tmp1[0], tmp1[1], i, scoreOLD, false);
				
				if(append){
					startNode.addChildren(nodeToAdd);
				}
				
				int score = MiniMaxWithAlphaBetaPruningSecondStage(depth + 1, maxDepth, nodeToAdd, true, alpha, minWert, append, fastScoring);
				
				if(score < minWert){
					minWert = score;
					if(minWert <= alpha){
						break;
					}
				}
			}
			return minWert;
		}
		
	}
	
	
	/*
	 * ------------------------------- Approach on iterative Alpha-Beta for better time management --------------------------------------
	 */
	
	public void miniMaxIterative(int maxDepth, GameTreeNode startNode, boolean cutoff){
		
		int depth = 0;
		boolean max = true;
		
		long startTime = System.currentTimeMillis();
		
		ArrayList<GameTreeNode> currentLeafs = new ArrayList<GameTreeNode>();
		
		currentLeafs.add(startNode);
		
		ArrayList<GameTreeNode> toAdd = new ArrayList<GameTreeNode>();
		
		while(depth <= maxDepth){
			
			ConcurrentHashMap<Integer, Long> possible;
			
			for(GameTreeNode n : currentLeafs){
				
				
				// add time limit for each node
				
				if(max){
					possible = possibleMovesLONG(n.getOwnChips(), n.getOtherChips());
				}else{
					possible = possibleMovesLONG(n.getOtherChips(), n.getOwnChips());
				}
				
				for(Integer i : possible.keySet()){

					if(max){
						Long[] tmp = updateForTreeBlackLONG(i, possible.get(i), n.getOwnChips(), n.getOtherChips());
						
						int scoreTMP;
						if(cutoff){
							scoreTMP = countTreshold(tmp[0], tmp[1]);							
						}else{
							scoreTMP = 0;							
						}
		
						GameTreeNode nodeToAdd = new GameTreeNode(tmp[0], tmp[1], i, scoreTMP, max);
						
						n.addChildren(nodeToAdd);
						
						toAdd.add(nodeToAdd);
						
					}else{
						Long[] tmp = updateForTreeWhiteLONG(i, possible.get(i), n.getOwnChips(), n.getOtherChips());
						
						int scoreTMP;
						if(cutoff){
							scoreTMP = countTreshold(tmp[0], tmp[1]);							
						}else{
							scoreTMP = 0;							
						}
						
						GameTreeNode nodeToAdd = new GameTreeNode(tmp[0], tmp[1], i, scoreTMP, max);
						
						n.addChildren(nodeToAdd);
						
						toAdd.add(nodeToAdd);
						
					}
					
				}
				
			}
			
			// add time limit for each depth
			
			long endTime = System.currentTimeMillis();
			currentLeafs.clear();
			currentLeafs.addAll(toAdd);
			toAdd.clear();
			
//			sortListe(currentLeafs);
			
			if((endTime-startTime) >= timeAvailable){
				// reicht liste löschen?
				for(GameTreeNode n : toAdd){
					n = null;
				}
				break;
			}
			
			depth += 1;
			max = !max;
		}

	}
	
	/**
	 * Comparator sorts an ArrayList containing GameTreeNodes according to the score for more effective a-b-pruning
	 * 
	 * @param liste
	 */
	public void sortListe(ArrayList<GameTreeNode> liste){
		
		Collections.sort(liste, new Comparator<GameTreeNode>() {
			@Override
			public int compare(GameTreeNode arg0, GameTreeNode arg1) {
//				int a = countTreshold(arg0.getOwnChips(), arg0.getOtherChips());
//				int b = countTreshold(arg1.getOwnChips(), arg1.getOtherChips());
				
				int a = arg0.getOldScore();
				int b = arg1.getOldScore();
				
				if(a > b){
					return -1;
				}else if(a < b){
					return 1;
				}else{
					return 0;
				}
			}
	    });
	}
	
	
	/*
	 * ----------------------------------------------------------------------------------------------------------------------------------
	 */
	
	/**
	 * calculates the next best move according to a matrix
	 * 
	 * EDIT: deprecated and not used anymore
	 * 
	 * @param toTurn , possible moves for current player
	 * @return int between -1 and 63 
	 */
	public int berechneAusMatrix(ConcurrentHashMap<Integer, Long> toTurn, int[] matrix) {

		int selectedMove = 0;
		int highestValue = Integer.MIN_VALUE;

		for (Entry<Integer, Long> entry : toTurn.entrySet()) {
			Integer key = entry.getKey();

			if (posMatrixArray[key] > highestValue && toTurn.get(key) != 0L) {
				highestValue = matrix[key];
				selectedMove = key;
			}
		}

		return selectedMove;
	}
	
	/**
	 * ********** MINIMAX ALGORITHM***************
	 * 											
	 * EDIT: deprecated and not used anymore	
	 * 											
	 * @param depth	, search depth in tree	
	 * @param startNode , startnode of tree
	 * @param max , boolean if current player is maximizing player
	 * @return int between -1 and 63
	 */
	public int miniMax(int depth, int maxDepth, GameTreeNode startNode, boolean max){
		
		ConcurrentHashMap<Integer, Long> possibleMoves;
		
		if(max){
			possibleMoves = possibleMovesLONG(startNode.getOwnChips(), startNode.getOtherChips());
		}else{
			possibleMoves = possibleMovesLONG(startNode.getOtherChips(), startNode.getOwnChips());
		}
		
		if(possibleMoves.keySet().size() == 0 || depth >= maxDepth){
			
			ConcurrentHashMap<Integer, Long> pTMP;
			
			if(max){
				pTMP = possibleMovesLONG(startNode.getOtherChips(), startNode.getOwnChips());
			}else{
				pTMP = possibleMovesLONG(startNode.getOwnChips(), startNode.getOtherChips());
			}
			if(possibleMoves.keySet().size() == 0 && pTMP.keySet().size() != 0){
				
				GameTreeNode nodeToAdd = new GameTreeNode(startNode.getOwnChips(), startNode.getOtherChips(), -1, 0, max);
				
				startNode.addChildren(nodeToAdd);
				
				return miniMax(depth +1, maxDepth, nodeToAdd, !max);
			}
//				
//				ConcurrentHashMap<Integer, Long> pTMP;
//				if(max){
//					pTMP = possibleMovesLONG(startNode.getOtherChips(), startNode.getOwnChips());
//				}else{
//					pTMP = possibleMovesLONG(startNode.getOwnChips(), startNode.getOtherChips());
//				}
//				if(pTMP.keySet().size() != 0){
//					return miniMax(depth + 1, maxDepth, startNode, !max);
//				}
//			}
			return 0;
//			return countTreshold(startNode.getOwnChips(), startNode.getOtherChips());
		}
		
		if(max){
			int best = Integer.MIN_VALUE;
			
			for(Integer i : possibleMoves.keySet()){
				
				Long[] tmp1 = updateForTreeBlackLONG(i, possibleMoves.get(i), startNode.getOwnChips(), startNode.getOtherChips());
				
				GameTreeNode nodeToAdd = new GameTreeNode(tmp1[0], tmp1[1], i, 0, true);
				
				startNode.addChildren(nodeToAdd);
				
				int score = miniMax(depth + 1, maxDepth, nodeToAdd, false);
				
				best = Math.max(score, best);
			}
			
			return best;
		}else{
			
			int best = Integer.MAX_VALUE;
			
			for(Integer i : possibleMoves.keySet()){
				
				Long[] tmp1 = updateForTreeWhiteLONG(i, possibleMoves.get(i), startNode.getOwnChips(), startNode.getOtherChips());
				
				GameTreeNode nodeToAdd = new GameTreeNode(tmp1[0], tmp1[1], i, 0, false);
				
				startNode.addChildren(nodeToAdd);
				
				int score = miniMax(depth + 1, maxDepth, nodeToAdd, true);
				
				best = Math.min(score, best);
			}
			
			return best;
		}
		
	}

	/**
	 * method checks if board is completely filled
	 * 
	 * @param black , black chips as bit representation
	 * @param white , white chips as bit representation
	 * @return boolean if board is filled
	 */
	public boolean feldIstVoll(long black, long white){
		for (int i = 0; i < 64; i++) {
			if (((black >> 0) & 1) == 1) {
			}else if (((white >> 0) & 1) == 1) {
				
			}else{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * method calculates a score according to a matrix
	 * this can be used to determine if a node should be followed by minimax
	 * 
	 * @param black , black chips as bit representation
	 * @param white , white chips as bit representation
	 * @return score , for current board
	 */
	public int getFastTreshold(long black, int[] matrix){
		int bl = 0;
		
		for (int i = 0; i < 64; i++) {
			if (((black >> i) & 1) == 1) {
				bl += matrix[i];
			}
		}	
		return bl;
	}
	
	/**
	 * old miniMax approach
	 * 
	 * EDIT: deprecated and not used anymore but the effort was too high for disposal
	 * 
	 * @param depth
	 * @param startNode
	 * @param last
	 * @return
	 */
	public GameTreeNode miniMaxAlphaBetaPruningFirstStageWithTree(int depth, GameTreeNode startNode, boolean last) {
		
		tree2.setRoot(startNode);
		
		GameTreeNode bestNode = null;
		int bestInt = Integer.MIN_VALUE;
		
		// possible moves for root node
		ConcurrentHashMap<Integer, Long> possible = possibleMovesLONG(startNode.getOwnChips(), startNode.getOtherChips());
		
		if (possible.keySet().size() == 0) {
			return null;
		}
		
		for (Integer i : possible.keySet()) {
			
			Long[] tmp1;
			int scoreOld;
			
			tmp1 = updateForTreeBlackLONG(i, possible.get(i), tree2.getRoot().getOwnChips(), tree2.getRoot().getOtherChips());
			
//			scoreOld = getFastTreshold(tmp1[0], tmp1[1]);
			
			
			long blackTMP1 = tmp1[0];
			long whiteTMP1 = tmp1[1];
			
			
			GameTreeNode nodeToAdd1 = new GameTreeNode(blackTMP1, whiteTMP1, i, 0, true);
			
			tree2.getRoot().addChildren(nodeToAdd1);
			
			int dep = depth - 1;
			
			if(getOtherChips() == 0L){
				return nodeToAdd1;
			}
			
			if(possibleMovesLONG(startNode.getOtherChips(), startNode.getOwnChips()).keySet().size() == 0){
				return nodeToAdd1;
			}
			
			if(i == 0 || i == 7 || i == 56 || i == 63){
				return nodeToAdd1;
			}
			
			int toCompare = miniMaxAlphaBetaPruningWithTree(dep, nodeToAdd1, false, Integer.MIN_VALUE, Integer.MAX_VALUE, last);
			
			if (bestInt < toCompare) {
				bestInt = toCompare;
				bestNode = nodeToAdd1;
			}
		}
		return bestNode;
	}

	/**
	 * the recursive part of the MiniMax with alpha-beta-pruning
	 * @param depth
	 * @param starNode
	 * @param maximazing
	 * @param alpha
	 * @param beta
	 * @param last
	 * @return
	 */
	public int miniMaxAlphaBetaPruningWithTree(int depth, GameTreeNode starNode, boolean maximazing, int alpha, int beta, boolean last) {

		ConcurrentHashMap<Integer, Long> possible;
		
		if (maximazing) {

			possible = possibleMovesLONG(starNode.getOwnChips(), starNode.getOtherChips());

		} else {

			possible = possibleMovesLONG(starNode.getOtherChips(), starNode.getOwnChips());
				
		}
		
		long timeUsed = System.currentTimeMillis() - startTime;

		if (possible.keySet().size() == 0 || depth == 0 || timeAvailable < timeUsed) {

			if(possible.keySet().size() == 0){
			
				if(feldIstVoll(starNode.getOwnChips(), starNode.getOtherChips())){
					int blacks = countChips(starNode.getOwnChips());
					int whites = countChips(starNode.getOtherChips());
					if(blacks > whites){
						return Integer.MAX_VALUE-(whites)-1;
					}else if(whites > blacks){
						return Integer.MIN_VALUE+(blacks)+1;
					}else{
						return 0;
					}
				}
				ConcurrentHashMap<Integer, Long> pTMP;
				if(maximazing){
					pTMP = possibleMovesLONG(starNode.getOtherChips(), starNode.getOwnChips());
				}else{
					pTMP = possibleMovesLONG(starNode.getOwnChips(), starNode.getOtherChips());
				}
				if(pTMP.keySet().size() == 0){
					int blacks = countChips(starNode.getOwnChips());
					int whites = countChips(starNode.getOtherChips());
					if(blacks > whites){
						return Integer.MAX_VALUE-(whites)-1;
					}else if(whites > blacks){
						return Integer.MIN_VALUE+(blacks)+1;
					}else{
						return 0;
					}
				}
			}

			return countTreshold(starNode.getOwnChips(), starNode.getOtherChips());

		}

		if (maximazing) {

			int maxWert = alpha;
			for (Integer i : possible.keySet()) {

				Long[] tmp1;
				int scoreOld;

				tmp1 = updateForTreeBlackLONG(i, possible.get(i), starNode.getOwnChips(), starNode.getOtherChips());
				
//				scoreOld = getFastTreshold(starNode.getOwnChips(), starNode.getOtherChips());

				long blackTMP1 = tmp1[0];
				long whiteTMP1 = tmp1[1];

				GameTreeNode nodeToAdd1 = new GameTreeNode(blackTMP1, whiteTMP1, i, 0, true);
				
				starNode.addChildren(nodeToAdd1);

				int moveStat;
				
//				if(starNode.getOldScore()*0.2 > scoreOld){
//					moveStat = miniMaxAlphaBetaPruningWithTree(0, nodeToAdd1, false, alpha, beta, last);
//				}else if(starNode.getOldScore()*10 < scoreOld){
//					return Integer.MAX_VALUE-1;
//				}else{
//					int dep = depth - 1;
//					moveStat = miniMaxAlphaBetaPruningWithTree(dep, nodeToAdd1, false, maxWert, beta, last);									
//				}
				
//				if(posMatrixArray[i] < 0){
//					moveStat = miniMaxAlphaBetaPruningWithTree(0, nodeToAdd1, false, alpha, beta, last);
//				}else if(posMatrixArray[i] > 90){
//					return Integer.MAX_VALUE-1;
//				}else{
					int dep = depth - 1;
					moveStat = miniMaxAlphaBetaPruningWithTree(dep, nodeToAdd1, false, maxWert, beta, last);									
//				}
				
				if(moveStat > maxWert){
					maxWert = moveStat;
					if(maxWert >= beta){
						break;
					}
				}				
			}
			return maxWert;				

		} else {

			int minWert = beta;
			for (Integer i : possible.keySet()) {

				Long[] tmp1;
				int scoreOld;
				
				tmp1 = updateForTreeWhiteLONG(i, possible.get(i), starNode.getOwnChips(), starNode.getOtherChips());
					
//				scoreOld = getFastTreshold(starNode.getOtherChips(), starNode.getOwnChips());

				long blackTMP1 = tmp1[0];
				long whiteTMP1 = tmp1[1];

				GameTreeNode nodeToAdd1 = new GameTreeNode(blackTMP1, whiteTMP1, i, 0, false);
				starNode.addChildren(nodeToAdd1);

				int dep = depth - 1;
				int moveStat = miniMaxAlphaBetaPruningWithTree(dep, nodeToAdd1, true, alpha, minWert, last);
				
				if(moveStat < minWert){
					minWert = moveStat;
					if(minWert <= alpha){
						break;
					}
				}
			}
			return minWert;
		}

	}

	
	/*
	 *	------------------Computational methods below --------------
	 */
	
	// update the matrix during the game
	// ------------------------------------------- ToDo: better dynamic implementation
	public void updateMatrix() {
		// change the value for corner-close spaces if corner is occupied
		if (((getOwnChips() >> 0) & 1) == 1) {
			posMatrixArray2[1] = 11;
			posMatrixArray2[9] = 2;
			posMatrixArray2[8] = 11;
			
			posMatrixArray[1] = 100;
			posMatrixArray[8] = 100;
		}
		if (((getOwnChips() >> 7) & 1) == 1) {
			posMatrixArray2[6] = 11;
			posMatrixArray2[14] = 2;
			posMatrixArray2[15] = 11;
			
			posMatrixArray[6] = 100;
			posMatrixArray[15] = 100;
		}
		if (((getOwnChips() >> 56) & 1) == 1) {
			posMatrixArray2[48] = 11;
			posMatrixArray2[49] = 2;
			posMatrixArray2[57] = 11;
			
			posMatrixArray[48] = 100;
			posMatrixArray[57] = 100;
		}
		if (((getOwnChips() >> 63) & 1) == 1) {
			posMatrixArray2[54] = 2;
			posMatrixArray2[55] = 11;
			posMatrixArray2[62] = 11;
			
			posMatrixArray[55] = 100;
			posMatrixArray[62] = 100;
		}
	}
	
	public int[] getCurrentMatrix(long own){
		
		int[] out = posMatrixArray;
		
		for (int i = 0; i < 64; i++) {
			if(isStable(i, own)){
				out[i] = 99;
			}
		}
		return out;
	}
	
	public int getFastTreshold2(long blacks, long whites){
		
		int stableBlack = 0;
		int stableWhite = 0;
		
		for (int i = 0; i < 64; i++) {
			if (((blacks >> i) & 1) == 1) {
				if(isStable(i, blacks) || isStable2(i, blacks, whites)){
					stableBlack += 1;
				}
			}
			if (((whites >> i) & 1) == 1) {
				if(isStable(i, whites) || isStable2(i, whites, blacks)){
					stableWhite += 1;
				}
			}
		}
		
		return (stableBlack-stableWhite);
	}
	
	
	/**
	 * method counts all chips of given color
	 * 
	 * @param chips , chips of a color as bit representation
	 * @return amount of chips on board
	 */
	public int countChips(long chips){
		int chipCount = 0;
		
		for (int i = 0; i < 64; i++) {
			if (((chips >> i) & 1) == 1) {
				chipCount++;
			}
		}
		return chipCount;
	}
	
	/**
	 * method calculates a heuristic score for a specific board constellation
	 * 
	 * CornerOccupancy: corners per player
	 * CornerCloseness: discs close to a empty corner per player
	 * Mobility: possible moves per player
	 * StableDiscs: stable disks per player
	 * 
	 * @param toCount , own board as bit representation
	 * @param toCountEnemy , enemy board as bit representation
	 * @return score , specific score for this board
	 */
	public int countTreshold(long toCount, long toCountEnemy){
		
		int stability = 0;
		int stableBlack = 0;
		int stableWhite = 0;
		
		int cornerBLACK = 0;
		int cornerWHITE = 0;
		
		boolean corner0B = false;
		boolean corner7B = false;
		boolean corner56B = false;
		boolean corner63B = false;
		
		boolean corner0W = false;
		boolean corner7W = false;
		boolean corner56W = false;
		boolean corner63W = false;

		int closeToCornerBLACK0 = 0;
		int closeToCornerBLACK7 = 0;
		int closeToCornerBLACK56 = 0;
		int closeToCornerBLACK63 = 0;

		int closeToCornerWHITE0 = 0;
		int closeToCornerWHITE7 = 0;
		int closeToCornerWHITE56 = 0;
		int closeToCornerWHITE63 = 0;
		
		int cornerCloseness = 0;
		
		int corner = 0;
		
		int mobility = 0;
		
		for (int i = 0; i < 64; i++) {
			if (((toCount >> i) & 1) == 1) {
				if(isStable(i, toCount) || isStable2(i, toCount, toCountEnemy)){
					stableBlack += 1;
				}
				if (i == 0 || i == 7 || i == 56 || i == 63) {
					cornerBLACK++;
					if (i == 0 && !corner0B) {
						corner0B = true;
					} else if (i == 7 && !corner7B) {
						corner7B = true;
					} else if (i == 56 && !corner56B) {
						corner56B = true;
					} else if (i == 63 && !corner63B) {
						corner63B = true;
					}
				}
				if (i == 1 || i == 8 || i == 9) {
					if(closeToCornerBLACK0 < 3){
						closeToCornerBLACK0++;						
					}
				}
				if (i == 6 || i == 14 || i == 15) {
					if(closeToCornerBLACK7 < 3){
						closeToCornerBLACK7++;						
					}
				}
				if (i == 48 || i == 49 || i == 57) {
					if(closeToCornerBLACK56 < 3){
						closeToCornerBLACK56++;						
					}
				}
				if (i == 54 || i == 5 || i == 62) {
					if(closeToCornerBLACK63 < 3){
						closeToCornerBLACK63++;						
					}
				}
			}
			if (((toCountEnemy >> i) & 1) == 1) {
				if(isStable(i, toCountEnemy) || isStable2(i, toCountEnemy, toCount)){
					stableWhite += 1;
				}
				if (i == 0 || i == 7 || i == 56 || i == 63) {
					cornerWHITE++;
					if (i == 0 && !corner0W) {
						corner0W = true;
					} else if (i == 7 && !corner7W) {
						corner7W = true;
					} else if (i == 56 && !corner56W) {
						corner56W = true;
					} else if (i == 63 && !corner63W) {
						corner63W = true;
					}
				}
				if (i == 1 || i == 8 || i == 9) {
					if(closeToCornerWHITE0 < 3){
						closeToCornerWHITE0++;						
					}
				}
				if (i == 6 || i == 14 || i == 15) {
					if(closeToCornerWHITE7 < 3){
						closeToCornerWHITE7++;						
					}
				}
				if (i == 48 || i == 49 || i == 57) {
					if(closeToCornerWHITE56 < 3){
						closeToCornerWHITE56++;						
					}
				}
				if (i == 54 || i == 55 || i == 62) {
					if(closeToCornerWHITE63 < 3){
						closeToCornerWHITE63++;						
					}
				}
			}
		}
		
		if (corner0B) {
			closeToCornerBLACK0 = 0;
		}
		if (corner7B) {
			closeToCornerBLACK7 = 0;
		}
		if (corner56B) {
			closeToCornerBLACK56 = 0;
		}
		if (corner63B) {
			closeToCornerBLACK63 = 0;
		}
		
		if (corner0W) {
			closeToCornerWHITE0 = 0;
		}
		if (corner7W) {
			closeToCornerWHITE7 = 0;
		}
		if (corner56W) {
			closeToCornerWHITE56 = 0;
		}
		if (corner63W) {
			closeToCornerWHITE63 = 0;
		}
		
		ConcurrentHashMap<Integer, Long> posEnemy = possibleMovesLONG(toCountEnemy, toCount); 
		ConcurrentHashMap<Integer, Long> posMe = possibleMovesLONG(toCount, toCountEnemy);
		
		mobility = posMe.keySet().size()*mobilityWeight - posEnemy.keySet().size()*mobilityWeight;

		cornerCloseness =  closeToCornerWeight * (closeToCornerWHITE0 + closeToCornerWHITE7 + closeToCornerWHITE56 + closeToCornerWHITE63) - closeToCornerWeight * (closeToCornerBLACK0 + closeToCornerBLACK7 + closeToCornerBLACK56 + closeToCornerBLACK63);
		
		corner = cornerBLACK * cornerWeight - cornerWHITE * cornerWeight;
		
		stability = stableBlack * stabilityWeight - stableWhite * stabilityWeight;
		
		return stability + mobility + corner + cornerCloseness;
	}
	

	
	/**
	 * method checks if disk on given position is stable
	 * because it is surrounded only by the same disks
	 * 
	 * @param pos , position to check
	 * @param chips , chips of given color as bit representation
	 * @return true if disk on given position is stable
	 */
	public boolean isStable(int pos, long chips){
		
		boolean waagrecht = false;
		boolean senkrecht = false;
		boolean schraegLinksUnten = false;
		boolean schraegRechtsUnten = false;
		
		int waag = pos;
		while(!waagrecht){
			if(((chips >> waag) & 1) != 1){
				break;
			}else{
				if((waag%8) == 0){
					waagrecht = true;
				}
			}
			waag--;
		}
		
		waag = pos;
		while(!waagrecht){
			
			if(((chips >> waag) & 1) != 1){
				break;
			}else{
				if(((waag+1)%8) == 0){
					waagrecht = true;
				}
			}
			waag++;
		}
		if(!waagrecht){
			return false;
		}
		
		int senk = pos;
		while(!senkrecht){
			if(((chips >> senk) & 1) != 1){
				break;
			}else{
				if(senk < 8){
					senkrecht = true;
				}
			}
			senk -= 8;
		}
		
		senk = pos;
		while(!senkrecht){
			if(((chips >> senk) & 1) != 1){
				break;
			}else{
				if(senk > 55){
					senkrecht = true;
				}
			senk += 8;
			}
		}
		if(!senkrecht){
			return false;
		}
		
		int diagL = pos;
		while(!schraegLinksUnten){
			if(((chips >> diagL) & 1) != 1){
				break;
			}else{
				if((diagL % 8) == 0 || diagL > 55){
					schraegLinksUnten = true;
				}
			}
			diagL += 7;
		}
		
		diagL = pos;
		while(!schraegLinksUnten){
			if(((chips >> diagL) & 1) != 1){
				break;
			}else{
				if(((diagL+1)%8) == 0 || diagL < 8){
					schraegLinksUnten = true;
				}
			}
			diagL -= 7;
		}
		
		if(!schraegLinksUnten){
			return false;
		}
		
		int diagR = pos;
		while(!schraegRechtsUnten){
			if(((chips >> diagR) & 1) != 1){
				break;
			}else{
				if((diagR % 8) == 0 || diagR < 8){
					schraegRechtsUnten = true;
				}
			}
			diagR -= 9;
		}
		
		diagR = pos;
		while(!schraegRechtsUnten){
			if(((chips >> diagR) & 1) != 1){
				break;
			}else{
				if(((diagR+1)%8) == 0 || diagR > 55){
					schraegRechtsUnten = true;
				}
			diagR += 9;
			}
		}
		
		if(!schraegRechtsUnten){
			return false;
		}
		
		return true;
	}
	

	/**
	 * method checks if disk on given position is stable
	 * because there is no possibility to turn ist again
	 * 
	 * @param pos , position to check
	 * @param ownChips , own chips as bit representation
	 * @param otherChips , other chips as bit representation
	 * @return
	 */
	public boolean isStable2(int pos, long ownChips, long otherChips){
		
		boolean waagrecht = false;
		boolean senkrecht = false;
		boolean schraegLinksUnten = false;
		boolean schraegRechtsUnten = false;
		
		boolean waagrecht2 = false;
		boolean senkrecht2 = false;
		boolean schraegLinksUnten2 = false;
		boolean schraegRechtsUnten2 = false;
		
		boolean wGesamt = false;
		boolean sGesamt = false;
		boolean slGesamt = false;
		boolean srGesamt = false;
		
		int waag = pos;
		
		while(!waagrecht){
			if(((ownChips >> waag) & 1) != 1){
				if(((otherChips >> waag) & 1) != 1){
					break;
				}
			}
			if((waag%8) == 0){
				waagrecht = true;
			}
			
			waag--;
		}
		
		waag = pos;
		
		while(!waagrecht2){
			
			if(((ownChips >> waag) & 1) != 1){
				if(((otherChips >> waag) & 1) != 1){
					break;
				}
			}
			if(((waag+1)%8) == 0){
				waagrecht2 = true;
			}
			
			waag++;
		}
		
		if(waagrecht && waagrecht2){
			wGesamt = true;
		}
		
		if(!wGesamt){
			return false;
		}
		
		//-----------------------------
		
		int senk = pos;
		while(!senkrecht){
			if(((ownChips >> senk) & 1) != 1){
				if(((otherChips >> senk) & 1) != 1){
					break;
				}
			}
			if(senk < 8){
				senkrecht = true;
			}
			
			senk -= 8;
		}
		
		senk = pos;
		while(!senkrecht2){
			if(((ownChips >> senk) & 1) != 1){
				if(((otherChips >> senk) & 1) != 1){
					break;
				}
			}
			if(senk > 55){
				senkrecht2 = true;
			}
			senk += 8;
		}
		
		if(senkrecht && senkrecht2){
			sGesamt = true;
		}
		
		if(!sGesamt){
			return false;
		}
		
		//--------------------------------
		
		int diagL = pos;
		while(!schraegLinksUnten){
			if(((ownChips >> diagL) & 1) != 1){
				if(((otherChips >> diagL) & 1) != 1){
					break;
				}
			}
			
			if((diagL % 8) == 0 || diagL > 55){
				schraegLinksUnten = true;
			}
			
			diagL += 7;
		}
		
		diagL = pos;
		while(!schraegLinksUnten2){
			if(((ownChips >> diagL) & 1) != 1){
				if(((otherChips >> diagL) & 1) != 1){
					break;
				}
			}
			if(((diagL+1)%8) == 0 || diagL < 8){
				schraegLinksUnten2 = true;
			}
			
			diagL -= 7;
		}
		
		if(schraegLinksUnten && schraegLinksUnten2){
			slGesamt = true;
		}
		
		if(!slGesamt){
			return false;
		}
		
		//-------------------------
		
		int diagR = pos;
		while(!schraegRechtsUnten){
			if(((ownChips >> diagR) & 1) != 1){
				if(((otherChips >> diagR) & 1) != 1){
					break;
				}
			}
			if((diagR % 8) == 0 || diagR < 8){
				schraegRechtsUnten = true;
			}
			
			diagR -= 9;
		}
		
		diagR = pos;
		while(!schraegRechtsUnten2){
			if(((ownChips >> diagR) & 1) != 1){
				if(((otherChips >> diagR) & 1) != 1){
					break;
				}
			}
			if(((diagR+1)%8) == 0 || diagR > 55){
				schraegRechtsUnten2 = true;
			}
			diagR += 9;
			
		}
		
		if(schraegRechtsUnten && schraegRechtsUnten2){
			srGesamt = true;
		}
		
		if(!srGesamt){
			return false;
		}
		//-------------------------
		return true;
	}

	/**
	 * generates the chips for each player at the beginning of the game
	 * 
	 * @param firstPLayer , 0 if player starts and 1 if enemy starts
	 */
	public void genOwnChips(int firstPLayer) {

		if (firstPLayer == 0) {
			ownChips = 34628173824L;
			otherChips = 68853694464L;
		} else {
			ownChips = 68853694464L;
			otherChips = 34628173824L;
		}
	}

	/**
	 * returns the player chips
	 * 
	 * @return long, own chips as bit representation
	 */
	public long getOwnChips() {
		return ownChips;
	}

	/**
	 * returns the enemy chips
	 * 
	 * @return long , enemy chips as bit representation
	 */
	public long getOtherChips() {
		return otherChips;
	}

	/**
	 * checks if the board is completely filled
	 * 
	 * @param player
	 * @param enemy
	 * @return
	 */
	public boolean isBeendet(long player, long enemy) {

		if ((player | enemy) == -1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * main method to get the possible moves for the AI
	 * 
	 * @param ownchips AI chips as bit representation
	 * @param otherchips Enemy chips as bit representation
	 * @return HashMap containing all possible moves as key and the chips to turn as value
	 */
	public ConcurrentHashMap<Integer, Long> possibleMovesLONG(long ownchips, long otherchips){
		ConcurrentHashMap<Integer, Long> toTurn2 = new ConcurrentHashMap<Integer, Long>();
		
		toTurn2.clear();
		
		leftLONG = 0L;
		rightLONG = 0L;
		upLONG = 0L;
		downLONG = 0L;
		upLeftLONG = 0L;
		upRightLONG = 0L;
		downLeftLONG = 0L;
		downRightLONG = 0L;
		
		berechneZuege(otherchips, ownchips, toTurn2);
		
		for(Integer i : toTurn2.keySet()){
			if(toTurn2.get(i) == 0L){
				toTurn2.remove(i);
			}
		}
		
		return toTurn2;
	}
	
	/**
	 * main method to get the possible moves for the AI
	 * 
	 * @param enemyMove integer between 0 and 63 being the enemy move
	 * @param ownchips AI chips as bit representation
	 * @param otherchips Enemy chips as bit representation
	 * @return HashMap containing all possible moves as key and the chips to turn as value
	 */
	public ConcurrentHashMap<Integer, Long> possibleMovesEnemyLONG(int enemyMove, long otherchips, long ownchips){
		
		ConcurrentHashMap<Integer, Long> toTurnEnemy2 = new ConcurrentHashMap<Integer, Long>();

		toTurnEnemy2.clear();

		leftLONG = 0L;
		rightLONG = 0L;
		upLONG = 0L;
		downLONG = 0L;
		upLeftLONG = 0L;
		upRightLONG = 0L;
		downLeftLONG = 0L;
		downRightLONG = 0L;
		
		berechneZuegeGegner(enemyMove, ownchips, otherchips, toTurnEnemy2);
		
		for(Integer i : toTurnEnemy2.keySet()){
			if(toTurnEnemy2.get(i) == 0L){
				toTurnEnemy2.remove(i);
			}
		}
		
		return toTurnEnemy2;
	}

	/**
	 * main method to get the possible moves for the AI which calls all the direction methods
	 * 
	 * @param gegner Enemys chips as bit representation
	 * @param spieler AIs chips as bit representation
	 * @param toTurn HashMap containing all possible moves (Integer) as key and all chips to turn as value
	 */
	public void berechneZuege(long gegner, long spieler, ConcurrentHashMap<Integer, Long> toTurn) {
		for (int i = 0; i < 64; i++) {
			if (((spieler >> i) & 1) == 1) {
				gehtUnten(gegner, spieler, i, toTurn);
				gehtLinks(gegner, spieler, i, toTurn);
				gehtRechts(gegner, spieler, i, toTurn);
				getOben(gegner, spieler, i, toTurn);
				gehtUntenLinks(gegner, spieler, i, toTurn);
				gehtUntenRechts(gegner, spieler, i, toTurn);
				gehtObenLinks(gegner, spieler, i, toTurn);
				gehtObenRechts(gegner, spieler, i, toTurn);
			}
		}
	}

	/**
	 * main method to get the possible moves for the enemy which calls all the direction methods
	 * 
	 * @param enemyMove integer between 0 and 63 for the enemy move
	 * @param gegner Enemys chips as bit representation
	 * @param spieler AIs chips as bit representation
	 * @param toTurnEnemy HashMap containing all possible moves (Integer) as key and all chips to turn as value
	 */
	public void berechneZuegeGegner(int enemyMove, long gegner, long spieler,
			ConcurrentHashMap<Integer, Long> toTurnEnemy) {

		gehtUntenEnemy(enemyMove, toTurnEnemy);
		gehtLinksEnemy(enemyMove, toTurnEnemy);
		gehtRechtsEnemy(enemyMove, toTurnEnemy);
		getObenEnemy(enemyMove, toTurnEnemy);
		gehtUntenLinksEnemy(enemyMove, toTurnEnemy);
		gehtUntenRechtsEnemy(enemyMove, toTurnEnemy);
		gehtObenLinksEnemy(enemyMove, toTurnEnemy);
		gehtObenRechtsEnemy(enemyMove, toTurnEnemy);

	}


	/**
	 * method updates the board, after the black player made a move
	 * 
	 * @param dezimalEnemy , integer between 0 and 63 which is the move the player made
	 * @param tmp , disks to turn for the move in bit representation
	 * @param blacks , black chips as bit representation
	 * @param whites , white chips as bit representation
	 * @return Long[] , with new black chips at [0] and new white chips at [1]
	 */
	public Long[] updateForTreeBlackLONG(int dezimalEnemy, long tmp, long blacks, long whites){
		long big = 0L;
		long dezLong = (long) Math.pow(2, dezimalEnemy);
		
		big = big | (1L << 63);
		
		if (dezimalEnemy == 63) {
			blacks = blacks | big;
		} else {
			blacks = blacks | dezLong;
		}
		
		blacks = blacks | tmp;
		whites = whites & ~(tmp);

		return new Long[] {blacks, whites};
	}
	
	/**
	 * method updates the board, after the white player made a move
	 * 
	 * @param dezimalEnemy , integer between 0 and 63 which is the move the player made
	 * @param tmp , disks to turn for the move in bit representation
	 * @param blacks , black chips as bit representation
	 * @param whites , white chips as bit representation
	 * @return Long[] , with new black chips at [0] and new white chips at [1]
	 */
	public Long[] updateForTreeWhiteLONG(int dezimalEnemy, long tmp, long blacks, long whites){
		long big = 0L;
		long dezLong = (long) Math.pow(2, dezimalEnemy);
		
		big = big | (1L << 63);
		
		if (dezimalEnemy == 63) {
			whites = whites | big;
		} else {
			whites = whites | dezLong;
		}
		
		whites = whites | tmp;
		blacks = blacks & ~(tmp);

		return new Long[] {blacks, whites};
	}
	
	/**
	 * method gets the position (between 0 and 63) of the AIs move to update the internal boards
	 * 
	 * @param dezimal Integer between 0 and 63
	 * @param toTurn HashMap with possible turns and the positions to turn
	 * @return Integer which it got
	 */
	public int makeMove(int dezimal, ConcurrentHashMap<Integer, Long> toTurn) {

		Long tmp = toTurn.get(dezimal);
		
		if(tmp == 0L){
			return -1;
		}

		long big = 0L;
		long dezLong = (long) Math.pow(2, dezimal);

		big = big | (1L << 63);

		if (dezimal == 63) {
			ownChips = ownChips | big;
		} else {
			ownChips = ownChips | dezLong;
		}
		
		ownChips = ownChips | tmp;
		otherChips = otherChips & ~(tmp);

		return dezimal;
	}
	
	/**
	 * methods prints a board to the console
	 * 
	 * EDIT: was used in testing and is not used in battle
	 * 
	 * @param blackChips black chips
	 * @param whiteChips white chips
	 */
	public void spielfeldAusgeben(long blackChips, long whiteChips) {
		String spielfeld[][] = new String[8][8];
		for (int i = 0; i < 64; i++) {
			spielfeld[i / 8][i % 8] = " ";
		}

		for (int i = 0; i < 64; i++) {
			if (((blackChips >> i) & 1) == 1) {
				spielfeld[i / 8][i % 8] = "s";
			}
			if (((whiteChips >> i) & 1) == 1) {
				spielfeld[i / 8][i % 8] = "w";
			}
		}
		for (int i = 0; i < 8; i++) {
			System.out.println(Arrays.toString(spielfeld[i]));
		}
	}
	
	/**
	 * method updates the internal board for an incoming enemy move
	 * 
	 * @param dezimalEnemy integer between 0 and 63 which is the move the enemy made
	 */
	public void update(int dezimalEnemy) {

		enemyMove = dezimalEnemy;

		ConcurrentHashMap<Integer, Long> toTurn = possibleMovesEnemyLONG(dezimalEnemy, otherChips, ownChips);

		Long tmp = 0L;

		for(Integer i : toTurn.keySet()){
			tmp = tmp | toTurn.get(i);
		}

		long big = 0L;
		long dezLong = (long) Math.pow(2, dezimalEnemy);

		big = big | (1L << 63);

		if (dezimalEnemy == 63) {
			otherChips = otherChips | big;
		} else {
			otherChips = otherChips | dezLong;
		}
		
		otherChips = otherChips | tmp;
		ownChips = ownChips & ~(tmp);

	}
	
	/**
	 * method check every direction for possible moves, these are used for the AI
	 * 
	 * @param gegner
	 * @param spieler
	 * @param pos
	 * @param toTurn
	 */
	
	public void gehtUnten(long gegner, long spieler, int pos, ConcurrentHashMap<Integer, Long> toTurn) {
		if (pos + 8 > 63) {
			
			upLONG = 0L;

			return;
		} else if (((gegner >> pos + 8) & 1) == 1) {
			upLONG = upLONG | (long) Math.pow(2, pos+8);

			gegnerDazwischen = true;
			gehtUnten(gegner, spieler, pos + 8, toTurn);

		} else if (gegnerDazwischen && ((spieler >> pos + 8) & 1) != 1) {

			if (!toTurn.containsKey(pos + 8)) {
				toTurn.put(pos+8, upLONG);
			}else{
				toTurn.put(pos+8, toTurn.get(pos+8) | upLONG);
			}
			
			upLONG = 0L;

			gegnerDazwischen = false;
		} else {
			upLONG = 0L;
		}
	}

	public void gehtLinks(long gegner, long spieler, int pos, ConcurrentHashMap<Integer, Long> toTurn) {

		if ((pos - 1) % 8 == 7 || (pos - 1) < 0) {
			rightLONG = 0L;
			
			return;
		} else if (((gegner >> pos - 1) & 1) == 1) {
	
			rightLONG = rightLONG | (long) Math.pow(2, pos-1);

			gegnerDazwischen = true;
			gehtLinks(gegner, spieler, pos - 1, toTurn);
		} else if (gegnerDazwischen && ((spieler >> pos - 1) & 1) != 1) {

			if (!toTurn.containsKey(pos - 1)) {
				toTurn.put(pos-1, rightLONG);
			}else{
				toTurn.put(pos-1, toTurn.get(pos-1) | rightLONG);
			}
			
			rightLONG = 0L;

			gegnerDazwischen = false;
		} else {
			rightLONG = 0L;
		}
	}

	public void gehtUntenLinks(long gegner, long spieler, int pos, ConcurrentHashMap<Integer, Long> toTurn) {
		if (pos + 7 > 63 || (pos + 7) % 8 == 7) {
			upRightLONG = 0L;
			
			return;
		} else if (((gegner >> pos + 7) & 1) == 1) {
			upRightLONG = upRightLONG | (long) Math.pow(2, pos+7);

			gegnerDazwischen = true;
			gehtUntenLinks(gegner, spieler, pos + 7, toTurn);
		} else if (gegnerDazwischen && ((spieler >> pos + 7) & 1) != 1) {

			if (!toTurn.containsKey(pos + 7)) {
				toTurn.put(pos+7, upRightLONG);
			}else{
				toTurn.put(pos+7, toTurn.get(pos+7) | upRightLONG);
			}
			upRightLONG = 0L;

			gegnerDazwischen = false;
		} else {
			upRightLONG = 0L;
		}
	}

	public void gehtRechts(long gegner, long spieler, int pos, ConcurrentHashMap<Integer, Long> toTurn) {
		if ((pos + 1) % 8 == 0 || (pos + 1) > 63) {
			leftLONG = 0L;
			
			return;
		} else if (((gegner >> pos + 1) & 1) == 1) {
			leftLONG = leftLONG | (long) Math.pow(2, pos+1);

			gegnerDazwischen = true;
			gehtRechts(gegner, spieler, pos + 1, toTurn);
		} else if (gegnerDazwischen && ((spieler >> pos + 1) & 1) != 1) {

			if (!toTurn.containsKey(pos + 1)) {
				toTurn.put(pos+1, leftLONG);
			}else{
				toTurn.put(pos+1, toTurn.get(pos+1) | leftLONG);
			}
			
			leftLONG = 0L;

			gegnerDazwischen = false;
		} else {
			leftLONG = 0L;
		}
	}

	public void gehtUntenRechts(long gegner, long spieler, int pos, ConcurrentHashMap<Integer, Long> toTurn) {
		if ((pos + 9) > 63 || (pos + 9) % 8 == 0) {
			
			upLeftLONG = 0L;
			
			return;
		} else if (((gegner >> pos + 9) & 1) == 1) {
			upLeftLONG = upLeftLONG | (long) Math.pow(2, pos+9);

			gegnerDazwischen = true;
			gehtUntenRechts(gegner, spieler, pos + 9, toTurn);
		} else if (gegnerDazwischen && ((spieler >> pos + 9) & 1) != 1) {

			if (!toTurn.containsKey(pos + 9)) {
				toTurn.put(pos+9, upLeftLONG);
			}else{
				toTurn.put(pos+9, toTurn.get(pos+9) | upLeftLONG);
			}
			
			upLeftLONG = 0L;

			gegnerDazwischen = false;
		} else {
			upLeftLONG = 0L;
		}
	}

	public void getOben(long gegner, long spieler, int pos, ConcurrentHashMap<Integer, Long> toTurn) {
		if (pos - 8 < 0) {
			downLONG = 0L;
			
			return;
		} else if (((gegner >> pos - 8) & 1) == 1) {
			downLONG = downLONG | (long) Math.pow(2, pos-8);

			gegnerDazwischen = true;
			getOben(gegner, spieler, pos - 8, toTurn);
		} else if (gegnerDazwischen && ((spieler >> pos - 8) & 1) != 1) {

			if (!toTurn.containsKey(pos - 8)) {
				toTurn.put(pos-8, downLONG);
			}else{
				toTurn.put(pos-8, toTurn.get(pos-8) | downLONG);
			}
			
			downLONG = 0L;

			gegnerDazwischen = false;
		} else {
			downLONG = 0L;
		}
	}
	
	public void gehtObenRechts(long gegner, long spieler, int pos, ConcurrentHashMap<Integer, Long> toTurn) {
		if (pos - 7 < 0 || (pos - 7) % 8 == 0) {
			
			downLeftLONG = 0L;
			
			return;
		} else if (((gegner >> pos - 7) & 1) == 1) {
			
			downLeftLONG = downLeftLONG | (long) Math.pow(2, pos-7);

			gegnerDazwischen = true;
			gehtObenRechts(gegner, spieler, pos - 7, toTurn);
		} else if (gegnerDazwischen && ((spieler >> pos - 7) & 1) != 1) {

			if (!toTurn.containsKey(pos - 7)) {
				toTurn.put(pos-7, downLeftLONG);
			}else{
				toTurn.put(pos-7, toTurn.get(pos-7) | downLeftLONG);
			}
			downLeftLONG = 0L;

			gegnerDazwischen = false;
		} else {
			
			downLeftLONG = 0L;
		}
	}
	
	public void gehtObenLinks(long gegner, long spieler, int pos, ConcurrentHashMap<Integer, Long> toTurn) {
		if (pos - 9 < 0 || (pos - 9) % 8 == 7) {
			
			downRightLONG = 0L;
			
			return;
		} else if (((gegner >> pos - 9) & 1) == 1) {
			
			downRightLONG = downRightLONG | (long) Math.pow(2, pos-9);

			gegnerDazwischen = true;
			gehtObenLinks(gegner, spieler, pos - 9, toTurn);
		} else if (gegnerDazwischen && ((spieler >> pos - 9) & 1) != 1) {

			if (!toTurn.containsKey(pos - 9)) {
				toTurn.put(pos-9, downRightLONG);
			}else{
				toTurn.put(pos-9, toTurn.get(pos-9) | downRightLONG);
			}

			downRightLONG = 0L;

			gegnerDazwischen = false;
		} else {
			
			downRightLONG = 0L;
		}
	}

	/**
	 * methods check every direction for possible moves, these are used for the enemy
	 * 
	 * @param pos , int between 0 and 63
	 * @param toTurn , HashMap
	 */

	public void gehtUntenEnemy(int pos, ConcurrentHashMap<Integer, Long> toTurn) {
		if (pos + 8 > 63) {
			upLONG = 0L;
			return;
		} else if (((ownChips >> pos + 8) & 1) == 1) {

			upLONG = upLONG | (long)  Math.pow(2, pos+8);

			gegnerDazwischen = true;
			gehtUntenEnemy(pos + 8, toTurn);

		} else if (gegnerDazwischen && ((otherChips >> pos + 8) & 1) == 1) {

			if (!toTurn.containsKey(enemyMove)) {
				toTurn.put(enemyMove, upLONG);
			}else{
				toTurn.put(enemyMove, toTurn.get(pos+8) | upLONG);
			}
			upLONG = 0L;

			gegnerDazwischen = false;
		} else {
			upLONG = 0L;
		}
	}
	
	public void gehtLinksEnemy(int pos, ConcurrentHashMap<Integer, Long> toTurn) {

		if ((pos - 1) % 8 == 7 || (pos - 1) < 0) {
			rightLONG = 0L;
			return;
		} else if (((ownChips >> pos - 1) & 1) == 1) {

			rightLONG = rightLONG | (long) Math.pow(2, pos-1);

			gegnerDazwischen = true;
			gehtLinksEnemy(pos - 1, toTurn);
		} else if (gegnerDazwischen && ((otherChips >> pos - 1) & 1) == 1) {

			if (!toTurn.containsKey(enemyMove)) {
				toTurn.put(enemyMove,rightLONG);
			}else{
				toTurn.put(enemyMove, toTurn.get(enemyMove) | rightLONG);
			}

			rightLONG = 0L;

			gegnerDazwischen = false;
		} else {
			rightLONG = 0L;
		}
	}

	public void gehtUntenLinksEnemy(int pos, ConcurrentHashMap<Integer, Long> toTurn) {
		if (pos + 7 > 63 || (pos + 7) % 8 == 7) {
			upRightLONG = 0L;
			return;
		} else if (((ownChips >> pos + 7) & 1) == 1) {

			upRightLONG = upRightLONG | (long) Math.pow(2, pos+7);

			gegnerDazwischen = true;
			gehtUntenLinksEnemy(pos + 7, toTurn);
		} else if (gegnerDazwischen && ((otherChips >> pos + 7) & 1) == 1) {

			if (!toTurn.containsKey(enemyMove)) {
				toTurn.put(enemyMove, upRightLONG);
			}else{
				toTurn.put(enemyMove, toTurn.get(enemyMove) | upRightLONG);
			}

			upRightLONG = 0L;

			gegnerDazwischen = false;
		} else {
			upRightLONG = 0L;
		}
	}

	public void gehtRechtsEnemy(int pos, ConcurrentHashMap<Integer, Long> toTurn) {
		if ((pos + 1) % 8 == 0 || (pos + 1) > 63) {
			leftLONG = 0L;
			return;
		} else if (((ownChips >> pos + 1) & 1) == 1) {

			leftLONG = leftLONG | (long) Math.pow(2, pos+1);

			gegnerDazwischen = true;
			gehtRechtsEnemy(pos + 1, toTurn);
		} else if (gegnerDazwischen && ((otherChips >> pos + 1) & 1) == 1) {

			if (!toTurn.containsKey(enemyMove)) {
				toTurn.put(enemyMove, leftLONG);
			}else{
				toTurn.put(enemyMove, toTurn.get(enemyMove) | leftLONG);
			}

			leftLONG = 0L;

			gegnerDazwischen = false;
		} else {
			leftLONG = 0L;
		}
	}

	public void gehtUntenRechtsEnemy(int pos, ConcurrentHashMap<Integer, Long> toTurn) {
		if ((pos + 9) > 63 || (pos + 9) % 8 == 0) {
			upLeftLONG = 0L;
			return;
		} else if (((ownChips >> pos + 9) & 1) == 1) {

			upLeftLONG = upLeftLONG | (long) Math.pow(2, pos+9);

			gegnerDazwischen = true;
			gehtUntenRechtsEnemy(pos + 9, toTurn);
		} else if (gegnerDazwischen && ((otherChips >> pos + 9) & 1) == 1) {

			if (!toTurn.containsKey(enemyMove)) {
				toTurn.put(enemyMove, upLeftLONG);
			}else{
				toTurn.put(enemyMove, toTurn.get(enemyMove) | upLeftLONG);
			}

			upLeftLONG = 0L;

			gegnerDazwischen = false;
		} else {
			upLeftLONG = 0L;
		}
	}

	public void getObenEnemy(int pos, ConcurrentHashMap<Integer, Long> toTurn) {
		if (pos - 8 < 0) {
			downLONG = 0L;
			return;
		} else if (((ownChips >> pos - 8) & 1) == 1) {

			downLONG = downLONG | (long) Math.pow(2, pos-8);

			gegnerDazwischen = true;
			getObenEnemy(pos - 8, toTurn);
		} else if (gegnerDazwischen && ((otherChips >> pos - 8) & 1) == 1) {

			if (!toTurn.containsKey(enemyMove)) {
				toTurn.put(enemyMove, downLONG);
			}else{
				toTurn.put(enemyMove, toTurn.get(enemyMove) | downLONG);
			}

			downLONG = 0L;

			gegnerDazwischen = false;
		} else {
			downLONG = 0L;
		}
	}

	public void gehtObenRechtsEnemy(int pos, ConcurrentHashMap<Integer, Long> toTurn) {
		if (pos - 7 < 0 || (pos - 7) % 8 == 0) {
			downLeftLONG = 0L;
			return;
		} else if (((ownChips >> pos - 7) & 1) == 1) {

			downLeftLONG = downLeftLONG | (long) Math.pow(2, pos-7);

			gegnerDazwischen = true;
			gehtObenRechtsEnemy(pos - 7, toTurn);
		} else if (gegnerDazwischen && ((otherChips >> pos - 7) & 1) == 1) {

			if (!toTurn.containsKey(enemyMove)) {
				toTurn.put(enemyMove, downLeftLONG);
			}else{
				toTurn.put(enemyMove, toTurn.get(enemyMove) | downLeftLONG);
			}

			downLeftLONG = 0L;

			gegnerDazwischen = false;
		} else {
			downLeftLONG = 0L;
		}
	}

	public void gehtObenLinksEnemy(int pos, ConcurrentHashMap<Integer, Long> toTurn) {
		if (pos - 9 < 0 || (pos - 9) % 8 == 7) {
			downRightLONG = 0L;
			return;
		} else if (((ownChips >> pos - 9) & 1) == 1) {

			downRightLONG = downRightLONG | (long) Math.pow(2, pos-9);

			gegnerDazwischen = true;
			gehtObenLinksEnemy(pos - 9, toTurn);
		} else if (gegnerDazwischen && ((otherChips >> pos - 9) & 1) == 1) {

			if (!toTurn.containsKey(enemyMove)) {
				toTurn.put(enemyMove, downRightLONG);
			}else{
				toTurn.put(enemyMove, toTurn.get(enemyMove) | downRightLONG);
			}

			downRightLONG = 0L;

			gegnerDazwischen = false;
		} else {
			downRightLONG = 0L;
		}
	}
}

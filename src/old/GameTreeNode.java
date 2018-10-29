package old;

import java.util.ArrayList;
import java.util.Arrays;

public class GameTreeNode{
	
	private GameTreeNode father;
	private ArrayList<GameTreeNode> children = new ArrayList<GameTreeNode>();
	
	private long black;
	private long white;
	private int dezimal;
	private int scoreFast;
	private boolean wasMax;
	
	public GameTreeNode(long black, long white, int dezimal, int scoreFast, boolean wasMax){
		this.black = black;
		this.white = white;
		this.dezimal = dezimal;
		this.scoreFast = scoreFast;
		this.wasMax = wasMax;
	}
	
	
	public long getOwnChips(){
		return this.black;
	}
	
	public long getOtherChips(){
		return this.white;
	}
	
	public int getDezimal(){
		return this.dezimal;
	}
	
	public boolean getWasMax(){
		return this.wasMax;
	}
	
	public int getOldScore(){
		return this.scoreFast;
	}

	public ArrayList<GameTreeNode> getChildren() {
		return children;
	}

	public void addChildren(GameTreeNode children) {
		this.children.add(children);
	}
	
	public void delChildren(GameTreeNode children){
		if(this.children.contains(children)){
			this.children.remove(children);			
		}
	}
	
	public void nodeAusgeben() {
		String spielfeld[][] = new String[8][8];
		for (int i = 0; i < 64; i++) {
			spielfeld[i / 8][i % 8] = " ";
		}

		for (int i = 0; i < 64; i++) {
			if (((this.black >> i) & 1) == 1) {
				spielfeld[i / 8][i % 8] = "s";
			}
			if (((this.white >> i) & 1) == 1) {
				spielfeld[i / 8][i % 8] = "w";
			}
		}
		for (int i = 0; i < 8; i++) {
			System.out.println(Arrays.toString(spielfeld[i]));
		}
	}

	public GameTreeNode getFather() {
		return father;
	}

	public void setFather(GameTreeNode father) {
		this.father = father;
	}
}
package version2;

import java.util.ArrayList;

public class GameTree {

    private GameTreeNode root;

    private ArrayList<GameTreeNode> leafNodes = new ArrayList<GameTreeNode>();

    public GameTree(){

    }

    public void setRoot(GameTreeNode newRoot){
        if(this.root == null){
            this.root = newRoot;
        }
    }

//	public void updateLeafNodes(){
//		for(GameTreeNode n : leafNodes){
//			if(n.getChildren().size() != 0){
//				leafNodes.remove(n);
//			}
//		}
//	}

    public ArrayList<GameTreeNode> getLeafNodes(){
        return leafNodes;
    }

    public GameTreeNode getRoot(){
        return this.root;
    }

    public void addNodeToParent(GameTreeNode parent, GameTreeNode child){

        child.setFather(parent);

        GameTreeNode tmp = parent;

        while(tmp != root){
            tmp.addChildren(child);
            tmp = tmp.getFather();
        }

        tmp.addChildren(child);
    }

}
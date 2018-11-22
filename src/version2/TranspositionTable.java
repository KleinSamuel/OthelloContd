package version2;

import java.util.HashMap;

public class TranspositionTable {

    /* First Key: Amount Chips on Board, Second Key: Board State; Value: Score */
    private HashMap<Integer, HashMap<Integer, Float>> table_black;
    private HashMap<Integer, HashMap<Integer, Float>> table_white;

    public TranspositionTable(){
        this.table_black = new HashMap<>();
        this.table_white = new HashMap<>();
    }

    public Float getScore(boolean black, int level, int hashCode){
        HashMap<Integer, HashMap<Integer, Float>> table = black ? table_black : table_white;
        if(table.containsKey(level)){
            if(table.get(level).containsKey(hashCode)){
                return table.get(level).get(hashCode);
            }
        }
        return null;
    }

    public void addScore(boolean black, int level, int hashCode, float score){
        HashMap<Integer, HashMap<Integer, Float>> table = black ? table_black : table_white;
        if(!table.containsKey(level)){
            table.put(level, new HashMap<>());
        }
        table.get(level).put(hashCode, score);
    }

}

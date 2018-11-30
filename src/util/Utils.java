package util;

public class Utils {

    public static String printLong(long l){
        String out = "";
        for(int i = 0; i < Long.numberOfLeadingZeros(l); i++) {
            out += "0";
        }
        if(l != 0L) {
            out += Long.toBinaryString(l);
        }
        return out;
    }

    public static Coordinate positionToCoordinate(int pos){
        return new Coordinate(7-(pos/8), 7-(pos%8));
    }

    public static int coordinateToPosition(int x, int y){
        return coordinateToPosition(new Coordinate(x, y));
    }

    public static int coordinateToPosition(Coordinate c){
        return 63-(c.x * 8) - c.y;
    }

    public static long positionToLong(int pos){
        return 0L ^ (1L << pos);
    }

    public static void printBoard(long black, long white){
        System.out.println("---------------------------------");
        for (int i = 63; i >= 0; i--) {
            if(((black >> i) & 1) == 1) {
                System.out.print("| X ");
            }else if(((white >> i) & 1) == 1){
                System.out.print("| O ");
            }else{
                System.out.print("|   ");
            }

            if((i%8) == 0){
                System.out.println("|\n---------------------------------");
            }
        }
    }

    public static void printBoardWithMoves(long black, long white, long l){
        System.out.println("### SINGLE LONG STATE ###");
        System.out.println("---------------------------------");
        for (int i = 63; i >= 0; i--) {
            if(((black >> i) & 1) == 1) {
                System.out.print("| X ");
            }else if(((white >> i) & 1) == 1) {
                System.out.print("| O ");
            }else if(((l >> i) & 1) == 1) {
                System.out.print("| + ");
            }else{
                System.out.print("|   ");
            }
            if((i%8) == 0){
                System.out.println("|\n---------------------------------");
            }
        }
    }

    public static void printMoves(long l){
        System.out.println("### SINGLE LONG STATE ###");
        System.out.println("---------------------------------");
        for (int i = 63; i >= 0; i--) {
            if(((l >> i) & 1) == 1) {
                System.out.print("| X ");
            }else{
                System.out.print("|   ");
            }
            if((i%8) == 0){
                System.out.println("|\n---------------------------------");
            }
        }
    }

}

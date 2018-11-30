package util;

public class Printer {

    public static boolean DEBUG = true;

    public static void printInfo(String s){
        if(DEBUG){
            System.out.println("[ INFO ]\t"+s);
        }
    }

}

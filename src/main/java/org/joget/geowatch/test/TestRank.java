package org.joget.geowatch.test;

public class TestRank {
    public static void main(String [] args){
        String rank = "2";
        if(rank != null && rank.trim().equals("1")){
            rank = "<b>"+rank+"</b>";
        }
    }
}

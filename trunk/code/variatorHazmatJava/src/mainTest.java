
import graph.*;
import java.util.*;
import population.*;

public class mainTest {
	
	public static void main(String[] args) {
		   System.out.println("Execution du programme");
		   System.out.println("1. Lecture du fichier de donnee");		   
		   Graph G = new Graph();
		   G.readInstance(args[0]);
		   Commodity c = G.returnListCommodities().get(0);
		   int indexO = c.getSource();
		   Node o = G.returnNode(indexO);
		   int indexD = c.getDest();
		   Node d = G.returnNode(indexD);
		   ArrayList<Integer> sPath= G.shortestPath(o,d);
	   }

}

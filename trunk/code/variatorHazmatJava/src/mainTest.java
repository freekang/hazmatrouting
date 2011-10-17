
import graph.*;
import java.util.*;
import population.*;

public class mainTest {
	
	public static void main(String[] args) {
		   System.out.println("Execution du programme");
		   System.out.println("1. Lecture du fichier de donnee");		   
		   Graph G = new Graph();
		   G.readInstance(args[0]);
		   System.out.println("Fin de lecture de l'instance");	
		   Commodity c = G.returnListCommodities().get(0);
		   int indexO = c.getSource();
		   Node o = G.returnNode(indexO);
		   int indexD = c.getDest();
		   Node d = G.returnNode(indexD);
		   System.out.println("Commodite 0: Origine = "+o.get_numero()+", Dest = "+d.get_numero());	
		   ArrayList<Node> sPath= G.shortestPath(o,d);
		   // Test	
		   //System.out.println("Je suis laaaaaaa11111111111");	
		   Arc arc = G.getArc(39, 38);		      
		   double risk = arc.getRisk(0);
		   System.out.println("r(39, 38) = " + risk);	
		   
		   G.graphViz(args[0]);
	   }

}

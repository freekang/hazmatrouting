import graph.*;
import population.*;

public class mainTest {
	
	public static void main(String[] args) {
		   System.out.println("Execution du programme");
		   System.out.println("1. Lecture du fichier de donnee");
		   
		   
		   Graph G = new Graph();
		   G.readInstance(args[0]);
	   }

}

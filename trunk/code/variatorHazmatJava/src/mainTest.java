import general.Variator;
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
		   
		   
		   
		   
		   Variator myvariator = new Variator();
		   PopulationHazmat mypophazmat = new PopulationHazmat();
		   myvariator.population = mypophazmat;
		   mypophazmat.initialize();
		   mypophazmat.debugPrint = true;
		   
		   // 1st individual
		   Vector<LinkedList<Node>> initialTruckPaths = new Vector<LinkedList<Node>>();
		   LinkedList<Node> firstTruckPath = new LinkedList<Node>();
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(262));
		   initialTruckPaths.add(firstTruckPath);
		   LinkedList<Node> secondTruckPath = new LinkedList<Node>();
		   secondTruckPath.add(PopulationHazmat.mygraph.returnNode(262));
		   initialTruckPaths.add(secondTruckPath);
		   LinkedList<Node> thirdTruckPath = new LinkedList<Node>();
		   thirdTruckPath.add(PopulationHazmat.mygraph.returnNode(262));
		   initialTruckPaths.add(thirdTruckPath);
		   LinkedList<Node> forthTruckPath = new LinkedList<Node>();
		   forthTruckPath.add(PopulationHazmat.mygraph.returnNode(262));
		   initialTruckPaths.add(forthTruckPath);
		   LinkedList<Node> fifthTruckPath = new LinkedList<Node>();
		   fifthTruckPath.add(PopulationHazmat.mygraph.returnNode(209));
		   initialTruckPaths.add(fifthTruckPath);
		   LinkedList<Node> sixthTruckPath = new LinkedList<Node>();
		   sixthTruckPath.add(PopulationHazmat.mygraph.returnNode(209));
		   initialTruckPaths.add(sixthTruckPath);
		   
		   Vector<Commodity> listOfCommodities = new Vector<Commodity>();
		   listOfCommodities.add(new Commodity(0, 262, 173, 923, 4));
		   listOfCommodities.add(new Commodity(0, 262, 173, 923, 4));
		   listOfCommodities.add(new Commodity(0, 262, 173, 923, 4));
		   listOfCommodities.add(new Commodity(0, 262, 173, 923, 4));
		   listOfCommodities.add(new Commodity(1, 209, 105, 107, 2));
		   listOfCommodities.add(new Commodity(1, 209, 105, 107, 2));
		   
		   IndividualHazmat ind = new IndividualHazmat(initialTruckPaths, listOfCommodities);
		   
		   ind.eval();
		   System.out.print(ind.objectiveSpace[0] + " " + ind.objectiveSpace[1] + " " + ind.objectiveSpace[2]);
		   System.out.println(" " + ind.getRepresentation());
		   
		   // 2nd individual:
		   Vector<LinkedList<Node>> initialTruckPaths2 = new Vector<LinkedList<Node>>();
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(261));
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(32));
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(267));
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(266));
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(48));
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(49));
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(65));
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(64));
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(63));
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(86));
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(126));
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(127));
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(244));
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(142));
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(143));
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(172));
		   firstTruckPath.add(PopulationHazmat.mygraph.returnNode(173)); 
		   initialTruckPaths2.add(firstTruckPath);
		   initialTruckPaths2.add(secondTruckPath);
		   initialTruckPaths2.add(thirdTruckPath);
		   initialTruckPaths2.add(forthTruckPath);
		   initialTruckPaths2.add(fifthTruckPath);
		   initialTruckPaths2.add(sixthTruckPath);
		   
		   IndividualHazmat ind2 = new IndividualHazmat(initialTruckPaths2, listOfCommodities);
		   
		   System.out.println();
		   ind2.eval();
		   System.out.print(ind2.objectiveSpace[0] + " " + ind2.objectiveSpace[1] + " " + ind2.objectiveSpace[2]);
		   System.out.println(" " + ind2.getRepresentation());
		   
		   System.out.println();
		   ArrayList<Node> shortestPath = PopulationHazmat.mygraph.shortestPath(PopulationHazmat.mygraph.returnNode(262),
				   PopulationHazmat.mygraph.returnNode(173));
		   for (Node n: shortestPath) {
			   System.out.print(n.get_numero() + " ");
		   }
		   System.out.println();
		   
		      
		   
		   // test for Data_generated.dat:
		   
		   //59 --> 119
		   G = new Graph();
		   G.readInstance("Data_generated.dat");	
		   c = G.returnListCommodities().get(0);
		   indexO = c.getSource();
		   o = G.returnNode(59);
		   indexD = c.getDest();
		   d = G.returnNode(indexD);
		   System.out.println("Commodite 0: Origine = "+o.get_numero()+", Dest = "+d.get_numero());	
		   sPath= G.shortestPath(o,d);
		   System.out.println(sPath);
		   
	   }

}

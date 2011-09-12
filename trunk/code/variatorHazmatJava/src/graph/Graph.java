package graph;

import general.Variator;


import java.util.*;
import java.io.*;
import java.lang.Integer;
import java.util.Scanner;
import java.util.regex.Pattern;
import population.*;
import java.io.*;




public class Graph {

	   // Variables ---------------------------------------
	    public int nbNodes; 
		public int nbArcs; 
		public int nbCom; 
		public int nbReg; 
		
		public ArrayList<Node> vectNodes; 
		public ArrayList<Arc> vectArcs;   
		public ArrayList<Commodity> listCom; 
	  	   
	   public Graph() {
		   this.vectNodes = new ArrayList<Node>();
		   this.vectArcs = new ArrayList<Arc>();
		   this.listCom = new ArrayList<Commodity>();
	   }	
	   
	   public void readInstance(String fileName) {
		   
    	   //lecture du fichier texte	
   			try{
   				FileReader f = new FileReader(fileName);
   				Scanner sc = new Scanner(f);
   				sc.useDelimiter(Pattern.compile("\t|\r\n|\n"));
   				
   				nbNodes = sc.nextInt();
   				System.out.println("n = " + nbNodes);  
   				
   				// Construct the nodes
   				for (int t = 0; t< nbNodes; t++) {
   					Node n = new Node(t);   
   					vectNodes.add(n);
   				}	
   				
   				nbCom = sc.nextInt();
   				System.out.println("c = " + nbCom);
   				nbReg = sc.nextInt();
   				System.out.println("r = " + nbReg);
   				
   				sc.next();
   				sc.next();
   				
   				// Read Arcs
   				int i = 0;
   				int j = 0;
   				int nbA = 0;   // The number of arcs 
   				while (sc.hasNextInt()){
   					//buffer = sc.next();
   					i = sc.nextInt();
   					Node ni = vectNodes.get(i);
   					j = sc.nextInt();
   					Node nj = vectNodes.get(j);
   					Double cost = Double.parseDouble(sc.next()); 
   					// Add the new arc
   					nbA++;
   					Arc arc = new Arc(ni, nj);
   					arc.addSuccNode(nj);
   					arc.addPredNode(ni);
   					arc.setCost(cost);
   					ni.add_out_arc(arc);
   					ni.add_in_arc(arc);
   					vectArcs.add(arc);   					
   				}
   				nbArcs = nbA;
   				System.out.println("number of arcs = "+nbA);
   				String s;
   				// Read: source	 dest	demand	tCap 
   				for (int k = 0; k < 7; k++){
   					s = sc.next();   					
   				}
   				
   				int nbCommodities = 0;
   				while (sc.hasNextInt()){
   				
   					nbCommodities++;
   					int nu =  sc.nextInt();   					   					
   					int source =  sc.nextInt();   					   					
   					int dest =  sc.nextInt();   					   					
   					int demand =  sc.nextInt();   					   					
   					int cap =  sc.nextInt();   		
   					int nbT = sc.nextInt();  
   					Commodity comm = new Commodity(nu, source, dest, demand, cap, nbT);
   					this.listCom.add(comm);   									
   				}
   				
   				this.nbCom = nbCommodities;
   				System.out.println("Number of commodities = " + nbCom);
   				
   				// Set the risk
   				// Aprcourir les commodites
   				while (!sc.hasNextInt())
   					sc.next();
   				
   				int numCom = 0;
   				while (sc.hasNextInt()) {	   				
	   				
   					while (sc.hasNextInt()) {
		   				// The source of the arc
		   				int so = sc.nextInt();   
		   				   			 
		   				// The destination of the arc
		   				int de = sc.nextInt(); 
		   				   				
		   				Arc theArc = getArc(so, de);
		   						   				
		   				int reg = sc.nextInt();		   				
		   				int risk = sc.nextInt();		   				
		   				theArc.setRisk(numCom, reg, risk);		   				
   					}
   					sc.next();
   					numCom++;   					
   				}   	   				
   				
	   			sc.close(); 
   			}		
	   		catch (Exception e){
	   			System.out.println(e.toString());
	   		}
       }

		 public int returnNbNodes() {
		   return nbNodes;
		 }

		 public int returnNbArcs(){
		   return nbArcs;
		 }

		 public int returnNbCom() {
		   return nbCom;
		 }

		 public int returnNbReg() {
		   return nbReg;
		 }

		 public Node returnNode(int n) {
		   return vectNodes.get(n);
		 }

		 // void Graph::affecterComOrigDest(int c, Sommet* o, Sommet* d) {
		 //   vector<Sommet*> l;
		 //   l.push_back(o);
		 //   l.push_back(d);  
		 //   list_Com_Orig_Dest.push_back(l);
		 // }

		 public ArrayList<Node> returnNodes(){
		   return vectNodes;
		 }

		 public ArrayList<Arc> returnArcs() {
		   return vectArcs;
		 }
		 
		 public Arc getArc(int o, int d) {		
			 Iterator it = this.vectArcs.iterator();
			 Arc arcc;
			 while (it.hasNext()){
				 arcc = (Arc) it.next();
				 if (arcc.returnOrigNode().get_numero() == o && arcc.returnDestNode().get_numero() == d)
					return arcc;				 
			 }
			 System.out.println("Error, arc not found: ("+o+", "+d+")");
			 arcc =  new Arc();
			 return arcc;
			 
		 }
		 
		 
		 public ArrayList<Node> shortestPath(Node o, Node d) {
			 
			 System.out.println("shortestPath from "+ o.get_numero() + " to "+d.get_numero());
			 
			 // The path: List of nodes
			 ArrayList<Integer> sPath = new ArrayList<Integer>();
			 ArrayList<Node> sPathSol = new ArrayList<Node>();
			 
			 // For each node (numero), we associate its weight
			 HashMap<Integer, Double> weight = new HashMap<Integer, Double>();
			 
			 // For each node (numero), we associate its predecessor node in the shortest path
			 HashMap<Integer, Integer> pred = new HashMap<Integer, Integer>();
			 
			 // List of definively treated nodes
			 ArrayList<Node> treatedNodes = new ArrayList<Node>(0);
			 			 
			 // List of temporary treated nodes
			 ArrayList<Node> visitedNodes = new ArrayList<Node>(0);
						 
			 // Initialize weight and pred
			for (int i = 0; i < this.nbNodes; i++) {
				 if(i == o.get_numero()) {
					 weight.put(i, 0.);
					 pred.put(i, -1);
					 treatedNodes.add(o);
				 }
				 else {
					 weight.put(i, 100000.);
					 visitedNodes.add(vectNodes.get(i));
				 }
			 }
			//System.out.println("Initialisation:");
			
			//System.out.println("Affichage des poids:");
			//Set<Integer> cles = weight.keySet();
			//Iterator<Integer> it = cles.iterator();
			//while (it.hasNext()){
				//Integer cle = (Integer) it.next(); 
			//Double valeur = weight.get(cle); 
			//System.out.println("weight("+cle+") = "+ valeur);
			//}
			
			//System.out.println("Affichage de treatedNodes:");
			//for (int i = 0; i < treatedNodes.size(); i++) {
				//		System.out.println("treatedNodes["+i+"] = "+ treatedNodes.get(i).get_numero());
					//}
			
			//System.out.println("Affichage de visitedNodes:");
			//for (int i = 0; i < visitedNodes.size(); i++) {
			//	System.out.println("visitedNodes["+i+"] = "+ visitedNodes.get(i).get_numero());
			//}
			
			 // Initialize Successors of o
			ArrayList<Arc> out = o.returnList_out_arcs();
			for (int i = 0; i<out.size(); i++) {
				 int numSucNode = out.get(i).returnDestNode().get_numero();
				 Double w = out.get(i).returnCost();
				 weight.put(numSucNode, w);
				 pred.put(numSucNode, o.get_numero());				 
			 }
			 
			 boolean stop = false;
			 while (visitedNodes.size() != 0 && !stop) {				 
				 
				 /** 1. find the minimum cost element in weight (visitedNodes) */
				 
				 Double min = 10000.;
				 int indexMin = -1;
				 
				 for (int i = 0; i<visitedNodes.size(); i++) {
					 int numNode = visitedNodes.get(i).get_numero();
					 Double val = (Double) weight.get(numNode); 
					 //System.out.println("val = "+val);
					 if (val < min) {
						 //System.out.println("passage22");
					   min = val;
					   indexMin = numNode;
					 }
				 }
				 		
				 // The treated node
				 Node currentNode = vectNodes.get(indexMin);
				 if (currentNode.get_numero() == d.get_numero())
					 stop = false; 
				 
				 
				 /**  2. Extend this node to its successor nodes in visitedNodes */
				 
				 ArrayList<Arc> arcs_out = currentNode.returnList_out_arcs();
				 
				 for (int i = 0; i<arcs_out.size() && !stop; i++) {	
					 int numSuccNode = arcs_out.get(i).returnDestNode().get_numero();					 
					 // if numSuccNode is in visitedNodes
					 Boolean visited = false;
					for (int j = 0; j<visitedNodes.size() && !visited; j++) {
						 if (visitedNodes.get(j).get_numero() == numSuccNode)
							 visited = true;
					 }
					if(visited) {						 
						 Double newCost = min + arcs_out.get(i).returnCost();						 
						 if (weight.get(numSuccNode) > newCost) {
							 weight.put(numSuccNode, newCost);
							 pred.put(numSuccNode, indexMin);	
						 }
					 }					
				 }				 
				 
				 /** 3. Add this node to the set of treated node: treatedNodes */
				 
				// System.out.println("coucou1: ");
				 treatedNodes.add(currentNode);
				 
				 /** 4. Remove this node from visitedNodes */
				 
				 for (int i = 0; i<visitedNodes.size(); i++) {
					 if (visitedNodes.get(i).get_numero() == indexMin) {
						 visitedNodes.remove(i);						
					 }
				 }				 
			 } 
			 
			 // Construct the optimal path;
			 int num = d.get_numero();	
			 while (num != -1) {
				 sPath.add(num);
				 num = pred.get(num);				 
			 }
			 
			 
			 // Inverse the array
			 for (int i = 0; i<sPath.size()/2; i++) {
				 int c;
				 int index = sPath.size() - i -1;
				 int elementIndex = sPath.get(index);
				 c = sPath.get(i);				 
				 sPath.set(i, elementIndex);
				 sPath.set(index, c);
			 }
			 System.out.println("The optimal path: ");
			 for (int i = 0; i<sPath.size(); i++) {
				 if(i == sPath.size()-1)
					 System.out.println(sPath.get(i)+"	");
				 else
					 System.out.print(sPath.get(i)+"	");
			 }
			 System.out.println("Cost = " + weight.get(d.get_numero()));
			 
			 for (int i = 0; i<sPath.size(); i++) {
				 int nn = sPath.get(i);
				 Node n = this.returnNode(nn);
				 sPathSol.add(n);
			 }
			 return sPathSol;			 
		 }
		 
		 
		 // Return the number of trucks associated to commodity c  
		 public int getNbTrucks(int c) {
			 return listCom.get(c).getNbTrucks();
		 }
		 
		 public ArrayList<Commodity> returnListCommodities() {
			 return listCom;
		 }
		 
		
		 
			 
}

package graph;

import general.Variator;


import java.util.*;
import java.io.*;
import java.lang.Integer;
import java.lang.Runtime;
import java.util.Scanner;
import java.util.regex.Pattern;
import population.*;
import java.io.*;
import java.lang.*;



public class Graph {

	   // Variables ---------------------------------------
	    public int nbNodes; 
		public int nbArcs; 
		public int nbCom; 
		//public int nbReg; 
		
		public ArrayList<Node> vectNodes; 
		public ArrayList<Arc> vectArcs;   
		public ArrayList<Commodity> listCom; 
		
		// List of hashtables for storing already computed shortest paths:
		// shortestPathStorage[s] contains all shortest paths from source s in a Hashtable
		// with the destination node d as key and the corresponding shortes s-d path as value
		private ArrayList<Hashtable<Node, ArrayList<Node>>> shortestPathStorage;
	  	   
	   public Graph() {
		   this.vectNodes = new ArrayList<Node>();
		   this.vectArcs = new ArrayList<Arc>();
		   this.listCom = new ArrayList<Commodity>();
		   this.shortestPathStorage = new ArrayList<Hashtable<Node, ArrayList<Node>>>();
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
   				//nbReg = sc.nextInt();
   				//System.out.println("r = " + nbReg);
   				
   				//sc.next();
   				sc.next();sc.next();sc.next();sc.next();
   				
   				// Read Arcs
   				int i = 0;
   				int j = 0;
   				int nbA = 0;   // The number of arcs 
   				while (sc.hasNextInt()){
   					//System.out.println("New arc \n");
   					//buffer = sc.next();
   					i = sc.nextInt();
   					//System.out.println("Noeud origine "+i);
   					Node ni = vectNodes.get(i);
   					//System.out.println("Noeud origine2222 "+ni.get_numero());
   					j = sc.nextInt();
   					//System.out.println("Noeud dest "+j);
   					Node nj = vectNodes.get(j);
   					//System.out.println("Noeud dest2222 "+nj.get_numero());
   					String ss = sc.next();
   					Double cost = Double.parseDouble(ss); 
   					//System.out.println("Cout "+cost);
   					// Add the new arc
   					Arc arc = new Arc(ni, nj, nbA);
   					arc.addSuccNode(nj);
   					arc.addPredNode(ni);
   					arc.setCost(cost);
   					ni.add_out_arc(arc);
   					ni.add_in_arc(arc);
   					
   					// add the risk to the arc
   					for (Integer k = 0; k < nbCom; k++){
   						Double risk = Double.parseDouble(sc.next()); 
   						Double t = k.doubleValue();
   						arc.setRisk(t, risk);	
   						//System.out.println("risk com"+k+" = "+ risk);
   					}
   					
   					vectArcs.add(arc);   
   					nbA++;
   					
   				}
   				nbArcs = nbA;
   				//System.out.println("number of arcs = "+nbA);
   				String s;
   				// Read: EndArcs source	 dest	demand 
   				for (int k = 0; k < 5; k++){
   					s = sc.next();   					
   				}
   				
   				for (int k = 0; k < nbCom; k++) {
   					
   					int source =  sc.nextInt();   					   					
   					int dest =  sc.nextInt();   					   					
   					int demand =  sc.nextInt(); 
   					int nbTrucks = sc.nextInt();
   					//System.out.println("Commodity "+k+": Source = "+source+", dest = "+ dest+" and demand = "+demand+" and NbTrucks = "+nbTrucks);
   					Commodity comm = new Commodity(k, source, dest, demand, nbTrucks);
   					this.listCom.add(comm); 
   				}   				
	   			sc.close(); 
	   			//System.out.println("FIN \n");
   			}		
	   		catch (Exception e){
	   			System.out.println(e.toString());
	   		}
	   		
	   		// don't forget to finish initialization of this.shortestPathStorage:
	   		for (int i=0; i< this.nbNodes; i++) {
	   			this.shortestPathStorage.add(new Hashtable<Node, ArrayList<Node>>());
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

		 //public int returnNbReg() {
		 //return nbReg;
		 //}

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
			 
			 /* compute shortest path only if not yet contained in this.shortestPathStorage */
			 if (this.shortestPathStorage.get(o.get_numero()).containsKey(d)) {
				 return this.shortestPathStorage.get(o.get_numero()).get(d);
			 }
			 else {
				 //System.out.println("shortestPath from "+ o.get_numero() + " to "+d.get_numero());
				 
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
					 // System.out.println("Je suis la shortest path5");	
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
					 //System.out.println("Je suis la shortest path4");	
					 // The treated node
					 Node currentNode = vectNodes.get(indexMin);
					 if (currentNode.get_numero() == d.get_numero())
						 stop = false; 
					 
					 
					 /**  2. Extend this node to its successor nodes in visitedNodes */
					 
					 ArrayList<Arc> arcs_out = currentNode.returnList_out_arcs();
					 // System.out.println("Je suis la shortest path3");	
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
					 //System.out.println("Je suis la shortest path2");	
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
				 //System.out.println("Je suis la shortest path1");	
				 // Construct the optimal path;
				 int num = d.get_numero();	
				 while (num != -1) {
					 //System.out.println("num = "+num);	
					 sPath.add(num);
					 num = pred.get(num);				 
				 }
				 
				 //System.out.println("Je suis la shortest path");	
				 // Inverse the array
				 for (int i = 0; i<sPath.size()/2; i++) {
					 int c;
					 int index = sPath.size() - i -1;
					 int elementIndex = sPath.get(index);
					 c = sPath.get(i);				 
					 sPath.set(i, elementIndex);
					 sPath.set(index, c);
				 }
				 //System.out.println("The optimal path: ");
				 //for (int i = 0; i<sPath.size(); i++) {
				 //if(i == sPath.size()-1)
				 //	 System.out.println(sPath.get(i)+"	");
				 // else
				 //	 System.out.print(sPath.get(i)+"	");
				 // }
				 //System.out.println("Cost = " + weight.get(d.get_numero()));
				 
				 for (int i = 0; i<sPath.size(); i++) {
					 int nn = sPath.get(i);
					 Node n = this.returnNode(nn);
					 sPathSol.add(n);
				 }
				 
				 /* Before to return newly computed shortest path, add it to
				  * this.shortestPathStorage: */
				 this.shortestPathStorage.get(o.get_numero()).put(d, sPathSol);
				 
				 System.out.println("new shortest path added");
				 
				 return sPathSol;
			 }
		 }
		 
		 
		 // Return the number of trucks associated to commodity c  
		 //public int getNbTrucks(int c) {
		 //return listCom.get(c).getNbTrucks();
		 // }
		 
		 public ArrayList<Commodity> returnListCommodities() {
			 return listCom;
		 }
		 
		
		 public void graphViz(String fileName) {
			 
			 PrintWriter pg = null;			
			 String nom_fich;			 

			nom_fich = fileName.concat(".dot");
		    		    
		    System.out.println("nom_fich = " + nom_fich);
		    		    
			//pg = fopen(nom_fich,"w");
			try
		    {
				pg = new PrintWriter(new FileWriter(nom_fich));
			}
		    catch (ArrayIndexOutOfBoundsException e) {
		      System.err.println("Caught ArrayIndexOutOfBoundsException: "
		          + e.getMessage());		  
		    }catch (IOException e) {
		        System.err.println("Caught IOException: " + e.getMessage());
		    }
			//fprintf(pg,"digraph G {\n");
		    pg.print("digraph G {\n");
			pg.flush();

			// decrir les sommets
			for (int i = 0; i < this.vectNodes.size(); i++) {
				pg.print(i+"[label = "+i+"];\n");				
			}
			
			// decrir les arcs et les couts
			
			for (int i = 0; i < this.vectArcs.size(); i++) {
				int o = vectArcs.get(i).returnOrigNode().get_numero();
				int d = vectArcs.get(i).returnDestNode().get_numero();
				double cos = vectArcs.get(i).returnCost();
				pg.print(o+" -> "+d+"[label = "+ cos +"];\n");	
			}
			pg.print("}");	
			pg.close();
			
			try { 
				String cmd = "dot -Tpng simpleTest.dat.dot > sortie.png"; 
				Runtime r = Runtime.getRuntime();
				r.exec(cmd,null,null);
				//p.waitFor();
			} catch (IOException t) { 
				System.out.println("erreur d'execution " +  t.toString()); 
			} 
			
		 }

		 
			 
}

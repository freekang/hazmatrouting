import java.util.*;
import java.io.*;
import java.lang.Integer;
import java.util.Scanner;
import java.util.regex.Pattern;


public class Graph {

	   // Variables ---------------------------------------
		private int nbNodes; 
		private int nbArcs; 
		private int nbCom; 
		private int nbReg; 

		private ArrayList<Node> vectNodes; 
		private ArrayList<Arc> vectArcs;   
	    private ArrayList<Commodity> listCom; 
	  	   
	   public Graph() {
		   this.vectNodes = new ArrayList<Node>();
		   this.vectArcs = new ArrayList<Arc>();
		   this.listCom = new ArrayList<Commodity>();
	   }	
	   
	   public void lectureInstance(String fileName) {
		   
    	   //lecture du fichier texte	
   			try{
   				FileReader f = new FileReader(fileName);
   				Scanner sc = new Scanner(f);
   				sc.useDelimiter(Pattern.compile("[\t\n]"));
   				
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
   					// Add the new arc
   					nbA++;
   					Arc arc = new Arc(ni, nj);
   					arc.addSuccNode(nj);
   					arc.addPredNode(ni);
   					vectArcs.add(arc);   					
   				}
   				nbArcs = nbA;
   				System.out.println("Nombre d'arcs = "+nbA);
   				String s;
   				// Read: source	 dest	demand	tCap 
   				for (int k = 0; k < 6; k++){
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
   					Commodity comm = new Commodity(nu, source, dest, demand, cap);
   					this.listCom.add(comm);   									
   				}
   				
   				this.nbCom = nbCommodities;
   				System.out.println("Nb Commodities = "+nbCom);
   				
   				
   				
   				
   				

   				
   				
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

		 public void returnNodeOrigineDestCom(int c) {
			 int a = 1+c;
			 System.out.println("returnNodeOrigineDestCom");
		 }

		 public void returnArc() {
			 System.out.println("returnNodeOrigineDestCom");
		 }

		 public ArrayList<Node> returnNodes(){
		   return vectNodes;
		 }

		 public void displayGraph() {
		   
		   System.out.println("Nodes:");
		   
		 }

		 public ArrayList<Arc> returnArcs() {
		   return vectArcs;
		 }
		 
			 
}

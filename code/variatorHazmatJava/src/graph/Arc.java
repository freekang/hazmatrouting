package graph;

import java.util.ArrayList;
import java.util.Vector;
import java.io.*;

public class Arc {
	
	private int num; 				// id of the arc
	private double cost; 			// cost of the arc
	private double rCost; 			// reduced cost of the arc
	private double totRisk; 		// The total risk on the arc (for all the regions)
	private Node origNode; 			// origin node of the arc
	private Node destNode; 			// destination node of the arc
	private Vector comRegRisk;		// com --> Reg--> risk
	private ArrayList<Node> succNodes;		// successor nodes of the arc
	private ArrayList<Node> predNodes;		// predecessor nodes of the arc
	
	public Arc(){
		this.succNodes = new ArrayList<Node>();
		this.predNodes = new ArrayList<Node>();
	}

	/**
	 * Constructor: sets origin (=v1) and destination (=v2) node
	 * @param v1
	 * @param v2
	 */
	public Arc(Node v1, Node v2){
	  this.origNode = v1;
	  this.destNode = v2;
	  this.succNodes = new ArrayList<Node>();
	  this.predNodes = new ArrayList<Node>();
	}
	
	public Node getOrigNode(){
	  return origNode;
	}

	public Node getDestNode(){
	  return destNode;
	}
	
	public void addSuccNode(Node s) {
		succNodes.add(s);
	}
	
	public void addPredNode(Node s) {
		predNodes.add(s);
	}

	/**
	 * Returns the risk associated to a given commodity and region when this arc is used.
	 * @param commodity
	 * @param region
	 * @return the risk associated to the specified commodity and region when this arc is used
	 */

	/*
	public int returnRisk(int c, int q) {
	 
	  list<list<vector<int> > >::iterator it = com_reg_risque.begin();
	  
	  int i = 0;
	  while (i != c && it != com_reg_risque.end()) {
	    i++;
	    it++;
	  }
	  if (it != com_reg_risque.end()) {
	    list<vector<int> >::iterator itt = (*it).begin();
	    while (itt != (*it).end()) {
	      if ((*itt)[0] == q) {
		return (*itt)[1];
	      }
	      itt++;
	    }
	  }
	//   else {
//	     cout << endl;
	//   } 
//	    cout<<"Erreur: la commodite "<<c<<" sur l'arc "<<num<< " n'impose pas de risque sur la region "<<q<<endl;
	  // si la commodite sur l'arc n'impose pas de risuqe sur la region q, on renvoie 0
	  return 0;
	}*/

	/**
	 * Sets the risk associated to this arc and the given commodity and region.
	 * @param commodity
	 * @param region
	 * @param risk
	 */
	/*
	void Arc::affecterRisque(int c, int r, int risque) {
	  
	  list<list<vector<int> > >::iterator it = com_reg_risque.begin();  
	  int i = 0;
	  while (i != c && it != com_reg_risque.end()) {
	    i++;
	    it++;
	  }
	  vector<int> v(2);
	  v[0] = r;
	  v[1] = risque;
	  // si la commodite existe, affecter le risque d'une nouvelle resgion
	  if (it != com_reg_risque.end()) {
	    (*it).push_back(v);
	  }
	  else {
	    // cette commodite n'as pas encore ete inseree
	    // la rajouter
	    list<vector<int> > nouvListe;
	    nouvListe.push_back(v);
	    com_reg_risque.push_back(nouvListe);
	  }
	}*/

	public int getNum() {
	  return num;
	}

	public double getReducedCost() {
	  return rCost;
	}

	public void setReducedCost(double cr) {
		this.rCost = cr;
	}

	public double getCost() {
	  return cost;
	}

	public void setCost(double cou) {
	  this.cost = cou;
	}

	/*int Arc::retournerRisqueTot(int c) {
	  
	  list<list<vector<int> > >::iterator it = com_reg_risque.begin();
	  int i = 0;
	  while (i != c && it != com_reg_risque.end()) {
	    i++;
	    it++;
	  }
	  int risque = 0;
	  if (it != com_reg_risque.end()) {
	    // faire la somme des risques de toutes les regions
	    list<vector<int> >::iterator itt = (*it).begin();
	    while (itt != (*it).end()) {
	      risque += (*itt)[1];
	      itt++;
	    }
	  }	    
	  return risque;
	}*/


}

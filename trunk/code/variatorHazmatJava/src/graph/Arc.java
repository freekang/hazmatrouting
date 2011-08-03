import java.util.*;
import java.util.Map;
import java.util.HashMap;
import java.io.*;


public class Arc {
	
	private Integer num; 				// numero of the arc
	private double cost; 			// cost of the arc
	private double rCost; 			// reduced cost of the arc
	private double totRisk; 		// The total risk on the arc (for all the regions)
	private Node origNode; 			// origin node of the arc
	private Node destNode; 			// destination node of the arc
	private Vector comRegRisk; 				// com --> Reg--> risk
	private ArrayList<Node> succNodes;		// Successor Nodes of the arc
	private ArrayList<Node> predNodes;		// predecessor Nodes of the arc
	private ArrayList<ArrayList<Integer>>  mapRisk; // commodity - region - risk 
	
	public Arc(){
		this.succNodes = new ArrayList<Node>();
		this.predNodes = new ArrayList<Node>();
		this.mapRisk = new ArrayList<ArrayList<Integer>>();
	}

	public Arc(Node v1, Node v2){
	  this.origNode = v1;
	  this.destNode = v2;
	  this.succNodes = new ArrayList<Node>();
	  this.predNodes = new ArrayList<Node>();
	  this.mapRisk = new ArrayList<ArrayList<Integer>>();
	}
	
	public Node returnOrigNode(){
	  return origNode;
	}

	public Node returnDestNode(){
	  return destNode;
	}
	
	public void addSuccNode(Node s) {
		succNodes.add(s);
	}
	
	public void addPredNode(Node s) {
		predNodes.add(s);
	}
	
	public int returnNum() {
	  return num;
	}

	public double returnReducedCost() {
	  return rCost;
	}

	public void setReducedCost(double cr) {
		this.rCost = cr;
	}

	public double returnCost() {
	  return cost;
	}

	public void setCost(double cou) {
	  this.cost = cou;
	}
	
	public void setRisk(int c, int reg, int risk) {
		ArrayList<Integer> v = new ArrayList<Integer>(2);
		v.add(0, reg);
		v.add(1, risk);
		this.mapRisk.add(v);		
	}
}

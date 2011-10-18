package graph;

import java.util.*;
import java.util.Map;
import java.util.HashMap;
import java.io.*;


public class Arc {
	
	private Integer num; 				// numero of the arc
	private double cost; 			// cost of the arc
	//private double rCost; 			// reduced cost of the arc
	//private double totRisk; 		// The total risk on the arc (for all the regions)
	private Node origNode; 			// origin node of the arc
	private Node destNode; 			// destination node of the arc
	//private Vector comRegRisk; 				// com --> Reg--> risk
	
	// TODO: why do we need a list of successor and predecessor nodes for each arc here? (Dimo)
	private ArrayList<Node> succNodes;		// Successor Nodes of the arc
	private ArrayList<Node> predNodes;		// predecessor Nodes of the arc
	//private ArrayList<ArrayList<Integer>>  mapRisk; // commodity - region - risk 
	private ArrayList<ArrayList<Double>>  comRisk; // commodity - risk 
	
	public Arc(){
		this.succNodes = new ArrayList<Node>();
		this.predNodes = new ArrayList<Node>();
		this.comRisk = new ArrayList<ArrayList<Double>>();
	}

	public Arc(Node v1, Node v2, int n){
	  this.origNode = v1;
	  this.destNode = v2;
	  this.num = n;
	  this.succNodes = new ArrayList<Node>();
	  this.predNodes = new ArrayList<Node>();
	  this.comRisk = new ArrayList<ArrayList<Double>>();
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
	
	public void setNum(int n) {
		num = n;
	}

	//public double returnReducedCost() {
	// return rCost;
	//}

	//public void setReducedCost(double cr) {
	//this.rCost = cr;
	//}

	public double returnCost() {
	  return cost;
	}

	public void setCost(double cou) {
	  this.cost = cou;
	}
	
	//public void setRisk(int c, int reg, int risk) {
	//ArrayList<Integer> v = new ArrayList<Integer>(3);
	//v.add(0, c);
		//v.add(1, reg);
	//v.add(2, risk);
	//this.mapRisk.add(v);		
	//}
	
	public void setRisk(Double c, Double risk) {
		ArrayList<Double> v = new ArrayList<Double>(2);
		v.add(0, c);
		//v.add(1, reg);
		v.add(1, risk);
		this.comRisk.add(v);		
	}
	
	public Double getRisk(int c) {
		for (Integer i = 0; i < this.comRisk.size(); i++) {
			if (this.comRisk.get(i).get(0) == c ) {
				return this.comRisk.get(i).get(1);
			}
		}	
		return -1.0;
	}
	
	//public ArrayList<ArrayList<Integer>> getMapRisk() {
	//return this.mapRisk;
	//}
	
	/* returns risk on region reg when this arc is used to transport commodity c */
	//public double getRisk(int c, int reg) {
		// parcourir mapRisk
	//for (int i = 0; i < this.mapRisk.size(); i++) {
	//if (this.mapRisk.get(i).get(0) == c && this.mapRisk.get(i).get(1) == reg) {
	//	return this.mapRisk.get(i).get(2);
	//}
	//}		
	//System.out.println("Error, risk not found");	
	//return -1;
	//}
}

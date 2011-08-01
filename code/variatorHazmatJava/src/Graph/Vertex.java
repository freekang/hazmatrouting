package Graph;

import java.util.ArrayList;

public class Vertex {

	private int num;
	private ArrayList<Arc> listOfIncomingArcs;
	private ArrayList<Arc> listOfOutgoingArcs;
	private ArrayList<Vertex> listOfNeighbors;
		   
	Vertex(int id) {
		this.num = id;
	}

	public int getNum() {
		return this.num;
	}
	
	public void addOutgoingArc(Arc a) {
		this.listOfOutgoingArcs.add(a);
	}
	
	public void addIncomingArc(Arc a) {
		this.listOfIncomingArcs.add(a);
	}
	
	public ArrayList<Arc> getListOfIncomingArcs() {
		return this.listOfIncomingArcs;
	}
	
	public int getNumberOfNeighbors() {
		return this.listOfNeighbors.size();
	}
	
}

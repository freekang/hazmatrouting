package graph;

import java.util.ArrayList;
import java.util.Vector;

public class Arc {

	private int num;
	private double weight;
	private double reducedWeight; // don't know what this is !?
	private double totalRisk; // risk asssociated with this arc summed over all regions
	private Vertex headVertex; // first vertex of arc (origin)
	private Vertex tailVertex; // second vertex of arc (destination)
	/**
	 * TODO: translate:
	 * chaque ligne represente une commodite
	 * chaque element de la liste (vecteur) contient la regio et le risque.
	 */
	private ArrayList<ArrayList<Vector<Integer>>> com_reg_risque; 
		  
	/**
	 * Constructor: sets head (=v1) and tail (=v2) vertex as well as id (=n)
	 * @param v1
	 * @param v2
	 * @param n
	 */
	public Arc(Vertex v1, Vertex v2, int n) {
		this.headVertex = v1;
		this.tailVertex = v2;
		this.num = n;
	}
	
	public Vertex getHeadVertex() {
		return this.headVertex;
	}
	
	public Vertex getTailVertex() {
		return this.tailVertex;
	}
	
	public double getWeight() {
		return this.weight;
	}
	
	public void setWeight(double w) {
		this.weight = w;
	}
	
	/**
	 * Returns the risk associated to a given commodity and region when this arc is used.
	 * @param commodity
	 * @param region
	 * @return the risk associated to the specified commodity and region when this arc is used
	 */
	public int getRisk(int commodity, int region) {
		// TODO: implement/translate
		return 0;
	}

	/**
	 * Sets the risk associated to this arc and the given commodity and region.
	 * @param commodity
	 * @param region
	 * @param risk
	 */
	public void setRisk(int commodity, int region, int risk) {
		// TODO: implement/translate
	}
	
	public int getNum() {
		return this.num;
	}
	
	public double getReducedWeight() {
		return this.reducedWeight;
	}
	
	public void setReducedWeight(double weight) {
		this.reducedWeight = weight;
	}
	
	public int getTotalRisk(int commodity) {
		// TODO: implement/translate
		return 0;
	}

	
}

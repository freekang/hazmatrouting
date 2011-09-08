package graph;

public class Commodity {
	
	private int num; 				// numero of the commodity
	private int source;
	private int dest;
	private int demand;
	private int tCap;
	private int nbTrucks;
	
	public Commodity(int n, int s, int des, int dem, int t, int nb){
		this.num = n;
		this.source = s;
		this.dest = des;
		this.demand = dem;
		this.tCap = t;
		this.nbTrucks = nb;
	}
	
	public int getNbTrucks() {
		return this.nbTrucks;
	}
	
	public int getSource() {
		return this.source;
	}
	
	public int getDest() {
		return this.dest;
	}

	/** 
	 * Returns a (deep) copy of itself.
	 * @return deep copy
	 */
	public Commodity getCopy() {
		return new Commodity(this.num, this.source, this.dest, this.demand, this.tCap, this.nbTrucks);
	}
}

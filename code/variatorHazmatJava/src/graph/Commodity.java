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
		this.dest = dem;
		this.demand = n;
		this.tCap = t;
		this.nbTrucks = nb;
	}
	
	public int getNbTrucks() {
		return this.nbTrucks;
	}
	
	
}


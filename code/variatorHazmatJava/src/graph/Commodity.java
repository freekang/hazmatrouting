package graph;

public class Commodity {
	
	private int num; 				// id of the commodity
	private int source;
	private int dest;
	private int demand;
	private int tCap;
	
	public Commodity(int n, int s, int des, int dem, int t){
		this.num = n;
		this.source = s;
		this.dest = dem;
		this.demand = n;
		this.tCap = t;
	}
}


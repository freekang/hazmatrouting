import java.util.ArrayList;


public class Node
{
    private int num;					// numero of the node in the graph
    private ArrayList<Arc> list_arcs_out;	// list of the outgoing arcs from node num 
    private ArrayList<Arc> list_arcs_in;    // list of the incoming arcs from node num 
    
    /**
       Constructor 
    */
    public Node(int n)
    {
    	this.num = n ;
    	this.list_arcs_out = new ArrayList<Arc>();
    	this.list_arcs_in = new ArrayList<Arc>();
    }
    
    /** get the numero */
    public int get_numero()
    {
    	return num ;
    }
    
    public void add_out_arc(Arc a) {
    	this.list_arcs_out.add(a);
    }

    public void add_in_arc(Arc a){
    	this.list_arcs_in.add(a);
    }

    public ArrayList<Arc> returnList_out_arcs() {
    	return list_arcs_out;
    }

    public int return_nb_out_arcs() {
    	return list_arcs_out.size();
    }    
}


package graph;

import java.util.ArrayList;


public class Node
{
    private int num;					// id of the node in the graph
    private ArrayList<Arc> list_arcs_out;		// list of the outgoing arcs from node num 
    private ArrayList<Arc> list_arcs_in;		// list of the incoming arcs from node num 
    
    /**
       Constructor setting the id to 'n'
    */
    public Node(int n)
    {
    	this.num = n ;
    	this.list_arcs_out = new ArrayList<Arc>();
    	this.list_arcs_in = new ArrayList<Arc>();
    }
    
    /** get the id */
    public int getNum()
    {
    	return num ;
    }
    
    public void add_out_arc(Arc a) {
    	this.list_arcs_out.add(a);
    }

    public void add_in_arc(Arc a){
    	this.list_arcs_in.add(a);
    }

    public ArrayList<Arc> getList_out_arcs() {
    	return list_arcs_out;
    }

    public int get_nb_out_arcs() {
    	return list_arcs_out.size();
    }    
}


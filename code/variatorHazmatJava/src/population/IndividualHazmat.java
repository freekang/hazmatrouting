/*
 Copyright (c) 2008 Computer Engineering and Communication Networks Lab (TIK)
 Swiss Federal Institute of Technology (ETH) Zurich, Switzerland
 All rights reserved.
 Permission is hereby granted, without written agreement and without
 license or royalty fees, to use, copy, modify, and distribute this
 software and its documentation for any purpose, provided that the above
 copyright notice and the following two paragraphs appear in all copies
 of this software.
 IN NO EVENT SHALL THE TIK OR THE ETH ZURICH BE LIABLE TO ANY PARTY
 FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
 ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
 THE TIK OR THE ETH ZURICH HAVE BEEN ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.
 THE TIK AND THE ETH ZURICH SPECIFICALLY DISCLAIM ANY WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
 PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND TIK AND THE ETH ZURICH
 HAVE NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 ENHANCEMENTS, OR MODIFICATIONS.
 Title: Variator
 Description: Standard variator, can be adapted to generate user-defined variators
 Copyright: Copyright (c) 2008
 Company: ETH Zurich
 */

package population;

import general.*;
import graph.*;
import java.util.Vector;
import java.util.LinkedList;
import java.util.ArrayList;


/** Specifies the problem-specific behavior of an individual.
 * Defines the decision space representation of the individuals,
 * provides two constructors
 * and implements methods to calculate the objective space values of an individual
 * and to create a copy of an individual.
 * 
 * @author Tamara Ulrich
 * @version 1.0
 */
public class IndividualHazmat extends IndividualAbstract {
	
	Vector<LinkedList<Node>> truckPaths; // for each truck a list of nodes gives the path they travel
	Vector<Commodity> associatedCommodities; // list of associated commodities of each truck
	boolean alreadyEvaluated;
	
	/** Standard class constructor, initializes the decision space representation randomly
	 * based on the graph instance in PopulationHazmat.mygraph.
	 * 
	 * In a first implementation, a fixed number of trucks per commodity is send through
	 * the network.
	 * 
	 */
	public IndividualHazmat(){
		this.truckPaths = new Vector<LinkedList<Node>>();
		this.associatedCommodities = new Vector<Commodity>();
		this.alreadyEvaluated = false;
		
		for (Commodity commodity: PopulationHazmat.mygraph.listCom) {
			/* create "empty" path, consisting of only the source node for this commodity */
			LinkedList<Node> emptyPath = new LinkedList<Node>();
			emptyPath.add(PopulationHazmat.mygraph.returnNode(commodity.getSource()));

			for (int i=0; i < commodity.getNbTrucks(); i++) {
				this.truckPaths.add(emptyPath);
				this.associatedCommodities.add(commodity);
			}
		}
		
		this.objectiveSpace = new double[3];
		this.eval();
	}
	
	/** Class constructor which initializes the decision and objective space representations with the given values.
	 * 
	 * @param initialTruckPaths A vector of node lists which specifies the paths through the graph
	 *                          for each truck
	 * @param listOfCommodities A vector of commodities, giving the type of commodity associtated with
	 *                          the truck paths
	 */
	public IndividualHazmat(Vector<LinkedList<Node>> initialTruckPaths, Vector<Commodity> listOfCommodities) {
		this.truckPaths = initialTruckPaths;
		this.associatedCommodities = listOfCommodities;
		this.alreadyEvaluated = false;
		
		this.objectiveSpace = new double[3];
		this.eval();
	}
	
	/** Class constructor which initializes the decision and objective space representations with the given values.
	 *  The individual is *not* again evaluated in this case (e.g. when stemming from invoking
	 *  the copy() method. 
	 * 
	 * @param initialTruckPaths            A vector of node lists which specifies the paths through
	 *                                     the graph for each truck
	 * @param listOfCommodities            A vector of commodities, giving the type of commodity
	 *                                     associtated with the truck paths
	 * @param correctlyEvaluatedObjVector  The objective vector, the new individual inherits from another,
	 *                                     correctly evaluated individual with the same nodes and commodities
	 */
	public IndividualHazmat(Vector<LinkedList<Node>> initialTruckPaths, Vector<Commodity> listOfCommodities, double[] correctlyEvaluatedObjVector) {
		this.truckPaths = initialTruckPaths;
		this.associatedCommodities = listOfCommodities;
		this.objectiveSpace = new double[3];
		
		/* no evaluation necessary here: */
		this.objectiveSpace[0] = correctlyEvaluatedObjVector[0];
		this.objectiveSpace[1] = correctlyEvaluatedObjVector[1];
		this.objectiveSpace[2] = correctlyEvaluatedObjVector[2];
		this.alreadyEvaluated = true;
		
	}
	
	/** Calculates the objective space values of this individual. The two objectives are number of
	 * leading ones and number of trailing zeros. Both objectives are converted such that they have to
	 * be minimized (by subtracting them from the total number of decision variables). */
	public void eval() {
		
		if (!alreadyEvaluated) {
			
			/* First, store 'completed' paths, not only the evolved parts */
			Vector<LinkedList<Node>> completedTruckPaths = new Vector<LinkedList<Node>>();
			int i=0; // index to get commodity of each truck
			
			for (LinkedList<Node> list : this.truckPaths ) {
				/* copy beginning of each path from individual */
				LinkedList<Node> pathToComplete = new LinkedList<Node>();
				for (Node node : list) {
					pathToComplete.add(node);
				}
				/* compute remaining part as shortest path from last node to destination */
				Node o = pathToComplete.getLast();
				Node d = PopulationHazmat.mygraph.returnNode(this.associatedCommodities.get(i).getDest());
				if (o.get_numero() == d.get_numero()) {
					continue; // no need to finish this path since already correct
				}
				ArrayList<Node> sp = PopulationHazmat.mygraph.shortestPath(o, d);
				/* go through this path and add every node to completedTruckPaths */
				for (Node node : sp) {
					if (node.get_numero() != pathToComplete.getLast().get_numero()) {
						pathToComplete.add(node);
					}
				}
				
				completedTruckPaths.add(pathToComplete);
				i++;
			}
			
			
			/* Second, go through all paths and compute the objective functions */
			i = 0; // as above, an index to get the corresponding commodity
			this.objectiveSpace[0] = 0;
			this.objectiveSpace[1] = 0;
			this.objectiveSpace[2] = Double.NEGATIVE_INFINITY; // 2nd obj. is a maximum of risks
			// store preliminary sums of the r^{cq}_{ij} y^c_{ij} for each region:
			ArrayList<Double> risksumsPerRegion = new ArrayList<Double>(
					                                      PopulationHazmat.mygraph.nbReg);
			for (int r=0; r < risksumsPerRegion.size(); r++) {
				risksumsPerRegion.add(r, 0.0);
			}
			
			for (LinkedList<Node> path : completedTruckPaths) {
				Node a, b = path.poll();
				
				while (!path.isEmpty()) {
					a = b;
					b = path.poll(); 
					Arc currArc = PopulationHazmat.mygraph.getArc(a.get_numero(), b.get_numero());
					// update 1st objective for first arc
					this.objectiveSpace[0] += currArc.returnCost();
					// for update of 2nd and 3rd objective, update the risk per region
					for (int r=0; r < risksumsPerRegion.size(); r++) {
						risksumsPerRegion.set(r, risksumsPerRegion.get(r)
								+ currArc.getRisk(this.associatedCommodities.get(i).getNum(), r));
					}
				}
				i++;
			} // now, 1st objective already computed, 2nd, and 3rd still missing
			
			// go through risksumsPerRegion and compute remaining objectives as sum and max resp.
			for (int r=0; r < risksumsPerRegion.size(); r++) {
				double currRisk = risksumsPerRegion.get(r);
				this.objectiveSpace[1] += currRisk; 
				if (currRisk > this.objectiveSpace[2]) {
					this.objectiveSpace[2] = currRisk;
				}
			}
			
			this.alreadyEvaluated = true;
		}
		
	}

	/** Returns a (deep) copy of this individual.
	 * 
	 * @return copy of the this individual
	 */
	public IndividualHazmat copy() {
		Vector<LinkedList<Node>> copyOfTruckPaths = new Vector<LinkedList<Node>>();
		Vector<Commodity> copyOfAssociatedCommodities = new Vector<Commodity>();
		
		for (LinkedList<Node> path: this.truckPaths) {
			LinkedList<Node> copiedPath = new LinkedList<Node>();

			for (Node node: path) {
				copiedPath.add(node);
			}
		}
		
		for (Commodity comm: this.associatedCommodities) {
			copyOfAssociatedCommodities.add(comm);
		}
		
		IndividualHazmat newInd = new IndividualHazmat(copyOfTruckPaths, copyOfAssociatedCommodities, this.objectiveSpace);
		return newInd;
	}
	
	/**
	 * Mutates the current individual.
	 */
	void mutate() {
		/* For each truck path, draw a binary value b uniformly at random and
		* either add or remove a node depending on b
		* 
		* For the moment, the implementation follows exactly the abstract at CTW'2011
		* where *every* truck path is mutated.
		*/
		for (LinkedList<Node> path: this.truckPaths) {
			boolean b = Variator.randomGenerator.nextBoolean();
			
			if (b == true) {
				/* make path shorter if possible */
				if (path.size() >= 2) {
					path.pollLast();
				}
			} else {
				/* make path longer if possible
				 * to this end, choose new node uniformly at random from neighbors
				 * if there are some */
				ArrayList<Arc> arcList = path.getLast().returnList_out_arcs();
				if (arcList.size() > 0) {
					int r = Variator.randomGenerator.nextInt(arcList.size());
					/* now add the corresponding node to the path */
					path.add(arcList.get(r).returnDestNode());
				}
			}
			
		}
		/* since the representation of the individual changed, ensure a new
		 * evaluation next time: */
		this.alreadyEvaluated = false;
	}
	
}
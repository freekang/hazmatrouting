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
	
	/** Standard class constructor
	 * 
	 * Note that no initialization of the truck paths is taking place here!
	 * 
	 * Before using the individual further, the initialization should be performed via the
	 * methods 'initCost' and 'initRandom'.
	 * 
	 */
	public IndividualHazmat(){
		this.truckPaths = new Vector<LinkedList<Node>>();
		this.associatedCommodities = new Vector<Commodity>();
		this.alreadyEvaluated = false;
		
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
		
		
		//System.out.println("---");
		//System.out.println(this.truckPaths);
		//System.out.println(this.associatedCommodities);
		//System.out.println(this.objectiveSpace);
		
		
	}

	/** Initializes the individual with all truck paths containing only the source node of
	 * the corresponding commodity. */
	public void initCost() {
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
	
	/** Initializes the individual by randomly choosing a node for each truck path and
	 * setting the truck path as the shortes path between the corresponding source node and the
	 * chosen random node. */
	public void initRandom() {
		for (Commodity commodity: PopulationHazmat.mygraph.listCom) {
			/* store source node of this commodity */
			Node source = PopulationHazmat.mygraph.returnNode(commodity.getSource());
			
			for (int i=0; i < commodity.getNbTrucks(); i++) {
				/* start with an empty path for this truck */
				LinkedList<Node> randomPath = new LinkedList<Node>();
				
				/* select a random node id which is not the source */
				int randomNodeID = Variator.randomGenerator.nextInt(PopulationHazmat.mygraph.nbNodes);
				while (randomNodeID == source.get_numero()) {
					randomNodeID = Variator.randomGenerator.nextInt(PopulationHazmat.mygraph.nbNodes);
				}
				
				/* compute shortest path between source and randomNode */
				Node randomNode = PopulationHazmat.mygraph.returnNode(randomNodeID);
				ArrayList<Node> shortestPath = PopulationHazmat.mygraph.shortestPath(source, randomNode);
				
				/* add the resulting shortest path to emptyPath */
				for (Node n: shortestPath) {
					randomPath.add(n);
				}
				
				/* Finally, add the new path to this.truckPaths and update associatedCommodities
				 * as well */
				this.truckPaths.add(randomPath);
				this.associatedCommodities.add(commodity);
			}
		}
		
		this.objectiveSpace = new double[3];
		this.eval();
	}
	
	/** Calculates the objective space values of this individual. The two objectives are number of
	 * leading ones and number of trailing zeros. Both objectives are converted such that they have to
	 * be minimized (by subtracting them from the total number of decision variables). */
	/* (non-Javadoc)
	 * @see population.IndividualAbstract#eval()
	 */
	public void eval() {
		
		//alreadyEvaluated = false; // for the moment to ensure a new evaluation each time (testing) 
		
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
					// no need to finish this path since already correct
					// but don't forget to store the complete path itself:
					completedTruckPaths.add(pathToComplete);
					i++;
					continue; 
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
			
			//String s = "";
			//for (LinkedList<Node> tp: completedTruckPaths) {
			//	s += "path: ";
			//	for (Node n: tp) {
			//		s += n.get_numero() + " ";
			//	}
			//}
			//System.out.println(s);
			
			/* Second, go through all paths and compute the objective functions */
			i = 0; // as above, an index to get the corresponding commodity
			this.objectiveSpace[0] = 0;
			this.objectiveSpace[1] = 0;
			this.objectiveSpace[2] = Double.NEGATIVE_INFINITY; // 2nd obj. is a maximum of risks
			// store risks summed over all commodities for each arc:
			ArrayList<Double> risksumsPerArc = new ArrayList<Double>();
			for (int arcRisk=0; arcRisk < PopulationHazmat.mygraph.nbArcs; arcRisk++) {
				risksumsPerArc.add(arcRisk, 0.0);
			}
			
			for (LinkedList<Node> path : completedTruckPaths) {
				Node a, b = path.poll();
				
				while (!path.isEmpty()) {
					a = b;
					b = path.poll(); 
					Arc currArc = PopulationHazmat.mygraph.getArc(a.get_numero(), b.get_numero());
					
					//System.out.println(a.get_numero() + " --> " + b.get_numero() + " costs "
					//		+ currArc.returnCost());
					
					// update 1st objective for first arc
					this.objectiveSpace[0] += currArc.returnCost();
					// for update of 2nd and 3rd objective, update the risk per arc
					int currArcID = currArc.returnNum();
					risksumsPerArc.set(currArcID, risksumsPerArc.get(currArcID)
								+ currArc.getRisk(this.associatedCommodities.get(i).getNum()));
				}
				i++;
				
				//System.out.println(" obj[0] -----> " + this.objectiveSpace[0]);
				
			} // now, 1st objective already computed, 2nd, and 3rd still missing
			
			//System.out.println("evaluated of 1st objective ended...");
			
			
			// go through risksumsPerRegion and compute remaining objectives as sum and max resp.
			for (int arcID=0; arcID < risksumsPerArc.size(); arcID++) {
				double currRisk = risksumsPerArc.get(arcID);	
				this.objectiveSpace[1] += currRisk; 
				if (currRisk > this.objectiveSpace[2]) {
					this.objectiveSpace[2] = currRisk;
				}
			}
			
			this.alreadyEvaluated = true;
		}
		
		//System.out.println(this);
		//System.out.println(this.objectiveSpace[0]);
		//System.out.println(this.objectiveSpace[1]);
		//System.out.println(this.objectiveSpace[2]);
		//System.out.println();
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
			
			copyOfTruckPaths.add(copiedPath);
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
		//LinkedList<Node> path = this.truckPaths.firstElement();
		
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
	
	public String toString() {
		String s = "solution: ";
		int i = 0;
		for (LinkedList<Node> path: this.truckPaths) {
			s += "\n" + i + "'th path: ";
			for (Node n: path) {
				s+= n.get_numero() + " ";
			}
			i++;
		}
		
		return s;
	}
	
	public String getRepresentation() {
		String s = "";
		int i = 1;
		for (LinkedList<Node> path: this.truckPaths) {
			if (i==1) {
				s += i + "st path (commodity " + this.associatedCommodities.get(i-1).getNum() + "): ";
			}
			else if (i==2) {
				s += i + "nd path (commodity " + this.associatedCommodities.get(i-1).getNum() + "): ";
			}
			else if (i==3) {
				s += i + "rd path (commodity " + this.associatedCommodities.get(i-1).getNum() + "): ";
			}
			else {
				s += i + "th path (commodity " + this.associatedCommodities.get(i-1).getNum() + "): ";
			}
			for (Node n: path) {
				s+= n.get_numero() + " ";
			}
			i++;
			s += " ";
		}
		
		return s;
	}
	
}
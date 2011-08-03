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
	
	/** Standard class constructor, initializes the decision space representation randomly
	 * based on the graph instance in PopulationHazmat.mygraph.
	 * 
	 * In a first implementation, a fixed number of trucks per commodity is send through
	 * the network.
	 * 
	 */
	public IndividualHazmat(){
		this.truckPaths = new Vector<LinkedList<Node>>();
		
		for (Commodity commodity: PopulationHazmat.mygraph.listCom) {
			/* create "empty" path, consisting of only the source node for this commodity */
			LinkedList<Node> emptyPath = new LinkedList<Node>();
			emptyPath.add(PopulationHazmat.mygraph.returnNode(commodity.getSource()));

			for (int i=0; i < commodity.getNbTrucks(); i++) {
				truckPaths.add(emptyPath);
			}
		}
		
		this.objectiveSpace = new double[3];
		this.eval();
	}
	
	/** Class constructor which initializes the decision and objective space representations with the given values.
	 * 
	 * @param initialTruckPaths A vector of node lists which specifies the paths through the graph
	 *                          for each truck
	 */
	public IndividualHazmat(Vector<LinkedList<Node>> initialTruckPaths) {
		this.truckPaths = initialTruckPaths;
		
		this.objectiveSpace = new double[3];
		this.eval();
	}
	
	/** Calculates the objective space values of this individual. The two objectives are number of
	 * leading ones and number of trailing zeros. Both objectives are converted such that they have to
	 * be minimized (by subtracting them from the total number of decision variables). */
	public void eval() {
		
		// TODO implement
		
		int todo=1000;
		int todoaswell=1000;
		
		objectiveSpace[0] = todo; 
		objectiveSpace[1] = todoaswell;
	}

	/** Returns a (deep) copy of this individual.
	 * 
	 * @return copy of the this individual
	 */
	public IndividualHazmat copy() {
		Vector<LinkedList<Node>> copyOfTruckPaths = new Vector<LinkedList<Node>>();
		
		for (LinkedList<Node> path: this.truckPaths) {
			LinkedList<Node> copiedPath = new LinkedList<Node>();

			for (Node node: path) {
				copiedPath.add(node);
			}
		}
		
		IndividualHazmat newInd = new IndividualHazmat(copyOfTruckPaths);
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
		
		
		// TODO implement for hazmat routing problem
		

	}
	
}
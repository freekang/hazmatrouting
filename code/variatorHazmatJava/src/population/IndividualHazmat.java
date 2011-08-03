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
	
	int[] decisionSpace;
	
	/** Standard class constructor, initializes the decision space representation randomly. */
	public IndividualHazmat(){
		this.decisionSpace = new int[10];
		for (int i = 0; i < 10; i++) {
			this.decisionSpace[i] = (Variator.randomGenerator.nextBoolean())?1:0;
		}
		this.objectiveSpace = new double[2];
		this.eval();
	}
	
	/** Class constructor which initializes the decision and objective space representations with the given values.
	 * 
	 * @param newDecisionSpace the decision space representation of the new individual
	 * @param newObjectiveSpace the objective space representation of the new individual
	 */
	public IndividualHazmat(int[] newDecisionSpace, double[] newObjectiveSpace) {
		this.decisionSpace = new int[newDecisionSpace.length];
		this.objectiveSpace = new double[newObjectiveSpace.length];
		System.arraycopy(newDecisionSpace, 0, this.decisionSpace, 0, newDecisionSpace.length);
		System.arraycopy(newObjectiveSpace, 0, this.objectiveSpace, 0, newObjectiveSpace.length);
	}
	
	/** Calculates the objective space values of this individual. The two objectives are number of
	 * leading ones and number of trailing zeros. Both objectives are converted such that they have to
	 * be minimized (by subtracting them from the total number of decision variables). */
	public void eval() {
		int leadingOnes = 0;
		int trailingZeros = decisionSpace.length - 1;
		
		while(leadingOnes < decisionSpace.length && decisionSpace[leadingOnes] == 1) {
			leadingOnes++;
		}
		
		objectiveSpace[0] = decisionSpace.length - leadingOnes; 
		
		while(trailingZeros > -1 && decisionSpace[trailingZeros] == 0) {
			trailingZeros--;
		}
		
		objectiveSpace[1] = trailingZeros + 1;
	}

	/** Returns a copy of this individual.
	 * 
	 * @return copy of the this individual
	 */
	public IndividualHazmat copy() {
		IndividualHazmat newInd = new IndividualHazmat(this.decisionSpace, this.objectiveSpace);
		return newInd;
	}
	
}
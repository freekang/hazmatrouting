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


/** Defines the individuals of the global Population.
 * Defines the objective space representation of the individuals
 * and defines two abstract methods to calculate the objective space values
 * and to create a copy of this individual.
 * 
 * @author Tamara Ulrich
 * @version 1.0
 */
abstract class IndividualAbstract {
	
	double[] objectiveSpace;
	
	/** Calculates the objective space values of this individual. The objective space
	 * values have to be defined such that lower values are better, because
	 * PISA always minimizes. */
	abstract void eval();

	/** Returns a copy of this individual.
	 * 
	 * @return copy of the this individual
	 */
	abstract IndividualAbstract copy();	
}
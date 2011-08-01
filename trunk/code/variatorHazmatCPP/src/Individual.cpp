/*
 Copyright (c) 2011 LIX, Ecole Polytechnique, Palaiseau, France

 Title: hazmat variator
 Description: variator for the problem of routing hazardous material in a graph
 Copyright: Copyright (c) 2011
 Company: Ecole Polytechnique
 Authors: Nora Touati-Moungla and Dimo Brockhoff, Ecole Polytechnique, France
 Authors (previous Java version): Tamara Ulrich, ETH Zurich, Switzerland
 */


#include "Individual.h"

/** Specifies the problem-specific behavior of an individual.
 * Defines the decision space representation of the individuals,
 * provides two constructors
 * and implements methods to calculate the objective space values of an individual
 * and to create a copy of an individual.
 *
 * @author Tamara Ulrich
 * @version 1.0
 */

	/** Standard class constructor, initializes the decision space representation randomly. */
	Individual::Individual(){
		this->decisionSpace = new vector<int>(10,0);
		for (int i = 0; i < 10; i++) {
			this->decisionSpace.at(i) = (Variator::randomGenerator.nextBoolean()) ? 1 : 0;
		}
		this->objectiveSpace = new vector<double>();
		this->eval();
	}

	/** Class constructor which initializes the decision and objective space representations with the given values.
	 *
	 * @param newDecisionSpace the decision space representation of the new individual
	 * @param newObjectiveSpace the objective space representation of the new individual
	 */
	Individual::Individual(vector<int> newDecisionSpace, vector<double> newObjectiveSpace) {
		this->decisionSpace = new vector<int>(newDecisionSpace.length,0);
		this->objectiveSpace = new vector<double>(newObjectiveSpace.length, 0);
		// deep copy of vectors:
		for (int i=0; i<newDecisionSpace.size(); i++) {
		    this->decisionSpace.at(i) = newDecisionSpace.at(i);
		}
		for (int i=0; i<newObjectiveSpace.size(); i++) {
		    this->objectiveSpace.at(i) = newObjectiveSpace.at(i);
		}
	}

	/** Calculates the objective space values of this individual. The two objectives are number of
	 * leading ones and number of trailing zeros. Both objectives are converted such that they have to
	 * be minimized (by subtracting them from the total number of decision variables). */
	void Individual::eval() {
		int leadingOnes = 0;
		int trailingZeros = decisionSpace.length - 1;

		while(leadingOnes < decisionSpace.length && decisionSpace.at(leadingOnes) == 1) {
			leadingOnes++;
		}

		objectiveSpace.at(0) = decisionSpace.length - leadingOnes;

		while(trailingZeros > -1 && decisionSpace.at(trailingZeros) == 0) {
			trailingZeros--;
		}

		objectiveSpace.at(1) = trailingZeros + 1;
	}

	/** Returns a copy of this individual.
	 *
	 * @return copy of the this individual
	 */
	Individual Individual::copy() {
		Individual newInd = new Individual(this->decisionSpace, this->objectiveSpace);
		return newInd;
	}

}

/*
 Copyright (c) 2011 LIX, Ecole Polytechnique, Palaiseau, France

 Title: hazmat variator
 Description: variator for the problem of routing hazardous material in a graph
 Copyright: Copyright (c) 2011
 Company: Ecole Polytechnique
 Authors: Nora Touati-Moungla and Dimo Brockhoff, Ecole Polytechnique, France
 Authors (previous Java version): Tamara Ulrich, ETH Zurich, Switzerland
 */


#ifndef INDIVIDUAL_H_
#define INDIVIDUAL_H_


/** Specifies the problem-specific behavior of an individual.
 * Defines the decision space representation of the individuals,
 * provides two constructors
 * and implements methods to calculate the objective space values of an individual
 * and to create a copy of an individual.
 *
 * @author Tamara Ulrich
 * @version 1.0
 */
class Individual : public IndividualAbstract {

	int decisionSpace[];

public:

	/** Standard class constructor, initializes the decision space representation randomly. */
	Individual();

	/** Class constructor which initializes the decision and objective space representations with the given values.
	 *
	 * @param newDecisionSpace the decision space representation of the new individual
	 * @param newObjectiveSpace the objective space representation of the new individual
	 */
	Individual(vector<int> newDecisionSpace, vector<double> newObjectiveSpace);

	/** Calculates the objective space values of this individual. The two objectives are number of
	 * leading ones and number of trailing zeros. Both objectives are converted such that they have to
	 * be minimized (by subtracting them from the total number of decision variables). */
	void eval();

	/** Returns a copy of this individual.
	 *
	 * @return copy of the this individual
	 */
	Individual copy();


};

#endif /* INDIVIDUAL_H_ */

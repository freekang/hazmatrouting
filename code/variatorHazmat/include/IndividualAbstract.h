/*
 Copyright (c) 2011 LIX, Ecole Polytechnique, Palaiseau, France

 Title: hazmat variator
 Description: variator for the problem of routing hazardous material in a graph
 Copyright: Copyright (c) 2011
 Company: Ecole Polytechnique
 Authors: Nora Touati-Moungla and Dimo Brockhoff, Ecole Polytechnique, France
 Authors (previous Java version): Tamara Ulrich, ETH Zurich, Switzerland
 */


#ifndef INDIVIDUALABSTRACT_H_
#define INDIVIDUALABSTRACT_H_

/** Defines the individuals of the global Population.
 * Defines the objective space representation of the individuals
 * and defines two abstract methods to calculate the objective space values
 * and to create a copy of this individual.
 *
 * @author Tamara Ulrich
 * @version 1.0
 */
class IndividualAbstract {

	vector<double> objectiveSpace;

public:

	/** Calculates the objective space values of this individual. The objective space
	 * values have to be defined such that lower values are better, because
	 * PISA always minimizes. */
	virtual void eval();

	/** Returns a copy of this individual.
	 *
	 * @return copy of the this individual
	 */
	virtual IndividualAbstract copy();
}

#endif /* INDIVIDUALABSTRACT_H_ */

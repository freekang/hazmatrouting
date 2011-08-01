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


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


/** Handles the problem-specific behavior of the global population.
 * Contains all problem-specific parameters and implements a method to set them. Implements methods 
 * to initialize a global population, to variate the individuals (recombination and mutation),
 * and to test for the stopping criterion of the EA.
 * Provides functions to perform recombination and mutation.
 * 
 * @author Tamara Ulrich
 * @version 1.0
 */
class PopulationHazmat extends PopulationAbstract {
						
	/** The number of generations of the EA, after which the variator and selector are stopped. */
	int maximumGenerations = 10;
	
	/** The probability that two individuals are recombined. */
	double recombinationProbability = 0.1;
	
	/** The probability that one individual is mutated. */
	double mutationProbability = 1;
	
	/** The probability during mutation of an individual that the current bit is flipped. */
	double bitFlipProbability = 0.1;
			
	/** Initializes the population with <code>alpha</code> individuals that have random decision space representations. */
	void initialize() {
		// Construct the initial population
		IndividualHazmat newIndividual;
		for (int i = 0; i < alpha; i++) {
			newIndividual = new IndividualHazmat();
			globalPopulation.add(newIndividual);
		}
		Variator.debugPrint("Initial population constructed.");
	}
	
	/** Performs recombination and mutation on the individuals with the given indices. First, the parents are recombined with <code>recombinationProbability</code>.
	 * Then, they are mutated with <code>mutationProbability</code>.
	 * 
	 * @param offspring the indices of the individuals to be variated
	 */
	void performVariation(int[] offspring){
		// recombination
		int k;
		if (offspring.length % 2 != 1) { // equal number of parents
			k = offspring.length;
		}
		else {
			k = offspring.length-1;
		}
		for (int i = 0; i < k; i+=2) {
			if (Variator.randomGenerator.nextDouble() <= recombinationProbability) {
				recombination(offspring[i], offspring[i+1]);
			}
		}
		
		// mutation
		for (int i = 0; i < Variator.population.lambda; i++) {
			if (Variator.randomGenerator.nextDouble() <= mutationProbability) {
				mutation(offspring[i]);
			}			
		}		
	}
	
	/** Performs uniform crossover on the two specified individuals. This means that for each bit of the decision 
	 * space representation, the values of the two individuals are switched with probability 0.5.
	 * 
	 * @param id1 index of the first individual
	 * @param id2 index of the second individual
	 */
	void recombination(int id1, int id2){
		IndividualHazmat ind1 = globalPopulation.get(id1);
		IndividualHazmat ind2 = globalPopulation.get(id2);
		
		int tmp;
		
		for (int i = 0; i < ind1.decisionSpace.length; i++) {
			if (Variator.randomGenerator.nextDouble() <= 0.5) { // switch at index i
				tmp = ind1.decisionSpace[i];
				ind1.decisionSpace[i] = ind2.decisionSpace[i];
				ind2.decisionSpace[i] = tmp;
			}
		}
		
		globalPopulation.set(id1, ind1);
		globalPopulation.set(id2, ind2);	
	}
	
	/** Performs independent bit mutation on the specified individual. This means that each bit
	 * of the individual is flipped with <code>bitFlipProbability</code>.
	 * 
	 * @param id index of the individual
	 */
	void mutation(int id){
		IndividualHazmat ind = globalPopulation.get(id);
		
		for (int i = 0; i < ind.decisionSpace.length; i++) {
			if (Variator.randomGenerator.nextDouble() <= bitFlipProbability) {
				ind.decisionSpace[i] = 1-ind.decisionSpace[i];
			}
		}
		globalPopulation.set(id, ind);
	}
	
	/** Tests whether the current generation is the last one. 
	 * 
	 * @return true if the EA should finish, else false
	 */
	boolean isFinished() {
		return Variator.population.generation >= Variator.population.maximumGenerations+1;
	}
		
	/** Defines the possible set of parameters in the parameter and the configuration file.
	 * Returns false if given a parameter which is not listed in this method or if the 
	 * parameter could not be set.
	 * 
	 * @param paramName the name of the parameter which has to be set
	 * @param paramValue the value of the parameter which has to be set
	 * @return true if the parameter has been set successfully, else false
	 */
	public boolean setNonfixedParam(String paramName, String paramValue){
		paramName = paramName.toLowerCase();
		if (paramName.equals("maximum_generations")){
			try {
				maximumGenerations = Integer.parseInt(paramValue);
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		}
		else if (paramName.equals("recombination_probability")){
			try {
				recombinationProbability = Double.parseDouble(paramValue);
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		}
		else if (paramName.equals("mutation_probability")){
			try {
				mutationProbability = Double.parseDouble(paramValue);
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		}
		else if (paramName.equals("bitflip_probability")){
			try {
				bitFlipProbability = Double.parseDouble(paramValue);
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		}
		return false;
	}

	/** Tests whether the parameters in the parameter file have reasonable values. */
	void testParam(){
		// test if mu and lambda from config file are the same, otherwise exit PISA
		if ((mu != lambda)){
			System.err.println("ERROR: wsnNodePlacer::Launcher:main: mu and lambda read from config file are not the same!");
			System.exit(1);
		}

		// test if dim equals 2, the only allowed setting for this problem
		if (dim != 2) {
			System.err.println("ERROR: wsnNodePlacer::Launcher:main: For this problem, number of objectives needs to be 2 not "+ dim+"!");
			System.exit(1);
		}
	}
	
	/** Writes all individuals of the population to the output file. The name of the output file is specified in the parameter
	 * file. First, the number of individuals is printed. Then, each line in the output file corresponds to an individual, where the first elements indicate the objective
	 * space values and the remaining elements give the decision space representation. Finally, an <code>END</code> tag is written.
	 */
	void writeOutput(){
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(Variator.population.outputFileName)), true);
			writer.println(Integer.toString((Variator.population.globalPopulation.size()-Variator.population.freeIdentities.size())));
			IndividualHazmat currentIndividual;

			for (int i = 0; i < Variator.population.globalPopulation.size(); i++) {
				if (!Variator.population.freeIdentities.contains(i)){
					writer.print(i + "\t");
					currentIndividual = Variator.population.globalPopulation.get(i);
					for (int j = 0; j < Variator.population.dim; j++) {
						writer.printf("%10.10f\t", currentIndividual.objectiveSpace[j]);
					}
					for (int j = 0; j < currentIndividual.decisionSpace.length; j++) {
						writer.print(currentIndividual.decisionSpace[j] + " ");
					}					
					writer.println("");
				}
			}			
			writer.println("END");
			writer.close();		
		}
		catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
			
}
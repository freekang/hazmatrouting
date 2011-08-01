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
import java.util.ArrayList;

/** Handles the PISA-specific behavior of the global population.
 * Contains all PISA-specific parameters and specifies an abstract method to set them and test them for consistency. 
 * Defines abstract methods to initialize a global population, to variate the individuals (recombination and mutation),
 * and to test for the stopping criterion of the EA.
 * Provides methods to add, retrieve and remove individuals from the global population, to update the global population
 * with the individuals given in the archive file, to copy individuals, to set parameters
 * and to write the whole or a part of the population to a file.
 * 
 * @author Tamara Ulrich
 * @version 1.0
 */
abstract class PopulationAbstract {
	
	/** The individuals generated during execution of the EA, which are considered for further use. */
	ArrayList<Individual> globalPopulation;
			
	/** The indices of the elements of the <code>globalPopulation</code> ArrayList
	 * which contain <code>null</code>. The indices of the individuals in the global population
	 * are static, which means that when the global population contains three individuals with the indices
	 * 1, 2 and 3 and the second individual is removed from the global population, the global population still contains
	 * three elements, but only the first and the third point to an individual, whereas the second is <code>null</code>.
	 * The <code>freeIdentities</code> list then contains of the single element <code>2</code>.
	 */
	ArrayList<Integer> freeIdentities; 

	/** The individuals which have been selected (by the selector) for variation. */
	int[] parents;		

	/** The individuals generated by applying recombination and mutation to the parents (by the variator). */
	int[] offspring;

	
	/** The seed of the random generator. Two runs of the variator with the same seed generate the same result. */
	int seed = 0;
	
	/** The current generation of the population. */
	int generation = 0;
	
	/** The time interval in seconds in which the state file is checked in the state machine. */
	int pollingInterval = 500;
	
	/** Says whether the debug messages should be printed (<code>true</code>) or whether the variator should execute silently (<code>false</code>). */
	boolean debugPrint = false;
		
	// PISA configuration variables
	
	/** The size of the initial population. */
	int alpha;
	
	/** The number of parents chosen by the selector for variation. Has to be equal to alpha. */
	int mu;
	
	/** The number of offspring generated by variating the <code>mu</code> parents. Has to be equal to alpha. */
	int lambda;
	
	/** The number of objectives. */
	int dim;
	
	// Communication files
	
	/** Communication file containing the state of the state machine. The file contains one single integer value. */
	String stateFile = "./PISA_sta";
	
	/** Communication file containing the indices of the individuals which are considered for further use by the selector. The parents are a subset of 
	 * these individuals. Each line of the file contains one single integer number (the index). Additionally, the first line is the number of indices and the 
	 * last line is an <code>END</code> tag. */
	String archiveFile = "./PISA_arc";
	
	/** Communication file containing the indices of the offspring. The offspring is generated by the variator by applying recombination
	 * and mutation to the parents (which have been chosen by the selector). Each line of the file contains one integer number (the index) 
	 * plus the objective space values of the corresponding individual. Additionally, the first line is the number of elements 
	 * ((number of indices)*(number of objective space values + 1)) and the 
	 * last line is an <code>END</code> tag. */
	String variatorFile = "./PISA_var";
	
	/** Communication file containing the parents which have been chosen for variation by the selector. The selector chooses them from its own archive of reusable individuals
	 * (the indices of which are stored in the archive file) and the offspring generated by the variator. Each line of the file contains one single integer number (the index). Additionally, the first line is the number of indices and the 
	 * last line is an <code>END</code> tag. */
	String selectorFile = "./PISA_sel";
	
	/** Communication file containing the the sizes of the initial population, of the parents and of the offspring as well as the number of objectives. */
	String configFile = ".PISA_cfg";
	
	/** Communication file containing the initial population generated by the variator. Each line of the file contains one integer number (the index) 
	 * plus the objective space values of the corresponding individual. Additionally, the first line is the number of elements 
	 * ((number of indices)*(number of objective space values + 1)) and the 
	 * last line is an <code>END</code> tag. */
	String initialFile = "./PISA_ini";
	
	// Other filenames
	
	/** The name of the file which contains the user-defined variator parameters which are read in State 0. */
	String paramFileName = "param.txt";
		
	/** The name of the file where the final population will be printed. After the last variation, the selector executes once more. All individuals
	 * which are contained in the archive file afterwards are written to the output file. For each individual, the index plus the objective space 
	 * values are written.
	 */
	String outputFileName = "out.txt";
		
	// Filepaths
	
	/**
	 * Holds the path and name-parts of the communication files of the PISA protocol.
	 * For example if <code>commFilePath</code> is <code>./PISA_</code>,
	 * then the variator file will be appended a <code>var</code>: <code>./PISA_var</code>.
	 * The same goes for the status (<code>sta</code>), selector files (<code>sel</code>),
	 * initial files (<code>ini</code>), the archive files (<code>arc</code>) and the
	 * configuration files (<code>cfg</code>).
	 */
	String commFilePath = "./PISA_";
	
	/** Constructor which instantiates the global Population and the <code>freeIdentities</code> list. */
	public PopulationAbstract() {
		globalPopulation = new ArrayList<Individual>(0);
		freeIdentities = new ArrayList<Integer>(0);
	}
	
	// Abstract methods: These methods have to be implemented by the user in Population.java
	
	/** Initializes the population with <code>mu</code> individuals. */
	abstract void initialize();

	/** Performs variation on a set of given individuals.
	 * 
	 * @param offspring the indices of the individuals which have to be variated
	 */
	abstract void performVariation(int[] offspring);
	
	/** Tests if the stopping criterion of the EA has been met.
	 * 
	 * @return true if the EA should stop, false if it should continue with the next generation
	 */
	abstract boolean isFinished();

	/** Sets the user-defined parameters. The parameter file can only contain parameters
	 * which are either handled in this method or in the <code>setFixedParam</code> method of 
	 * the abstract population class <code>PopulationAbstract</code>.
	 * 
	 * @param paramName the name of the parameter to be set
	 * @param paramValue the value of the parameter to be set
	 * @return true is the parameter has been successfully set, else false
	 */
	abstract boolean setNonfixedParam(String paramName, String paramValue);
	
	/** Tests the parameter for consistent values. If there are some values or value combination
	 * that should not be allowed (e.g. mu != lambda), the user can test them in this method.
	 * If all values are allowed, the method can be left empty.
	 */
	abstract void testParam();
	
	// Normal methods
	
	/**
	 * Reads the individuals from the archive file and deletes all individuals which are not contained in this file from the global population.
	 * The archive file contains the individuals which are considered for further use by the selector. These individuals include the parents.
	 */
	void performClean() {

		int[] arcIdentities;
		ArrayList<Integer> arcIdentitiesList;
		ArrayList<Individual> oldGlobalPopulation;
		
		arcIdentities = Variator.fileManager.readArcSelFile(Variator.population.archiveFile);
		// convert arcIdentities to an ArrayList
		arcIdentitiesList = new ArrayList<Integer>();
		for (int i = 0; i < arcIdentities.length; i++) {
			arcIdentitiesList.add(arcIdentities[i]);
		}
		Variator.debugPrint("All active gene IDs read.");
		
		// store the globalPopulation in oldGlobalPopulation and reset the globalPopulation elements to null
		oldGlobalPopulation = globalPopulation;
		globalPopulation = new ArrayList<Individual>(0);
		for (int i = 0; i < oldGlobalPopulation.size(); i++) {
			globalPopulation.add(null);
		}
		// reset the free Identities List
		freeIdentities = new ArrayList<Integer>(0); 
		// For all elements in the oldGlobalPopulation, add the individual to the globalPopulation
		// if its index is contained in arcIdentitiesList, otherwise add the index to freeIdentities
		int arcIDCount = 0; // counts the number of different elements in the archive file
		for (int i = 0; i < globalPopulation.size(); i++) {
			if (arcIdentitiesList.contains(i)) {
				globalPopulation.set(i, oldGlobalPopulation.get(i));
				arcIDCount++;
			}
			else {
				globalPopulation.set(i,null);
				freeIdentities.add(i);
			}
		}
		if (freeIdentities.size() + arcIDCount != globalPopulation.size()){
			System.out.println("Error: not all individuals specified in the arc file were copied to the globalPopulation.");
			System.exit(1);
		}
	}

	/**
	 * Reads the selector file (containing the parents), makes a copy of them (called offspring) and adds
	 * the offspring to the global population. It then performs recombination and mutation on the offspring.
	 */
	public void variate() {
		parents = Variator.fileManager.readArcSelFile(Variator.population.selectorFile);
		if (parents.length != Variator.population.mu) {
			System.err.println("Selector file does not contain mu individuals");
			System.exit(1);
		}
		offspring = new int[parents.length];
		//System.arraycopy(parents, 0, offspring, 0, parents.length);
		// add copy of parents (prospective offspring) to the globalPopulation and write their indices into offspring array
		for (int i = 0; i < parents.length; i++){
			offspring[i] = addIndividual(copyIndividual(getIndividual(parents[i])));
		}
		
		performVariation(offspring);
		
		for (int i = 0; i < offspring.length; i++) {
			evalIndividual(offspring[i]);
		}

	}
		
	/** writes the whole globalPopulation (only those individuals which are not listed in the freeIdentities list) to a file.
	 * It first prints the number of individuals, then the index of the individual followed by its
	 * objective space values and finally an END tag.
	 * 
	 * @param filename the name of the file to be written (e.g. "./PISA_ini" or "./PISA_var")
	 */
	void writePopulation(String filename) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)), true);
			writer.println(Integer.toString((Variator.population.globalPopulation.size()-Variator.population.freeIdentities.size())* (Variator.population.dim + 1)));
			Individual currentIndividual;

			for (int i = 0; i < Variator.population.globalPopulation.size(); i++) {
				if (!Variator.population.freeIdentities.contains(i)){
					writer.print(i + " ");
					currentIndividual = Variator.population.globalPopulation.get(i);
					for (int j = 0; j < Variator.population.dim; j++) {
						writer.print(currentIndividual.objectiveSpace[j] + " ");
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
	
	/** writes the individuals with the given identities (with respect to the global population) to a file.
	 * It first prints the number of individuals, then the index of the individual followed by its
	 * objective space values and finally an END tag.
	 * 
	 * @param filename the name of the file to be written (e.g. "./PISA_ini" or "./PISA_var")
	 * @param identities the indices of the individuals which have to be printed to the file
	 */
	void writePopulation(String filename, int[] identities) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)), true);
			writer.println(Integer.toString(identities.length * (Variator.population.dim + 1)));
			Individual currentIndividual;
			
			for (int i = 0; i < identities.length; i++) {
				writer.print(identities[i] + " ");
				currentIndividual = Variator.population.globalPopulation.get(identities[i]);
				//objValues = currentIndividual.objectiveSpace;
				for (int j = 0; j < Variator.population.dim; j++) {
					writer.print(currentIndividual.objectiveSpace[j] + " ");
				}
				writer.println("");				
			}
			
			writer.println("END");
			writer.close();		
		}
		catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/** Writes all individuals of the population to the output file. The name of the output file is specified in the parameter
	 * file.
	 */
	abstract void writeOutput();	
	
	/** Defines the possible set of parameters in the parameter and the configuration file.
	 * Returns false if given a parameter which is not listed in this method or if the 
	 * parameter could not be set. User-defined parameters are set with a similar method
	 * <code>setNonfixedParam</code> in class <code>Population</code>.
	 * 
	 * @param paramName the name of the parameter which has to be set
	 * @param paramValue the value of the parameter which has to be set
	 * @return true if the parameter has been set successfully, else false
	 */
	boolean setFixedParam(String paramName, String paramValue){
		paramName = paramName.toLowerCase();
		if (paramName.equals("seed")){
			try {
				Variator.population.seed = Integer.parseInt(paramValue);
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		}
		else if (paramName.equals("debug_print")){
			try {
				Variator.population.debugPrint = Boolean.parseBoolean(paramValue);
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		}
		else if (paramName.equals("alpha")){
			try {
				Variator.population.alpha = Integer.parseInt(paramValue);
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		}
		else if (paramName.equals("mu")){
			try {
				Variator.population.mu = Integer.parseInt(paramValue);
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		}
		else if (paramName.equals("lambda")){
			try {
				Variator.population.lambda = Integer.parseInt(paramValue);
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		}
		else if (paramName.equals("dim")){
			try {
				Variator.population.dim = Integer.parseInt(paramValue);
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		}
		else if (paramName.equals("output_file_name")){
			try {
				Variator.population.outputFileName = paramValue;
				return true;
			}
			catch (Exception ex) {
				return false;
			}
		}
		return false;
	}

	/** Takes an index and returns the individual which is located at this index in the global population
	 * 
	 * @param id index of the individual
	 * @return the individual with the given index in the global population
	 */
	Individual getIndividual(int id) {
		return globalPopulation.get(id);
	}
	
	/** Returns a copy of the given individual.
	 * 
	 * @param ind individual to be copied
	 * @return copy of the given individual
	 */
	Individual copyIndividual(Individual ind) {
		Individual newInd = ind.copy();
		return newInd;
	}
	
	/** Adds an individual to the global Population and returns the index, where the individual has
	 * been placed.
	 * 
	 * @param ind individual which has to be added to the global population
	 * @return index, where the individual has been placed in the global Population
	 */
	int addIndividual(Individual ind) {
		int id = 0;
		if (!freeIdentities.isEmpty()){
			// take the first one of the free elements in globalPopulation and place the new individual there
			id = freeIdentities.get(0);
			globalPopulation.set(id, ind);
			freeIdentities.remove(0);
			return id;
		}
		else {
			// no free elements in the globalPopulation. Append new individual at the end.
			globalPopulation.add(ind);
			return globalPopulation.size()-1; // return the last index
		}
	}
	
	/** Removes the individual with the given index from the global population. To do so, it sets the element of the global population at
	 * the given index to <code>null</code> and adds the index to the list of free identities <code>freeIdentities</code>.
	 * 
	 * @param id index of the individual which has to be removed from the global population
	 */
	void removeIndividual(int id) {
		globalPopulation.set(id, null);
		freeIdentities.add(id);
	}
	
	/** Updates the objective space values of the individual with the given index. If the decision space representation of an individual is
	 * changed, this function must be called to keep the objective space representation up to date.
	 * 
	 * @param id index of the individual whose objective space values have to be evaluated
	 */
	void evalIndividual(int id) {
		Individual ind = getIndividual(id);
		ind.eval();
		globalPopulation.set(id, ind);
	}

}
/*
 Copyright (c) 2011 LIX, Ecole Polytechnique, Palaiseau, France

 Title: hazmat variator
 Description: variator for the problem of routing hazardous material in a graph
 Copyright: Copyright (c) 2011
 Company: Ecole Polytechnique
 Authors: Nora Touati-Moungla and Dimo Brockhoff, Ecole Polytechnique, France
 Authors (previous Java version): Tamara Ulrich, ETH Zurich, Switzerland
 */

#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <string>
#include <exception>

#include "Individual.h"
#include "Variator.h"
#include "PopulationAbstract.h"

using namespace std;


/** Handles the PISA-specific behavior of the global population.
 * Contains all PISA-specific parameters and specifies an abstract method to set them and test them for consistency. 
 * Defines abstract methods to initialize a global population, to variate the individuals (recombination and mutation),
 * and to test for the stopping criterion of the EA.
 * Provides methods to add, retrieve and remove individuals from the global population, to update the global population
 * with the individuals given in the archive file, to copy individuals, to set parameters
 * and to write the whole or a part of the population to a file.
 * 
 * @author Dimo Brockhoff (C++ adaptation) and Tamara Ulrich (original Java version)
 * @version 1.0
 */
	

	
	/** Constructor which instantiates the global Population and the <code>freeIdentities</code> list. */
	PopulationAbstract::PopulationAbstract() {
		vector<Individual> globalPopulation(0);
		vector<int> freeIdentities(0);

		/* set parameters to their standard values */
		seed = 0;
		generation = 0;
		pollingInterval = 500;
		debugPrint = false;
		stateFile = "./PISA_sta";
		archiveFile = "./PISA_arc";
		variatorFile = "./PISA_var";
		selectorFile = "./PISA_sel";
		configFile = ".PISA_cfg";
		initialFile = "./PISA_ini";
		paramFileName = "param.txt";
		outputFileName = "out.txt";
		commFilePath = "./PISA_";
	}
	

	
	
	// Normal methods
	
	/**
	 * Reads the individuals from the archive file and deletes all individuals which are not contained in this file from the global population.
	 * The archive file contains the individuals which are considered for further use by the selector. These individuals include the parents.
	 */
	void PopulationAbstract::performClean() {

		vector<int> arcIdentitiesList;
		vector<Individual> oldGlobalPopulation;
		
		int arcIdentities[] = Variator::fileManager.readArcSelFile(Variator::population.archiveFile);
		// convert arcIdentities to a Vector
		arcIdentitiesList = vector<int>(0);
		int i;
		for (i = 0; i < arcIdentities.size; i++) {
			arcIdentitiesList.push_back(arcIdentities[i]);
		}
		Variator::debugPrint("All active gene IDs read.");
		
		// store the globalPopulation in oldGlobalPopulation and reset the globalPopulation elements to null
		oldGlobalPopulation = globalPopulation;
		globalPopulation = std::vector<Individual>(0);
		//
		// DO WE REALLY NEED THIS?
		//
		//for (int i = 0; i < oldGlobalPopulation.size(); i++) {
		//	globalPopulation.push_back(NULL);
		//}
		// reset the free Identities List
		freeIdentities = std::vector<int>(0);
		// For all elements in the oldGlobalPopulation, add the individual to the globalPopulation
		// if its index is contained in arcIdentitiesList, otherwise add the index to freeIdentities
		int arcIDCount = 0; // counts the number of different elements in the archive file

		for (int i = 0; i < globalPopulation.size(); i++) {
			if (PopulationAbstract::contains(arcIdentitiesList, i)) {
				globalPopulation.at(i) = oldGlobalPopulation.at(i);
				arcIDCount++;
			}
			else {
				globalPopulation.at(i) = null;
				freeIdentities.push_back(i);
			}
		}
		if (freeIdentities.size() + arcIDCount != globalPopulation.size()){
			cout << "Error: not all individuals specified in the arc file were copied to the globalPopulation.";
			exit(1);
		}
	}

		
	/** writes the whole globalPopulation (only those individuals which are not listed in the freeIdentities list) to a file.
	 * It first prints the number of individuals, then the index of the individual followed by its
	 * objective space values and finally an END tag.
	 * 
	 * @param filename the name of the file to be written (e.g. "./PISA_ini" or "./PISA_var")
	 */
	void PopulationAbstract::writePopulation(string filename) {
		ofstream myfile (filename.c_str());


		if (myfile.is_open()) {
			myfile << sprintf("%s\n", (Variator::population::globalPopulation.size()-Variator::population::freeIdentities.size()) * (Variator::population.dim + 1));
			Individual currentIndividual;

			for (int i = 0; i < Variator::population.globalPopulation.size(); i++) {
				if (!contains( Variator::population.freeIdentities, i) ){
					myfile << sprintf("%d ", i);
					currentIndividual = Variator::population.globalPopulation.get(i);
					for (int j = 0; j < Variator::population.dim; j++) {
						myfile << sprintf("%e ", currentIndividual.objectiveSpace[j]);
					}
					myfile << "\n";
				}
			}
			myfile << "END\n";
			myfile.close();
		}
		else cout << sprintf("Unable to open file %s", filename.c_str());
	}
	
	/** writes the individuals with the given identities (with respect to the global population) to a file.
	 * It first prints the number of individuals, then the index of the individual followed by its
	 * objective space values and finally an END tag.
	 * 
	 * @param filename the name of the file to be written (e.g. "./PISA_ini" or "./PISA_var")
	 * @param identities the indices of the individuals which have to be printed to the file
	 */
	void PopulationAbstract::writePopulation(string filename, int identities[]) {
		ofstream myfile (filename.c_str());

		if (myfile.is_open()) {
			myfile << sprintf("%s\n", identities.length * (Variator::population.dim + 1));
			Individual currentIndividual;

			for (int i = 0; i < identities.length; i++) {
				if (!contains( Variator::population.freeIdentities, i) ){
					myfile << sprintf("%d ", identities[i]);
					currentIndividual = Variator::population.globalPopulation.get(identities[i]);;
					for (int j = 0; j < Variator::population.dim; j++) {
						myfile << sprintf("%e ", currentIndividual.objectiveSpace[j]);
					}
					myfile << "\n";
				}
			}
			myfile << "END\n";
			myfile.close();
		}
		else cout << sprintf("Unable to open file %s", filename.c_str());

	}
	
	
	/** Defines the possible set of parameters in the parameter and the configuration file.
	 * Returns false if given a parameter which is not listed in this method or if the 
	 * parameter could not be set. User-defined parameters are set with a similar method
	 * <code>setNonfixedParam</code> in class <code>Population</code>.
	 * 
	 * @param paramName the name of the parameter which has to be set
	 * @param paramValue the value of the parameter which has to be set
	 * @return true if the parameter has been set successfully, else false
	 */
	bool PopulationAbstract::setFixedParam(string paramName, string paramValue){
		paramName = paramName.toLowerCase();
		if (paramName == "seed"){
			try {
				Variator::population.seed = (int)paramValue;
				return true;
			}
			catch (exception& e) {
				cout << e.what() << endl;
				return false;
			}
		}
		else if (paramName == "debug_print"){
			try {
				Variator::population.debugPrint = (bool)(paramValue);
				return true;
			}
			catch (exception& e) {
				cout << e.what() << endl;
				return false;
			}
		}
		else if (paramName == "alpha"){
			try {
				Variator::population.alpha = (int)(paramValue);
				return true;
			}
			catch (exception& e) {
				cout << e.what() << endl;
				return false;
			}
		}
		else if (paramName == "mu"){
			try {
				Variator::population.mu = (int)(paramValue);
				return true;
			}
			catch (exception& e) {
				cout << e.what() << endl;
				return false;
			}
		}
		else if (paramName == "lambda"){
			try {
				Variator::population.lambda = (int)(paramValue);
				return true;
			}
			catch (exception& e) {
				cout << e.what() << endl;
				return false;
			}
		}
		else if (paramName == "dim"){
			try {
				Variator::population.dim = (int)(paramValue);
				return true;
			}
			catch (exception& e) {
				cout << e.what() << endl;
				return false;
			}
		}
		else if (paramName  == "output_file_name"){
			try {
				Variator::population.outputFileName = paramValue;
				return true;
			}
			catch (exception& e) {
				cout << e.what() << endl;
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
	Individual PopulationAbstract::getIndividual(int id) {
		return globalPopulation[id];
	}
	
	/** Returns a copy of the given individual.
	 * 
	 * @param ind individual to be copied
	 * @return copy of the given individual
	 */
	Individual PopulationAbstract::copyIndividual(Individual ind) {
		Individual newInd = ind.copy();
		return newInd;
	}
	
	/** Adds an individual to the global Population and returns the index, where the individual has
	 * been placed.
	 * 
	 * @param ind individual which has to be added to the global population
	 * @return index, where the individual has been placed in the global Population
	 */
	int PopulationAbstract::addIndividual(Individual ind) {
		int id = 0;
		if (!freeIdentities.empty()){
			// take the last of the free indices in globalPopulation and place the new individual there
			id = freeIdentities.back();
			freeIdentities.pop_back();
			globalPopulation[id] = ind;
			return id;
		}
		else {
			// no free elements in the globalPopulation. Append new individual at the end.
			globalPopulation.push_back(ind);
			return globalPopulation.size()-1; // return the last index
		}
	}
	
	/** Removes the individual with the given index from the global population. To do so, it sets the element of the global population at
	 * the given index to <code>null</code> and adds the index to the list of free identities <code>freeIdentities</code>.
	 * 
	 * @param id index of the individual which has to be removed from the global population
	 */
	void PopulationAbstract::removeIndividual(int id) {
		globalPopulation[id] = null;
		freeIdentities.push_back(id);
	}
	
	/** Updates the objective space values of the individual with the given index. If the decision space representation of an individual is
	 * changed, this function must be called to keep the objective space representation up to date.
	 * 
	 * @param id index of the individual whose objective space values have to be evaluated
	 */
	void PopulationAbstract::evalIndividual(int id) {
		Individual ind = getIndividual(id);
		ind.eval();
		globalPopulation[id] = ind;
	}

	/** Returns true iff integer 'i' is included at least once in vectorOfInts
	 *
	 * */
	bool PopulationAbstract::contains(vector<int> vectorOfInts, int i) {
		bool found = false;
		for (vector<int>::iterator it = vectorOfInts.begin(); it != vectorOfInts.end(); it++) {
		    if (*it == i) {
		    	found = true;
		    }
		}
		return found;
	}


	/**
	 * Reads the selector file (containing the parents), makes a copy of them (called offspring) and adds
	 * the offspring to the global population. It then performs recombination and mutation on the offspring.
	 */
	void PopulationAbstract::variate() {
		parents = Variator:fileManager.readArcSelFile(Variator:population.selectorFile);
		if (parents.length != Variator:population.mu) {
			cerr << "Selector file does not contain mu individuals";
			exit(1);
		}
		offspring = int(parents.length);
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




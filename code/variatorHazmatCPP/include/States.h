/*
 * States.h
 *
 *  Created on: 6 juil. 2011
 *      Author: dimo
 */

#ifndef STATES_H_
#define STATES_H_


#include <string>
#include <exception>

#include "Variator.h"

using namespace std;

/** Contains the methods which are called in the different states of the <code>StateMachine</code>.
 * It includes two methods for state 0 and state 2, as well as methods for reading the parameter and
 * configuration files.
 *
 * @author Tamara Ulrich
 * @version 1.0
 */
class States {

public:
	/** Initializes the variator. The parameters are read and tested for reasonability, the population is initialized
	 * and the initial file and the first variator file is written.
	 */
	static void state0();

	/** The main state of the variator which performs the variation. It cleans the population by removing all individuals
	 * which are not contained in the archive file (which is generated by the selector). Then, it performs recombination and
	 * mutation on the parent individuals given by the selector file (which is also generated by the selector) and writes the offspring
	 * to the variator file.
	 */
	static void state2();


private:
	/** Reads the parameters in the given file. Skips over empty lines and lines starting with a <code>#</code>.
	 *
	 * @param filename the name of the file which contains the parameters
	 */
	static void readParam(String filename);


};

#endif /* STATES_H_ */
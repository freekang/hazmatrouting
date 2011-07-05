/*
 * Variator.h
 *
 *  Created on: 12 juin 2011
 *      Author: dimo
 */

#ifndef VARIATOR_H_
#define VARIATOR_H_

#include <string>

using namespace std;

class Variator {
	/** Defines what is displayed in the command window
	 * if the wrong number of arguments is given
	 * with the function call.
	 */
	static void displayArgsHelp();



public:
	/** Simple constructor. Is never needed,
	 * because the Variator class has only static
	 * fields and methods.
	 */
	Variator();
	virtual ~Variator();

	static Population population;
	static FileManager fileManager;
	static StateMachine stateMachine;
	static Random randomGenerator;

	/**
	 * Writes the given string to System.out
	 * if the parameter <code>Population.debugPrint</code> is set to <code>true</code>.
	 * @param text The text which will be printed to System.out
	 */
	static void debugPrint(string text);

	/** Reads the command line arguments, initializes (state = 0)
	 * and starts the state machine */
	static void main(string args[]);

};

#endif /* VARIATOR_H_ */

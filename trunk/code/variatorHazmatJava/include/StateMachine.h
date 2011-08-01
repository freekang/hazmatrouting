/*
 Copyright (c) 2011 LIX, Ecole Polytechnique, Palaiseau, France

 Title: hazmat variator
 Description: variator for the problem of routing hazardous material in a graph
 Copyright: Copyright (c) 2011
 Company: Ecole Polytechnique
 Authors: Nora Touati-Moungla and Dimo Brockhoff, Ecole Polytechnique, France
 Authors (previous Java version): Tamara Ulrich, ETH Zurich, Switzerland
 */

#ifndef STATEMACHINE_H_
#define STATEMACHINE_H_

// Sleep handling:
#ifdef WIN32
#include <windows.h> // needed for Sleep
#else
#include <unistd.h>
#define Sleep(x) usleep((x)*1000)
#endif
// end of Sleep handling

/** Handles the transitions between the states and defines the behavior in each state.
 * When necessary, it calls methods of the <code>States</code> class.
 *
 * @author Tamara Ulrich
 * @version 1.0
 */
class StateMachine {
	/** The current state of the state machine */
	int state = -1;

public:

	/**
	 * Continuously watches the communication
	 * file which contains the state.
	 * The coding of states is as follows:
	 * <table border="1"><tr><td>
	 * 0 </td><td> Variator initialization: Variator reads parameter files, initializes global population and writes the initial and variator file. </td></tr><tr><td>
	 * 1 </td><td> Selector initialization: Selector reads the initial file and generates the archive and selector file. </td></tr><tr><td>
	 * 2 </td><td> Main variator state: Variator first reads archive file to update the population, and then reads the selector file, performs variation and generates the variator file. </td></tr><tr><td>
	 * 3 </td><td> Main selector state: Selector reads the variator file, performs environmental selection and generates the archive and selector file. </td></tr><tr><td>
	 * 4 </td><td> Variator termination: Variator reads archive file and updates population with it and then writes the updated popoulation to the output file. </td></tr><tr><td>
	 * 5 </td><td> Selector termination.</td></tr></table>
	 * <p>
	 * The state diagram looks as follows:<p>
	 * 0 -Variator-> 1 -Selector-> 2 -Variator-> 3 -Selector-> 2 -Variator-> 3  -Selector-> 2  ... -Selector-> 4 -Variator-> 5 <p>
	 */
	void runStateMachine();
};

#endif /* STATEMACHINE_H_ */

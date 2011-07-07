/*
 Copyright (c) 2011 LIX, Ecole Polytechnique, Palaiseau, France

 Title: hazmat variator
 Description: variator for the problem of routing hazardous material in a graph
 Copyright: Copyright (c) 2011
 Company: Ecole Polytechnique
 Authors: Nora Touati-Moungla and Dimo Brockhoff, Ecole Polytechnique, France
 Authors (previous Java version): Tamara Ulrich, ETH Zurich, Switzerland
 */

/** Handles the transitions between the states and defines the behavior in each state.
 * When necessary, it calls methods of the <code>States</code> class.
 *
 * @author Tamara Ulrich
 * @version 1.0
 */

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
	void StateMachine::runStateMachine(){
		while (true) {
			state = Variator::fileManager.readState();
			if (state == 0) {
				Variator::debugPrint("Initialisation sequence started.");
				States::state0();
				Variator::fileManager.writeState(1);
			}
			else if (state == 2) {
				if (Variator::population.isFinished()) {
					Variator::debugPrint("Maximum Generations reached. Preparing to terminate.");
					Variator::fileManager.writeState(4);
				}
				else {
					Variator::debugPrint("****** Generation " + Variator::population.generation + " *******");
					States::state2();
					Variator::fileManager.writeState(3);
					Variator::debugPrint("3 written to state file");
				}
			}
			else if (state == 4) {
				// terminate program
				Variator::debugPrint("Preparing to terminate.");
				Variator::population.performClean();
				Variator::population.writeOutput();
				Variator::fileManager.writeState(5);
				break;
			}
			else if (state == 8){
				// perform reset
				Variator::debugPrint("Reset state found.");
				Variator::fileManager.writeState(9);
			}
			else if (state == 11){
				// selector reset done...
				Variator::debugPrint("Selector reset performed.");
				Variator::fileManager.writeState(0);
			}
			try {
				Sleep(Variator::population.pollingInterval);
			}
			catch (exception& e) {
				cout << "Exception thrown in State Machine while waiting." << endl;
				cout << e.what() << endl;
			}
		}
	}


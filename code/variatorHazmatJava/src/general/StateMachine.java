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

package general;

/** Handles the transitions between the states and defines the behavior in each state.
 * When necessary, it calls methods of the <code>States</code> class.
 * 
 * @author Tamara Ulrich
 * @version 1.0
 */
class StateMachine {
	/** The current state of the state machine */
	int state = -1;
		
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
	void runStateMachine(){
		while (true) {
			state = Variator.fileManager.readState();
			if (state == 0) {
				System.out.println("Initialisation sequence started.");
				States.state0();				
				Variator.fileManager.writeState(1);					
			}
			else if (state == 2) {
				if (Variator.population.isFinished()) {
					Variator.debugPrint("Maximum Generations reached. Preparing to terminate.");
					Variator.fileManager.writeState(4);	
				}
				else {
					Variator.debugPrint("****** Generation " + Variator.population.generation + " *******");
					States.state2();
					Variator.fileManager.writeState(3);
					System.out.println("3 written to state file");
				}
			}
			else if (state == 4) {
				// terminate program
				Variator.debugPrint("Preparing to terminate.");
				Variator.population.performClean();
				Variator.population.writeOutput();
				Variator.fileManager.writeState(5);
				break;
				//System.exit(0);
			}
			else if (state == 8){
				// perform reset
				Variator.debugPrint("Reset state found.");
				Variator.fileManager.writeState(9);
			}
			else if (state == 11){
				// selector reset done...
				Variator.debugPrint("Selector reset performed.");
				Variator.fileManager.writeState(0);
			}
			try {
				Thread.sleep(Variator.population.pollingInterval);
			}
			catch (InterruptedException e) {}
		}


	}
}
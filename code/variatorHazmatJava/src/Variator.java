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


import java.util.Random;

/** 
 * The main variator class. Reads the command line arguments,
 * instantiates the population, the file manager and the state machine,
 * initializes (state = 0) and starts the state machine.
 * 
 * @author Tamara Ulrich
 * @version 1.0
*/
public class Variator {
	
	
	static Population population;
	
	static FileManager fileManager;
	
	static StateMachine stateMachine;
	
	static Random randomGenerator;
		
	/** Simple constructor. Is never needed,
	 * because the Variator class has only static
	 * fields and methods.
	 */
	public Variator() {
	}

	/** Reads the command line arguments, initializes (state = 0)
	 * and starts the state machine */
	public static void main(String[] args) {

		population = new Population();
		randomGenerator = new Random();
		
		if (args.length != 3) {
			displayArgsHelp();
			System.exit(0);
		}
		else {
			System.out.println(
					"Variator started");
			// set some static variables
			population.paramFileName = args[0];
			population.commFilePath = args[1];
			population.pollingInterval = (int) Math.round(1000 *
					Double.parseDouble(args[2]));
			
			// set the communication files
			population.selectorFile = args[1] + "sel";
			population.variatorFile = args[1] + "var";
			population.archiveFile = args[1] + "arc";
			population.initialFile = args[1] + "ini";
			population.stateFile = args[1] + "sta";
			population.configFile = args[1] + "cfg";
			
			// instantiate the file manager, which is able to read and write the communication files
			fileManager = new FileManager();
			// instantiate the state machine, which controls the behavior of the variator
			stateMachine = new StateMachine();
			
			// set the state to 0 (initial state)
			fileManager.writeState(0);
			
			// start the state machine
			stateMachine.runStateMachine();
					
			debugPrint("Variator terminated.");
		}
	}

	/** Defines what is displayed in the command window 
	 * if the wrong number of arguments is given
	 * with the function call.
	 */
	private static void displayArgsHelp() {
		System.out.println(
		"Please start the Variator using the following options:");
		System.out.println("Variator paramFileName commFilePath poll");
		System.out.println("where:");
		System.out.println("\t paramFileName \t \t the name of the file containing the parameters for the Variator");
		System.out.println("\t\t\t\t (normally the value 'param.txt' should be used)");
		System.out.println("\t\t\t\t but make sure that this file really exists.");
		System.out.println("\t commFilePath \t \t the name base of the communication files");
		System.out.println("\t\t\t\t e.g. 'd:\\test\\PISA_' or '/home/test/PISA_'");
		System.out.println("\t poll \t\t\t the polling interval in [sec]");
	}
	
	/**
	 * Writes the given string to System.out
	 * if the parameter <code>Population.debugPrint</code> is set to <code>true</code>.
	 * @param text The text which will be printed to System.out
	 */
	static void debugPrint(String text) {
		if (population.debugPrint) {
			System.out.println(text);
		}
	}

}
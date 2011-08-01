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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StreamTokenizer;

/** Handles access to the communication files. It contains methods to read and write the state file and
 * to read out the archive file and the selector file (which are both generated by the selector).
 * 
 * @author Tamara Ulrich
 * @version 1.0
 */ 
class FileManager {
	PrintWriter writer;
	FileReader reader;
	StreamTokenizer token;

	/** Standard constructor */
	public FileManager(){
	}
	
	/** Writes the state given in the argument to the state file. The given state has to be a single integer number.
	 * 
	 * @param newState the state which has to be written to the state file
	 */
	void writeState(int newState){
		try {
			
			writer = new PrintWriter(new BufferedWriter(new FileWriter(Variator.population.stateFile)), true);
			writer.println(newState);
			writer.close();
		}
		catch (IOException e) {
			System.err.println("Problem with 'state' file");
			e.printStackTrace();
		}
	}
	
	/** Reads and returns the state in the state file. The state file must contain a single integer number.
	 * 
	 * @return the number of the current state
	 */
	int readState(){
		int state = -1;

		try{
			reader = new FileReader(Variator.population.stateFile);
			token = new StreamTokenizer(reader);
			state = -1;
			if (token.nextToken()!=StreamTokenizer.TT_EOF){
				state = (int) token.nval;
			}
			reader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return state;
	}
	
	/** Reads the values of an archive or selector file and stores them in an integer array. Writes 0 into the file
	 * to indicate that it has been read (to prevent multiple readouts). The archive and selector files start with
	 * the number of elements (which is also the length of the return array) and then one element per line, ending
	 * with an <code>END</code> tag on the last line. Each element consists of a single integer number which indicates
	 * the index of the individual in the global population. 
	 * <p>
	 * The method terminates if the file contains a single 0, as this means that the file has already been read, which
	 * indicates an error.
	 * 
	 * @param filename the name of the file to be read (e.g. "./PISA_arc" or "./PISA_sel" 
	 * @return an array where the values in the file are stored
	 */
	int[] readArcSelFile(String filename) {
		int[] values;
		try{			
			FileReader reader = new FileReader(new File(filename));
			StreamTokenizer tokenStream = new StreamTokenizer(reader);
			tokenStream.nextToken(); // get the first token
			int numberOfValues = (int) tokenStream.nval;
			if (numberOfValues == 0) {
				System.out.println("Arc/Sel file has already been read");
				System.exit(1);
			}
			
			values = new int[numberOfValues];
			for (int i = 0; i < numberOfValues; i++) {
				tokenStream.nextToken();
				values[i] = (int) tokenStream.nval;
			}
						
			// check for END token...
			tokenStream.nextToken();
			if (!tokenStream.sval.equals("END")) {
				System.err.println("No END tag found");
			}
			
			// According to the PISA protocol, a 0 has to be written into the file that has been read (to prevent multiple readouts)
			FileWriter fw = new FileWriter(new File(filename));
			fw.write("0\r\n");
			fw.flush();
			fw.close();
			return values;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

}
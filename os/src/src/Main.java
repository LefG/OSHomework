/*
 * Jeffrey Moon
 */
package src;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		/*
		 
		 Copy the appropriate jobs.dat file into the project. Comment/uncomment assignments. If more than one
		 assignment is uncommented it will throw an error (depending on which jobs.dat file is in the project).
		 
		 The Assignment1 is incredibly late, but I guess to add some fairness, I did program it using only the
		 sample outputs that were given on your website.
		 
		 */
		
		
		/*
		Assignment1 a1 = new Assignment1();
		a1.nbns();     // No buffering, No Spooling
		a1.bns();	   // Buffering, No Spooling
		a1.bas();	   // Buffering, Spooling
		a1.mp();  	   // Buffering, Spooling, Multiprogramming
		*/
		
		Assignment2 a2 = new Assignment2();
		a2.fcfs();		// First come, first server
		a2.sjf();		// Shortest job first
		a2.priority();  // Priority
	}

}

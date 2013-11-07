package src;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Assignment2 {
	Queue<Job> jobs = new LinkedList<Job>();
	public Assignment2() throws FileNotFoundException{
		Scanner s = new Scanner(new FileReader("jobs.dat"));
		final int tSlice = s.nextInt();	// Time Slice
		final int DOG = s.nextInt();	// Degree of Multiprogramming
		while(s.hasNextLine()){
			String jobdesc = s.nextLine();
		}
		
		
		
	}
}

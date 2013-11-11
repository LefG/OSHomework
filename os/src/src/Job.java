package src;

import java.util.LinkedList;
import java.util.Queue;

public class Job {
	public LinkedList<String> todo = new LinkedList<String>();	
	
	// Assignment 1 variables
	public String jobName;
	public boolean dataOnDisk=false; 
	public boolean dataInMem = false;
	public int inbuff;
	
	// Assignment 2 variables
	public int nextBurst;
	public int wait;
	public int priority;
	public int arrivalTime;
	public int loadTime;
	public int completeTime;
	public int IO;
	public int CPU;
}

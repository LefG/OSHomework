package src;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Assignment2 {
	final int DOM;
	final int tSlice;
	LinkedList<Job> jobs = new LinkedList<Job>();
	public Assignment2() throws FileNotFoundException{
		Scanner s = new Scanner(new FileReader("jobs2.dat"));
		tSlice = s.nextInt();	// Time Slice
		DOM = s.nextInt();	// Degree of Multiprogramming
		while(s.hasNextLine()){
			String[] jobDesc = s.nextLine().split(" ");
			Job j = new Job();
			j.jobName = jobDesc[0];
			for(int i = 1; i<jobDesc.length;i++){
				j.todo.add(jobDesc[i]);
			}
			jobs.add(j);
		}
	}
	public void fcfs(){
		int time = 0;
		int curJob=0; //
		int jobListOffset = 1;
		Job[] RQ = new Job[DOM];
		int rqSize=jobs.size()-1<DOM?jobs.size()-1:DOM;
		System.out.println(rqSize);
		for(int i=1;i<jobs.size();i++){
			logger(JOBARRIVED, jobs.get(i).jobName, time);
		}
		for(int i=0; i<rqSize; i++){
			RQ[i] = jobs.remove(i+jobListOffset--);
			RQ[i].nextBurst = Integer.parseInt(RQ[i].todo.remove(1));
			logger(JOBLOADED, RQ[i].jobName, time);
			
		}
		do{
			if(curJob>=3)curJob=0;
			
			if(RQ[curJob].nextBurst>0){
				logger(JOBRUNNING, RQ[curJob].jobName, time);
				time+=RQ[curJob].nextBurst<tSlice?RQ[curJob].nextBurst:tSlice;
				RQ[curJob].nextBurst-=tSlice;
			}
			if(RQ[curJob].nextBurst<=0 && RQ[curJob].wait <=0){
				switch(RQ[curJob].todo.remove(1)){
				case "I": logger(JOBINPUT, RQ[curJob].jobName, time);
							RQ[curJob].wait=time+50;
							break;
				case "O": logger(JOBOUTPUT, RQ[curJob].jobName, time);
							RQ[curJob].wait=time+50;
							break;
				case "T": logger(JOBINTER, RQ[curJob].jobName, time);
							RQ[curJob].wait=time+200;
							break;
				}
			}
			
			
			
		}while(true);
	}
	public final int JOBLOADED=0, JOBARRIVED=1, JOBRUNNING=2, JOBTIMEOUT=3, JOBINPUT=4, JOBOUTPUT=5, JOBINTER=6;
	public void logger(int v, String jobName, int time){
		switch(v){
		case JOBLOADED: System.out.printf("%d Job %s loaded and ready\n", time, jobName);
			break;
		case JOBARRIVED: System.out.printf("%d Job %s arrived\n", time, jobName);
			break;
		case JOBRUNNING: System.out.printf("%d Job %s running\n", time, jobName);
			break;
		case JOBTIMEOUT: System.out.printf("%d Job %s timed out", time, jobName);
			break;
		case JOBINPUT: System.out.printf("%d Job %s requires input", time, jobName);
			break;
		case JOBOUTPUT: System.out.printf("%d Job %s requires output", time, jobName);
			break;
		case JOBINTER: System.out.printf("%d Job %s requires IO", time, jobName);
			break;
		}
	}
}

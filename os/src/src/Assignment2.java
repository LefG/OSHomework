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
		s.nextLine();
		while(s.hasNextLine()){
			String[] jobDesc = s.nextLine().split(" ");
			Job j = new Job();
			j.jobName = jobDesc[0];
			System.out.println(j.jobName);
			j.priority = Integer.parseInt(jobDesc[1]);
			for(int i = 2; i<jobDesc.length;i++){
				j.todo.add(jobDesc[i]);
			}
			jobs.add(j);
		}
	}
	public void fcfs(){
		int time = 0; //
		int jobListOffset = 0;
		Queue<Job> RQ = new LinkedList<Job>(); //Ready queue
		Queue<Job> DQ = new LinkedList<Job>(); //Done queue
		LinkedList<Job> wait = new LinkedList<Job>();
		int rqSize=jobs.size()<DOM?jobs.size():DOM;
		System.out.println(rqSize);
		for(int i=0;i<jobs.size();i++){
			logger(JOBARRIVED, jobs.get(i).jobName, time, null);
		}
		for(int i=0; i<rqSize; i++){
			Job j = jobs.remove(i+jobListOffset--);
			j.nextBurst = Integer.parseInt(j.todo.remove(0));
			j.arrivalTime=time;
			j.loadTime=time;
			logger(JOBLOADED, j.jobName, time, null);
			RQ.add(j);
		}
		Job j = null;

		do{
			int p = 0;
			// ANY JOBS COMPLETED THEIR WAIT?
			for(int i=0;i<wait.size();i++){
				Job waitJob = wait.get(i-p);
				if(waitJob.wait<=time){
					waitJob.wait = 0;
					waitJob.nextBurst = Integer.parseInt(waitJob.todo.remove(0));
					RQ.add(waitJob);
					logger(JOBOFFWAIT, waitJob.jobName, time, null);
					wait.remove(i);
					p++;
				}
			}
			j = RQ.poll();
			
			if(j==null){
				time+=tSlice;
			}else{
				// If the job needs the cpu
				if(j.nextBurst > 0){
					logger(JOBRUNNING, j.jobName, time, null);
					time+=j.nextBurst<tSlice?j.nextBurst:tSlice;
					j.CPU+=j.nextBurst<tSlice?j.nextBurst:tSlice;
					j.nextBurst-=tSlice;
					if(RQ.isEmpty()){
						logger(JOBTIMEOUT, j.jobName, time, j.jobName);
					}else{
						logger(JOBTIMEOUT, j.jobName, time, RQ.peek().jobName);
					}
					
					// If burst left after cpu is <=0
					if(j.nextBurst<=0){
						if(j.todo.size()>0){
							switch(j.todo.remove(0)){
							case "I": logger(JOBINPUT, j.jobName, time, null);
									  j.wait=time+50;
									  j.IO+=50;
									  wait.add(j);
									  break;
							case "O": logger(JOBOUTPUT, j.jobName, time, null);
									  j.wait = time+50;
									  j.IO+=50;
									  wait.add(j);
									  break;
							case "T": logger(JOBINTER, j.jobName, time, null);
									  j.wait = time+200;
									  j.IO+=200;
									  wait.add(j);
									  break;
							}
						}else{
							if(jobs.size()>0){
								j.completeTime = time;
								DQ.add(j);
								logger(JOBDONE, j.jobName, time, null);
								Job newjob = jobs.remove();
								newjob.nextBurst = Integer.parseInt(newjob.todo.remove());
								newjob.loadTime = time;
								RQ.add(newjob);
								logger(JOBLOADED, newjob.jobName, time, null);
							}else{
								j.completeTime=time;
								DQ.add(j);
								logger(JOBDONE, j.jobName, time, null);
							}
						}
					}else{
						RQ.add(j);
					}
				}
			}		
	
		}while(RQ.size()>0 || wait.size()>0);
		logger(ALLJOBSDONE, null, time, null);
		printJobInfo(DQ);
	}
	public void printJobInfo(Queue<Job> DQ){
		System.out.printf("Job\t\tArrival Time\tLoad Time\tCompletion Time\tCPU\t\tIO\tWait in RQ\n");
		while(!DQ.isEmpty()){
			Job j = DQ.remove();
			System.out.printf("%s\t\t%d\t\t%d\t\t%d\t\t%d\t\t%d\t\t%d\n", j.jobName,
								j.arrivalTime, j.loadTime, j.completeTime, j.CPU, j.IO, (j.completeTime-j.loadTime) - (j.CPU + j.IO));
		}
	}
	
	public final int JOBLOADED=0, JOBARRIVED=1, JOBRUNNING=2, JOBTIMEOUT=3, JOBINPUT=4, JOBOUTPUT=5, JOBINTER=6, JOBDONE = 7, OUTPUTDONE = 8,
					 JOBOFFWAIT=9, ALLJOBSDONE=10;
	public void logger(int v, String jobName, int time, String jobReadyName){
		switch(v){
		case JOBLOADED: System.out.printf("%d Job %s loaded and ready\n", time, jobName);
			break;
		case JOBARRIVED: System.out.printf("%d Job %s arrived\n", time, jobName);
			break;
		case JOBRUNNING: System.out.printf("%d Job %s running\n", time, jobName);
			break;
		case JOBTIMEOUT: System.out.printf("%d Job %s timed out\t\tJob %s ready\n", time, jobName, jobReadyName);
			break;
		case JOBINPUT: System.out.printf("%d Job %s requires input\t\tJob %s in wait\n", time, jobName, jobName);
			break;
		case JOBOUTPUT: System.out.printf("%d Job %s requires output\t\tJob %s in wait\n", time, jobName, jobName);
			break;
		case JOBINTER: System.out.printf("%d Job %s requires IO\t\tJob %s in wait\n", time, jobName, jobName);
			break;
		case JOBDONE:  System.out.printf("%d Job %s completed\n", time, jobName);
			break;
		case OUTPUTDONE: System.out.printf("%d Job %s output completed\n", time, jobName);
			break;
		case JOBOFFWAIT: System.out.printf("%d Job %s is done waiting\n", time, jobName);
			break;
		case ALLJOBSDONE: System.out.printf("%d All jobs complete\n\n", time);
		}
	}
}

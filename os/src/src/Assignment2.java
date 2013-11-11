package src;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Assignment2 {
	int DOM=0;
	int tSlice=0;
	LinkedList<Job> jobs = new LinkedList<Job>();
/*
 * Initiates the jobs list that will contains jobs not in ready queue
 */
	public void init() throws FileNotFoundException{
		jobs.clear();
		Scanner s = new Scanner(new FileReader("jobs.dat"));
		tSlice = s.nextInt();	// Time Slice
		DOM = s.nextInt();	// Degree of Multiprogramming
		s.nextLine();
		while(s.hasNextLine()){
			String[] jobDesc = s.nextLine().split(" ");
			Job j = new Job();
			j.jobName = jobDesc[0];
			j.priority = Integer.parseInt(jobDesc[1]);
			for(int i = 2; i<jobDesc.length;i++){
				j.todo.add(jobDesc[i]);
			}
			jobs.add(j);
		}
	}
	/*
	 * Job scheduling based on priority
	 */
	public void priority() throws FileNotFoundException{
		System.out.printf("\n\nPRIORITY SCHEDULING\n\n");
		init();
		int time = 0; //
		LinkedList<Job> RQ = new LinkedList<Job>(); //Ready queue
		Queue<Job> DQ = new LinkedList<Job>(); //Done queue
		LinkedList<Job> wait = new LinkedList<Job>();
		int rqSize=jobs.size()<DOM?jobs.size():DOM;
		
		for(int i=0;i<jobs.size();i++){
			logger(JOBARRIVED, jobs.get(i).jobName, time, null);
			jobs.get(i).nextBurst = Integer.parseInt(jobs.get(i).todo.remove());
		}
		// Loading shortest jobs into RQ
		for(int i=0; i<rqSize; i++){
			int min=999999, mindex=-1;
			String jobname="";
			for(int l=0; l<jobs.size();l++){
				Job j = jobs.get(l);
				if(j.priority < min){
					min=j.priority;
					mindex = l;
					jobname = j.jobName;
				}
			}
			RQ.add(jobs.remove(mindex));
			logger(JOBLOADED, jobname, time, null);
		}
		// Start punchin' out jobs
		do{
			int p = 0;
			// Check to see if any jobs are done with I/O
			// If a job is complete, insert it back into the ready queue
			// At appropriate priority
			for(int i=0;i<wait.size();i++){
				Job waitJob = wait.get(i-p);
				if(waitJob.wait<=time){
					waitJob.wait = 0;
					int addIndex=0;
					for(int l=RQ.size()-1;l>=0;l--){
						if(waitJob.priority>=RQ.get(l).priority){
							addIndex = l;
							break;
						}
					}
					RQ.add(addIndex, waitJob);
					logger(JOBOFFWAIT, waitJob.jobName, time, null);
					wait.remove(i);
					p++;
				}
			}
			if(RQ.isEmpty()){
				time+=tSlice;
			}else{
				Job j=RQ.remove();
				if(j.nextBurst>0){
					// If there is still burst to be done, do it!
					logger(JOBRUNNING, j.jobName, time, null);
					time+=j.nextBurst<tSlice?j.nextBurst:tSlice;
					j.CPU+=j.nextBurst<tSlice?j.nextBurst:tSlice;
					j.nextBurst-=tSlice;
					if(j.nextBurst>0){
						logger(JOBTIMEOUT, j.jobName, time, j.jobName);
						RQ.addFirst(j);
					// Handle IO Requests
					}else if(j.nextBurst<=0 && j.todo.size() > 0){
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
						j.nextBurst = Integer.parseInt(j.todo.remove());
					// Bring another job into ready queue, as one is completed
					}else if(j.nextBurst<=0 && j.todo.isEmpty()){
						if(jobs.size()>0){
							j.completeTime = time;
							DQ.add(j);
							logger(JOBDONE, j.jobName, time, null);
							int min=999999, mindex=-1;
							for(int i=0;i<jobs.size(); i++){
								if (jobs.get(i).priority <min){
									min=jobs.get(i).priority;
									mindex = i;
								}
							}
							Job newjob = jobs.remove(mindex);
							newjob.loadTime = time;
							RQ.add(newjob);
							logger(JOBLOADED, newjob.jobName, time, null);
						}else{
							j.completeTime=time;
							DQ.add(j);
							logger(JOBDONE, j.jobName, time, null);
						}
					}
				}
			}
		}while(!RQ.isEmpty() || !wait.isEmpty());
		printJobInfo(DQ);
	}

	public void sjf() throws FileNotFoundException{
		System.out.printf("\n\nSJF SCHEDULING\n\n");
		init();
		int time = 0; //
		LinkedList<Job> RQ = new LinkedList<Job>(); //Ready queue
		Queue<Job> DQ = new LinkedList<Job>(); //Done queue
		LinkedList<Job> wait = new LinkedList<Job>();
		int rqSize=jobs.size()<DOM?jobs.size():DOM;
		
		for(int i=0;i<jobs.size();i++){
			logger(JOBARRIVED, jobs.get(i).jobName, time, null);
			jobs.get(i).nextBurst = Integer.parseInt(jobs.get(i).todo.remove());
		}
		// Loading shortest jobs into RQ
		for(int i=0; i<rqSize; i++){
			int min=999999, mindex=-1;
			String jobname="";
			for(int l=0; l<jobs.size();l++){
				Job j = jobs.get(l);
				if(j.nextBurst < min){
					min=j.nextBurst;
					mindex = l;
					jobname = j.jobName;
				}
			}
			RQ.add(jobs.remove(mindex));
			logger(JOBLOADED, jobname, time, null);
		}
		
		do{
			int p = 0;
			for(int i=0;i<wait.size();i++){
				Job waitJob = wait.get(i-p);
				if(waitJob.wait<=time){
					waitJob.wait = 0;
					//waitJob.nextBurst = Integer.parseInt(waitJob.todo.remove());
					RQ.add(waitJob);
					logger(JOBOFFWAIT, waitJob.jobName, time, null);
					wait.remove(i);
					p++;
				}
			}
			if(RQ.isEmpty()){
				time+=tSlice;
			}else{
				Job j=RQ.remove();
				if(j.nextBurst>0){
					logger(JOBRUNNING, j.jobName, time, null);
					time+=j.nextBurst<tSlice?j.nextBurst:tSlice;
					j.CPU+=j.nextBurst<tSlice?j.nextBurst:tSlice;
					j.nextBurst-=tSlice;
					if(j.nextBurst>0){
						logger(JOBTIMEOUT, j.jobName, time, j.jobName);
						RQ.addFirst(j);
					}else if(j.nextBurst<=0 && j.todo.size() > 0){
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
						j.nextBurst = Integer.parseInt(j.todo.remove());
					}else if(j.nextBurst<=0 && j.todo.isEmpty()){
						if(jobs.size()>0){
							j.completeTime = time;
							DQ.add(j);
							logger(JOBDONE, j.jobName, time, null);
							int min=999999, mindex=-1;
							String jobname = "";
							for(int i=0;i<jobs.size(); i++){
								if (jobs.get(i).nextBurst<min){
									min=jobs.get(i).nextBurst;
									mindex = i;
									jobname = jobs.get(i).jobName;
								}
							}
							Job newjob = jobs.remove(mindex);
							newjob.loadTime = time;
							RQ.add(newjob);
							logger(JOBLOADED, newjob.jobName, time, null);
						}else{
							j.completeTime=time;
							DQ.add(j);
							logger(JOBDONE, j.jobName, time, null);
						}
					}
				}
			}
		}while(!RQ.isEmpty() || !wait.isEmpty());
		printJobInfo(DQ);
	}
	public void fcfs() throws FileNotFoundException{
		System.out.printf("\n\nFCFS SCHEDULING\n\n");
		init();
		int time = 0; //
		int jobListOffset = 0;
		Queue<Job> RQ = new LinkedList<Job>(); //Ready queue
		Queue<Job> DQ = new LinkedList<Job>(); //Done queue
		LinkedList<Job> wait = new LinkedList<Job>();
		int rqSize=jobs.size()<DOM?jobs.size():DOM;
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
		// Start executing jobs in RQ
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
		case JOBINPUT: System.out.printf("%d Job %s requires input\tJob %s in wait\n", time, jobName, jobName);
			break;
		case JOBOUTPUT: System.out.printf("%d Job %s requires output\tJob %s in wait\n", time, jobName, jobName);
			break;
		case JOBINTER: System.out.printf("%d Job %s requires IO\t\tJob %s in wait\n", time, jobName, jobName);
			break;
		case JOBDONE:  System.out.printf("\n\n%d Job %s completed\n\n", time, jobName);
			break;
		case OUTPUTDONE: System.out.printf("%d Job %s output completed\n", time, jobName);
			break;
		case JOBOFFWAIT: System.out.printf("%d Job %s is done waiting\n", time, jobName);
			break;
		case ALLJOBSDONE: System.out.printf("%d All jobs complete\n\n", time);
		}
	}
}

package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Scanner;

public class Assignment1 {
	private LinkedList<Job> jobs;
	final int DATASIZE = 100;
	final int CODESIZE = 200;
	final int ttds, dtms, ttdt, dtmt, diskSize, memSize, inBufferSize, dmaBufferSize;
	int inputbuffer=0;
	public Assignment1() throws FileNotFoundException{
		jobs = new LinkedList<Job>();
		File file = new File("jobs1.dat");
		Scanner s = new Scanner(file);
		ttds = s.nextInt();
		ttdt = s.nextInt();
		diskSize = s.nextInt();
		dtms = s.nextInt();
		dtmt = s.nextInt();
		memSize = s.nextInt();
		inBufferSize = s.nextInt();
		dmaBufferSize = s.nextInt();
		while(s.hasNextLine()){
			String jobdesc[] = s.nextLine().split(" ");
			Job j = new Job();
			j.jobName = jobdesc[0];
			for(int i=1;i<jobdesc.length;i++){
				j.todo.add(jobdesc[i]);
			}
		jobs.add(j);
		}
		
		
	}
	public void mp(){
		int uTime = 0;
		int curJob = 1;
		int time=0;
		int burst;
		int inputbuffer = 0;
		int dataOnDisk=0;
		int codeLoaded=0, dataInMem=0;
		do{
			if(!jobs.get(curJob).dataOnDisk){
				for(int i=0; i<CODESIZE/ttds; i++){
					logger(TTDCODE, jobs.get(curJob).jobName, time);
					time+=ttdt;
					jobs.get(codeLoaded).dataOnDisk = true;
					codeLoaded++;
					
				}
				logger(CODEONDISK, jobs.get(curJob).jobName, time);
			}
			
			if(!jobs.get(curJob).dataInMem){
				for(int i=0; i<CODESIZE/dtms; i++){
					logger(DTMCODE, jobs.get(curJob).jobName, time);
					time+=dtmt;
				}
				logger(CODEINMEM, jobs.get(curJob).jobName, time);
			}
			dataInMem++;
			logger(JOBSTART, jobs.get(curJob).jobName, time);
			for(int i=0;i<jobs.get(curJob).todo.size(); i+=2){
				burst = Integer.parseInt(jobs.get(curJob).todo.get(i));
				int timeAB = time+burst;
				uTime+=burst;
				boolean needData=false;
				if(i<(jobs.get(curJob).todo.size()-1)){
					needData=true;
				}
				inputbuffer=0;
				while(burst>0){
					if(dataOnDisk==0 && burst>ttdt && needData){
						logger(TTDDATA, jobs.get(curJob).jobName, time);
						time+=ttdt;
						burst-=ttdt;
						dataOnDisk+=ttds;
						logger(DATAONDISK, jobs.get(curJob).jobName, time);
					// If we still have data on disk to transfer into memory
					}else if(dataOnDisk>0 && inputbuffer+dtms<=inBufferSize && burst>dtmt && needData){
						logger(NEEDDATA, jobs.get(curJob).jobName, time);
						logger(DTMDATA, jobs.get(curJob).jobName, time);
						time+=dtmt;
						burst-=dtmt;
						inputbuffer+=dtms;
						logger(DATAINMEM, jobs.get(curJob).jobName, time);
						if(inputbuffer>=inBufferSize){
							logger(INPUTBUFFERFULL, jobs.get(curJob).jobName, time);
						}
					}else if(codeLoaded<jobs.size() && !jobs.get(codeLoaded).dataOnDisk && burst > ((CODESIZE/ttds)*ttdt)){
						for(int k=0;k<CODESIZE/ttds;k++){
							logger(TTDCODE, jobs.get(codeLoaded).jobName, time);
							time+=ttdt;
							burst-=ttdt;
						}
						logger(CODEONDISK, jobs.get(codeLoaded).jobName, time);
						jobs.get(codeLoaded).dataOnDisk=true;
						codeLoaded++;
					}else if(dataInMem<jobs.size() && jobs.get(dataInMem).dataOnDisk && burst > ((CODESIZE/dtms)*dtmt)){
						for(int k=0; k<CODESIZE/ttds;k++){
							logger(DTMCODE, jobs.get(dataInMem).jobName, time);
							time+=dtmt;
							burst-=dtmt;
						}
						logger(CODEINMEM, jobs.get(dataInMem).jobName, time);
						jobs.get(dataInMem).dataInMem=true;
						dataInMem++;
					}else{
						break;
					}
				}
				time = timeAB;
				if(i+2<jobs.get(curJob).todo.size())logger(JOBCONT, jobs.get(curJob).jobName, time);

			}
		logger(JOBDONE, jobs.get(curJob).jobName, time);
		curJob++;
		}while(curJob<jobs.size());
		System.out.printf("\n\nTotal time:\t\t%d\nUser time:\t\t%d\nSystem time:\t\t%d\n", time, uTime, time-uTime);
	}
		
	
	public void bas(){
		int uTime = 0;
		int curJob = 1;
		int time=0;
		int burst;
		int inputbuffer = 0;
		int dataOnDisk=0;
		int codeLoaded=0;
		do{
			if(!jobs.get(curJob).dataOnDisk){
				for(int i=0; i<CODESIZE/ttds; i++){
					logger(TTDCODE, jobs.get(curJob).jobName, time);
					time+=ttdt;
					jobs.get(codeLoaded).dataOnDisk = true;
					codeLoaded++;
					
				}
			}
			for(int i=0; i<CODESIZE/dtms; i++){
				logger(DTMCODE, jobs.get(curJob).jobName, time);
				time+=dtmt;
			}
			logger(JOBSTART, jobs.get(curJob).jobName, time);
			for(int i=0;i<jobs.get(curJob).todo.size(); i+=2){
				burst = Integer.parseInt(jobs.get(curJob).todo.get(i));
				int timeAB = time+burst;
				uTime+=burst;
				boolean needData=false;
				if(i<(jobs.get(curJob).todo.size()-1)){
					needData=true;
				}
				inputbuffer=0;
				while(burst>0){
					if(dataOnDisk==0 && burst>ttdt && needData){
						logger(TTDDATA, jobs.get(curJob).jobName, time);
						time+=ttdt;
						burst-=ttdt;
						dataOnDisk+=ttds;
					// If we still have data on disk to transfer into memory
					}else if(dataOnDisk>0 && inputbuffer<=(inBufferSize+dtms) && burst>dtmt && needData){
						logger(DTMDATA, jobs.get(curJob).jobName, time);
						time+=dtmt;
						burst-=dtmt;
						inputbuffer+=dtms;
						if(inputbuffer>=inBufferSize){
							logger(INPUTBUFFERFULL, jobs.get(curJob).jobName, time);
							break;
						}
					}else if(codeLoaded<jobs.size() && !jobs.get(codeLoaded).dataOnDisk && burst > (CODESIZE/ttds*ttdt)){
						for(int k=0;k<CODESIZE/ttds;k++){
							logger(TTDCODE, jobs.get(codeLoaded).jobName, time);
							time+=ttdt;
							burst-=ttdt;
						}
						jobs.get(codeLoaded).dataOnDisk=true;
						codeLoaded++;
					}else{
						break;
					}
				}
				time = timeAB;
				if(i+2<jobs.get(curJob).todo.size())logger(JOBCONT, jobs.get(curJob).jobName, time);

			}
		logger(JOBDONE, jobs.get(curJob).jobName, time);
		curJob++;
		}while(curJob<jobs.size());
		System.out.printf("\n\nTotal time:\t\t%d\nUser time:\t\t%d\nSystem time:\t\t%d\n", time, uTime, time-uTime);
	}

	public void bns(){
		int uTime=0;
		int burst;
		int time = 0, curJob = 1;
		do{
			for(int i=0; i<CODESIZE/ttds; i++){
				logger(TTDCODE, jobs.get(curJob).jobName, time);
				time+=ttdt;
			}
			for(int i=0; i<CODESIZE/dtms; i++){
				logger(DTMCODE, jobs.get(curJob).jobName, time);
				time+=dtmt;
			}
			logger(JOBSTART, jobs.get(curJob).jobName, time);
			int dataOnDisk=0;
			for(int i=0; i<jobs.get(curJob).todo.size();i+=2){
				boolean needData=false;
				if(i<(jobs.get(curJob).todo.size()-1)){
					needData=true;
				}
				burst=Integer.parseInt(jobs.get(curJob).todo.get(0));
				int timeAB=time+burst;
				uTime+=burst;
				while(burst>0){
					// If no data on disk, TTD transfer
					if(dataOnDisk==0 && burst>ttdt && needData){
						logger(TTDDATA, jobs.get(curJob).jobName, time);
						time+=ttdt;
						burst-=ttdt;
						dataOnDisk+=ttds;
					// If we still have data on disk to transfer into memory
					}else if(dataOnDisk>0 && inputbuffer+dtms<=inBufferSize && burst>dtmt && needData){
						logger(DTMDATA, jobs.get(curJob).jobName, time);
						time+=dtmt;
						burst-=dtmt;
						inputbuffer+=dtms;
						if(inputbuffer>=inBufferSize){
							logger(INPUTBUFFERFULL, jobs.get(curJob).jobName, time);
							inputbuffer=0;
							break;
						}
					}else{
						break;
					}
				}
				time=timeAB;
				if(i+2<jobs.get(curJob).todo.size())logger(JOBCONT, jobs.get(curJob).jobName, time);
			}
		logger(JOBDONE, jobs.get(curJob).jobName, time);
		curJob++;
		}while(curJob<jobs.size());
		System.out.printf("\n\nTotal time:\t\t%d\nUser time:\t\t%d\nSystem time:\t\t%d\n", time, uTime, time-uTime);	
	}
	public void nbns(){
		int uTime=0;
		int burst;
		int time = 0, curJob = 1;
		do{
			for(int i=0; i<CODESIZE/ttds; i++){
				logger(TTDCODE, jobs.get(curJob).jobName, time);
				time+=ttdt;
			}
			for(int i=0; i<CODESIZE/dtms; i++){
				logger(DTMCODE, jobs.get(curJob).jobName, time);
				time+=dtmt;
			}
			logger(JOBSTART, jobs.get(curJob).jobName, time);
			burst=Integer.parseInt(jobs.get(curJob).todo.get(0));
			time+=burst;
			uTime+=burst;
			for(int i=1; i<jobs.get(curJob).todo.size(); i+=2){
				int dataNeeded = Integer.parseInt(jobs.get(curJob).todo.get(i));
				logger(NEEDDATA, jobs.get(curJob).jobName, time);
				if((i+3)%4==0){ //TTD every other pass, as code size is 100
					do{
						logger(TTDDATA, jobs.get(curJob).jobName, time);
						time+=ttdt;
						dataNeeded-= ttds;
					}while(dataNeeded>0);
				}
				dataNeeded = Integer.parseInt(jobs.get(curJob).todo.get(i));
				for(int j=0;j<dataNeeded/dtms;j++){
					logger(DTMDATA, jobs.get(curJob).jobName, time);
					time+=dtmt;
					inputbuffer+=dtms;
					if(inputbuffer>=inBufferSize){
						logger(INPUTBUFFERFULL, jobs.get(curJob).jobName, time);
						inputbuffer=0;
						break;
					}
				}
				logger(JOBCONT, jobs.get(curJob).jobName, time);
				burst=Integer.parseInt(jobs.get(curJob).todo.get(i+1));
				time+=burst;
				uTime+=burst;
			}
			logger(JOBDONE, jobs.get(curJob).jobName, time);
			curJob++;
			
		}while(curJob<jobs.size());
		System.out.printf("\n\nTotal time:\t\t%d\nUser time:\t\t%d\nSystem time:\t\t%d\n", time, uTime, time-uTime);
	}
	private final int JOBSTART=0, TTDCODE=1, DTMCODE=2, NEEDDATA=3, DTMDATA=4, TTDDATA=5, JOBDONE=6, JOBCONT=7,
					  INPUTBUFFERFULL=8, CODEONDISK=9, CODEINMEM=10, DATAONDISK=11, DATAINMEM=12;
	public void logger(int logNum, String job, int time){
		switch(logNum){
		case JOBSTART: System.out.printf("\nStart executing job %s\t\t\t\t%d\n\n", job, time);
				break;
		case TTDCODE: System.out.printf("Tape to disk code for job %s\t\t\t%d\n", job, time);
				break;
		case DTMCODE: System.out.printf("Disk to memory code for job %s\t\t\t%d\n", job, time);
				break;
		case NEEDDATA: System.out.printf("Need data for job %s\t\t\t\t%d\n", job, time);
				break;
		case DTMDATA: System.out.printf("Disk to memory data for job %s\t\t\t%d\n", job, time);
				break;
		case TTDDATA: System.out.printf("Tape to disk data for job %s\t\t\t%d\n", job, time);
				break;
		case JOBDONE: System.out.printf("\nCompleted executing job %s\t\t\t%d\n\n", job, time);
				break;
		case JOBCONT: System.out.printf("\nContinue executing job %s\t\t\t%d\n\n", job, time);
				break;
		case INPUTBUFFERFULL: System.out.printf("Input buffer full for job %s\t\t\t%d\n", job, time);
				break;
		case CODEONDISK: System.out.printf("Code for job %s on disk\t\t\t%d\n", job, time);
				break;
		case CODEINMEM: System.out.printf("Code for job %s in memory\t\t\t%d\n", job, time);
				break;
		case DATAONDISK: System.out.printf("Data for job %s on disk\t\t\t%d\n", job, time);
				break;
		case DATAINMEM: System.out.printf("Data for job %s in memory\t\t\t%d\n", job, time);
		}
	}
}

package src;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		Scanner c = new Scanner(System.in);
		System.out.println("Choose assignment");
		int choice = c.nextInt();
		switch(choice){
		case 1:{
			Assignment1 a = new Assignment1();
			//a.mp();
			System.out.printf("Select method and data, respectively (i.e. 1 2)\n1. No buff no spool\t\t1.No Data Sample input\n"+
							 "2. Buff no spool\t\t2. Data Sample input\n3. Buff and Spool\t\t3. Emailed input\n4. MP buff and spool");
			int method = c.nextInt();
			int data = c.nextInt();
			break;
		}
		}
	}

}

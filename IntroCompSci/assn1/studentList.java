// STUDENT1_NAME: Lara Goxhaj
//STUDENT1_ID: 260574574
//LG: From here on out, comments written by me will be preceded with "LG:"

import java.io.*;   
import java.util.*;

class studentList {
	int studentID[];
	int numberOfStudents;
	String courseName;

	// A constructor that reads a studentList from the given fileName and assigns it the given courseName
	public studentList(String fileName, String course) {
		String line;
		int tempID[]=new int[4000000]; // this will only work if the number of students is less than 4000000.
		numberOfStudents=0;
		courseName=course;
		BufferedReader myFile;
		try {
			myFile = new BufferedReader(new FileReader( fileName ) );

			while ( (line=myFile.readLine())!=null ) {
				tempID[numberOfStudents]=Integer.parseInt(line);
				numberOfStudents++;
			}
			studentID=new int[numberOfStudents];
			for (int i=0;i<numberOfStudents;i++) {
				studentID[i]=tempID[i];
			}
		} catch (Exception e) {System.out.println("Can't find file "+fileName);}
	}

	// A constructor that generates a random student list of the given size and assigns it the given courseName
	public studentList(int size, String course) {
		int IDrange=2*size;
		studentID=new int[size];
		boolean[] usedID=new boolean[IDrange];
		for (int i=0;i<IDrange;i++) usedID[i]=false;
		for (int i=0;i<size;i++) {
			int t;
			do {
				t=(int)(Math.random()*IDrange);
			} while (usedID[t]);
			usedID[t]=true;
			studentID[i]=t;
		}
		courseName=course;
		numberOfStudents=size;
	}

	//Returns the number of students present in both lists L1 and L2
	public static int intersectionSizeNestedLoops(studentList L1, studentList L2) {
		int both = 0;
		int num1 = L1.studentID.length;
		int num2 = L2.studentID.length;
		int[] list1 = L1.studentID;
		int[] list2 = L2.studentID;
		for (int i = 0; i < num1; i++){
			for (int j = 0; j < num2; j++){
				if (list1[i] == list2[j])
					both++;
			}
		}
		return both;
	}

	/* This algorithm takes as input a sorted array of integers called mySortedArray, the number of elements it contains,
and the student ID number to look for
It returns true if the array contains an element equal to ID, and false otherwise.*/
	public static boolean myBinarySearch(int mySortedArray[], int numberOfStudents, int ID) {
		int left = 0;
		int right= numberOfStudents;
		int mid;
		while (right > left+1) {
			mid = left + (right - left) / 2; //LG: though (first + last), which is normally used to find mid (and equivalent
			if (mySortedArray[mid] > ID)      //to (first+(last-first)), will never exceed the largest value for ints (2^30-1)
				right = mid;                    //in this situation, this is better form when accounting for such extreme cases
			else
				left = mid;
		}
		if (mySortedArray[left] == ID)
			return true;
		return false;
	}

public static int intersectionSizeBinarySearch(studentList L1, studentList L2) {
	int both = 0;
	int num1 = L1.studentID.length;
	int num2 = L2.studentID.length;
	int[] list1 = L1.studentID;
	int[] list2 = L2.studentID;
	Arrays.sort(list1);
	Arrays.sort(list2);
	for (int i=0; i < num1; i++){
		if (myBinarySearch(list2, num2, list1[i]))
			both++;
	}
	return both;
}

public static int intersectionSizeSortAndParallelPointers(studentList L1, studentList L2) {
	int both = 0;
	int num1 = L1.studentID.length;
	int num2 = L2.studentID.length;
	int[] list1 = L1.studentID;
	int[] list2 = L2.studentID;
	int p1 = 0; //LG: pointer for list 1
	int p2 = 0; //LG: pointer for list 2
	while( p1 < num1 && p2 < num2 ){
		if( list1[p1] == list2[p2]){
			both++;
			p1++;
			p2++;
		}
		else if( list1[p1] < list2[p2] ){
			p1++;
		}
		else{
			p2++;
		}
	}
	return both;
}

public static int intersectionSizeMergeAndSort(studentList L1, studentList L2) {
	int both = 0;
	int num1 = L1.studentID.length;
	int num2 = L1.studentID.length;
	int[] list1 = L1.studentID;
	int[] list2 = L2.studentID;
	int[] A = new int[num1+num2];
	for( int i=0; i<num1; i++){
		A[i] = list1[i];
	}
	for( int i=0; i<num2; i++){
		A[i+num1] = list2[i];
	}
	Arrays.sort(A);
	int p = 0;
	while( p < num1 + num2 - 1 ){
		if( A[p] == A[p+1] ){
			both++;
			p += 2;
		}
		else
			p++;
	}
	return both;   
}

/* The main method */
/* Write code here to test your methods, and evaluate the running time of each. 2014 */
/* This method will not be marked */
public static void main(String args[]) throws Exception {

	studentList firstList;
	studentList secondList;
	// This is how to read lists from files. Useful for debugging.

	firstList=new studentList("COMP250.txt", "COMP250 - Introduction to Computer Science");
	secondList=new studentList("MATH240.txt", "MATH240 - Discrete Mathematics");
	// get the time before starting the intersections
	long startTime = System.nanoTime();
	// repeat the process a certain number of times, to make more accurate average measurements.
	for (int rep=0;rep<1000;rep++) {
		// This is how to generate lists of random IDs.
		// For firstList, we generate 16000 IDs
		// For secondList, we generate 16000 IDs

		firstList=new studentList(16000 , "COMP250 - Introduction to Computer Science");
		secondList=new studentList(16000 , "MATH240 - Discrete Mathematics");

		// run the intersection method
		//int intersection=studentList.intersectionSizeBinarySearch(firstList,secondList);
		//System.out.println("The intersection size is: "+intersection);
	}
	// get the time after the intersection
	long endTime = System.nanoTime();
	System.out.println("Running time: "+ (endTime-startTime)/1000.0 + " nanoseconds");
}
}

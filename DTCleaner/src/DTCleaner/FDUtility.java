package DTCleaner;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import weka.core.Instances;

/**
 * Functional Dependency Utilities
 * 
 */
public class FDUtility {

	
	public static void addFD(Instances i){
		System.out.println("Add new FD. Usage: attribute#->attribute#,attribute# \n e.g. \"1->2,3\"\n");
		System.out.println("# Name\n===============");
		for(int j = 0; j < i.numAttributes();j++) System.out.println(j + " " + i.attribute(j).name());
		
		Scanner sc = new Scanner(System.in);
		String fd = sc.next(); // the functional dependency 
		
	}

	/**
	 * Loads functional dependencies from file.
	 * Syntax of file:
	 * 	1->2,3,4
	 *  5->7
	 *  ...
	 * @param filename, e.g "data/FDslist.txt"
	 * @return HashMap of FDs
	 * @throws FileNotFoundException 
	 */
	public static HashMap<String, String[]> readFDs(String filename) throws FileNotFoundException{
		HashMap<String, String[]> FDs = new HashMap<String, String[]>();
		Scanner in = new Scanner(new FileReader(filename));
		
		while(in.hasNextLine()){
			String [] fd = in.nextLine().split(",");
			String key = fd[0].substring(0, fd[0].indexOf('-'));
			String [] rhs = Arrays.copyOfRange(fd, 1, fd.length);
			FDs.put(key, rhs);
		}
		
		return FDs;
	}
	
	public static void main(String[] args) {

	}

}

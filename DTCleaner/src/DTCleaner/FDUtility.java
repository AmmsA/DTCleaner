package DTCleaner;
import weka.core.Utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Enumeration;
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
		
		System.out.println("\nReading FDs: " + filename + "...\n");
		Scanner in = new Scanner(new FileReader(filename));
		
		while(in.hasNextLine()){
			String line = in.nextLine();
			if(line.contains(",")){
				String [] fd = line.split("->");
				String key = fd[0];
				System.out.println(key);
				String [] rhs = fd[1].split(",");
				FDs.put(key, rhs);
			}else if(!line.substring(line.indexOf('>')+1, line.length()).contains(",")){
				String key = line.substring(0, line.indexOf('-'));
				String[] rhs = {line.substring(line.indexOf('>')+1,line.length())};
				FDs.put(key, rhs);
			}else{
				// invalid FD syntax
				System.out.println("invalid FD syntax: "+ line);
				continue;
			}
		}
		
		
		return FDs;
	}
	
	/**
	 * Prints a summary of FDs
	 * @param FDs
	 * @param i
	 * @return String summary: summary of FDs
	 */
	public static String toSummaryString(Instances i,HashMap<String, String[]> FDs){
		StringBuilder summary = new StringBuilder();
		summary.append("Num FDs: "+FDs.keySet().size()+"\n\n");
		
		int counter = 1;
		
		for(String premise : FDs.keySet()){
			StringBuilder fd = new StringBuilder();
			fd.append(Utils.padLeft("" + (counter++), 4)+" ");
			fd.append(i.attribute(Integer.parseInt(premise)).name());
			fd.append(" -> ");
			for(String RHS : FDs.get(premise)){
				fd.append(i.attribute(Integer.parseInt(RHS)).name()+", ");
			}


			//remove the last ", "
			fd.deleteCharAt(fd.length()-1);
			fd.deleteCharAt(fd.length()-1);
			
			
			//add to summary
			summary.append(fd.toString() + "\n");
		}
		
		return summary.toString();
	}
	
	public static void main(String[] args) {

	}

	/**
	 * Check whether the dataset satisfies all FDs
	 * @param i: The dataset instances
	 * @param FDs: list of FDs
	 * @return true if dataset satisfies all FDs, otherwise false.
	 */
	
	public static <T> boolean checkFDSatisfiaction(Instances i,	HashMap<String, String[]> FDs) {
		Enumeration enumInstances = i.enumerateInstances();
		while(enumInstances.hasMoreElements()){
			System.out.println(enumInstances.toString());
		}
		
		HashMap<T[],T> map = new HashMap<T[],T>();
		for(String premiseID : FDs.keySet()){
			
		}
		return false;
	}

}

package DTCleaner;
import weka.core.Utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
			fd.append(Utils.padLeft("" + (counter++), 4)+"   ");
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
	

	/**
	 * Check whether the dataset satisfies all FDs
	 * @param i: The dataset instances
	 * @param FDs: list of FDs
	 * @return true if dataset satisfies all FDs, otherwise false.
	 */
	public static boolean checkFDSatisfaction(Instances i,	HashMap<String, String[]> FDs) {		
		HashMap<Object, List<Object>> map = new HashMap<Object,List<Object>>();
		
		System.out.println("\nChecking FD sataisfactian...\n");
		
		for(String premiseID : FDs.keySet()){
			String [] rhsIDs = FDs.get(premiseID);
			for(int j = 0; j < i.numInstances(); j++){
				List<Object> rhsValues = new LinkedList<Object>();
				for(int k = 0; k < rhsIDs.length; k++) rhsValues.add(i.instance(j).toString(Integer.parseInt(rhsIDs[k])));
				String premise = i.instance(j).toString(Integer.parseInt(premiseID));
				
				if(map.containsKey(premise) && !map.get(premise).equals(rhsValues)){
					
					System.out.println("The following pair violate an FD:");
					System.out.println(premise + " " + rhsValues.toString());
					System.out.println(premise + " " + map.get(premise).toString()+ "\n");
					
					return false;
				}
				else{
					map.put(premise, Collections.unmodifiableList(rhsValues));
				}
			}
		}
		return true;
	}
	
	/**
	 * Finds and returns a list of tuples that violates the FDs
	 * @param i
	 * @param FDs
	 * @return v, tupleIDs: Violated instances in weka instances format, and a list of tupleIDs and their FDs that they violate
	 */
	public static violatedTuples returnViolatedTuples(Instances i, HashMap<String, String[]> FDs){
		Instances v = new Instances(i,0);
		
		// Key is the premiseValue, and the value is an Entry containing tupleID values and RHS values.
		HashMap<String, SimpleImmutableEntry<List<Integer>,List<Object>>> map = new HashMap<String, SimpleImmutableEntry<List<Integer>,List<Object>>>();

		//Holds tuple index of violated tuples, and the FD it violates
		HashMap<Integer, List<String>> tupleID = new HashMap<Integer, List<String>>();
				
		System.out.println("\nFinding violated tuples...\n");
		
		for(String premiseID : FDs.keySet()){
			String [] rhsIDs = FDs.get(premiseID);
			
			// processing FD
			String fd = premiseID+"->";
			String rhs = "";
			for(String r : rhsIDs) rhs = r + ",";
			rhs = rhs.substring(0, rhs.length()-1); // delete last ','
			fd = fd+rhs; // merge
			fd = FDtoString(i, fd);
			
			for(int j = 0; j < i.numInstances(); j++){
				
				// get RHS values
				List<Object> rhsValues = new LinkedList<Object>();
				for(int k = 0; k < rhsIDs.length; k++) rhsValues.add(i.instance(j).toString(Integer.parseInt(rhsIDs[k])));
				
				// premiseValue
				String premiseValue = i.instance(j).toString(Integer.parseInt(premiseID));
				
				if(map.containsKey(premiseValue) && !map.get(premiseValue).getValue().equals(rhsValues)){
					List<Integer> tupleIndexes = map.get(premiseValue).getKey();

					// Add index to list of violated tuples and the FD it violated
					// and add the tuple to the list of violated tuples
					
					// Here we take care of the ones already in the map
					for(int index : tupleIndexes){
						if(!tupleID.containsKey(index)){
							List<String> vFDs = new LinkedList<String>();
							vFDs.add(fd);
							tupleID.put(index, vFDs);
							
							v.add(i.instance(index));
						}
					}
					
					// Here we take care of the tuple we are currently processing
					if(!tupleID.containsKey(j)){
						List<String> vFDs = new LinkedList<String>();
						vFDs.add(fd);
						tupleID.put(j,vFDs);
						
						v.add(i.instance(j));
					}

				}else if(map.containsKey(premiseValue) && !map.get(premiseValue).getKey().contains(j)){
					map.get(premiseValue).getKey().add(j);
				}else{
					List<Integer> newIDsList = new LinkedList<Integer>();
					newIDsList.add(j);
					map.put(premiseValue, new SimpleImmutableEntry<List<Integer>,List<Object>>(newIDsList,rhsValues));
				}
			}
		}
	
		System.out.println("Found: "+ v.numInstances() + " violating tuples.");
		
		violatedTuples pair = new violatedTuples(v, tupleID);
		return pair;
		
	}
	
	/**
	 * Returns the FD by it's name, e.g. given  1->2, return "HospitalName->Address1"
	 * @param i, premiseToRHS
	 * @return
	 */
	public static String FDtoString(Instances i, String FDIDs){
		String [] fd = FDIDs.split("->");
		String premise = fd[0];
		String rhs = fd[1];
		
		StringBuilder result = new StringBuilder();
		result.append(i.attribute(Integer.parseInt(premise)).name());
		result.append("->");
		if(rhs.contains(",")){
			String[] rhsList = rhs.split(",");
			for(String r : rhsList) result.append(i.attribute(Integer.parseInt(r)).name()+", ");
			//remove the last ", "
			result.deleteCharAt(result.length()-1);
			result.deleteCharAt(result.length()-1);
			
		}else{
			 result.append(i.attribute(Integer.parseInt(rhs)).name());
		}
		
		return result.toString();
		
	}

}

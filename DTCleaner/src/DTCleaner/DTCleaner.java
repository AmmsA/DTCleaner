package DTCleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;


public class DTCleaner {
	
	// Set of FDs
	HashMap<String, String[]> FDs;
	// Dataset instance
	Instances i;
	// Violated instances
	Instances violated;
	// holds index of violated tuples and the list of FDs that it violates.
	HashMap<Integer, List<String>> violatedTuplesMap;
	
	/**
	 * 
	 * @param dataInput: The input data set location, e.g. data/hospital.arff 
	 * @param FDInput: The FD input, e.g. data/FDs.txt
	 * @throws Exception 
	 */
	public DTCleaner(String dataInput, String FDInput) throws Exception{
		
		// Initialize variables
		// Reading dataset
		DataSource scource = new DataSource(dataInput);
		System.out.println("\nReading dataset: "+dataInput+"...");
		i = scource.getDataSet();
		System.out.println("\nDataset summary:");
		System.out.println(i.toSummaryString());
		// Reading FDs
		FDs = FDUtility.readFDs(FDInput);
		System.out.println("\nFDs summary:");
		System.out.println(FDUtility.toSummaryString(i, FDs));
		// initialize violated instances, same header as original instance.
		updateViolated();
	}
	
	/**
	 * Removes the violated instances from our training set.
	 * This should be performed after updateViolated() method.
	 * @return the new instances after removal
	 */
	public Instances seperateViolatedInstances(){
		System.out.println("\nSeperating violating tuples from dataset...");
		Object[] keys = violatedTuplesMap.keySet().toArray();
		Arrays.sort(keys, Collections.reverseOrder());
		int count = 0;
		// it's important to iterate in descending order (from last to first), because when we remove
		// an instance, the rest shifts by 1 position.
		for(Object k : keys){
			int index = (Integer) k;
			i.delete(index);
			count++;
		}
		if(violatedTuplesMap.keySet().size() >= 1) {
			System.out.println("Removed: "+ count);
			System.out.println("Num Instances left: "+ i.numInstances());
		}
		else System.out.println("Did not preform any removal. Violating tuples set is empty.");
		return i;
	}
	
	/**
	 * Finds the violating tuples and returns
	 * 1- Instances violated: contains the list of violating instances.
	 * 2- HashMap that maps the tuple index in the dataset and a list of FDs
	 * 		that it violates.
	 */
	private void updateViolated() {
		violatedTuples v = FDUtility.returnViolatedTuples(i, FDs);
		violated = v.instances;
		violatedTuplesMap = v.tupleID;
	}

	public Instances getViolatedInstancs(){
		return violated;
	}
	
	/**
	 * Checks wheather the data instance satisfies our FDs
	 * @return boolean
	 */
	public boolean isFDSatisfied(){
		
		return FDUtility.checkFDSatisfiaction(i, FDs);
	}
	
	/**
	 * Prints the instances to console.
	 */
	public void printInstances(){
		System.out.println(i);
	}
	
	/**
	 * Prints the violating tuple index and the FDs that it violates.
	 */
	public void printViolatingTuplesMap(){
		int count = 0;
		
		//Print header
		System.out.println("\n"+ Utils.padLeft("Num" , 5)+
				"   " + Utils.padLeft("Index" , 5)+
				"   " + " FD");
		
		for(int index : violatedTuplesMap.keySet()){
			StringBuilder row = new StringBuilder();
			row.append(Utils.padLeft("" + (count++), 5)+"   ");
			row.append(Utils.padLeft("" + (index), 5)+" : ");
			for(String FD : violatedTuplesMap.get(index)){
				row.append(FD+ " | ");
			}
			
			//remove the last " | " chars
			row.delete(row.length()-3, row.length());
			
			System.out.println(row);
		}
	}
	
	/**
	 * Replaces violating entries (w.r.t FDs) in the list of violating tuples with missing values
	 */
	public void replaceViolatingTuplesWithMissing(){
		for(String premise : FDs.keySet()){
			ArrayList<Integer> attrIndexes = new ArrayList<Integer>();
			attrIndexes.add(Integer.parseInt(premise));
			for(String rhs : FDs.get(premise)){
				attrIndexes.add(Integer.parseInt(rhs));
			}
			
			setMissingAtIndex(violated, Util.convertIntegers(attrIndexes));
		}
	}
	
	/**
	 * Sets attribute values to be "missing" in the input instances i.
	 *   
	 * @param i: instances to work with
	 * @param attIndex: array of indexes of the attribute which we would remove the values in
	 */
	public void setMissingAtIndex(Instances i, int[] attIndexes){
		for(int j = 0; j < i.numInstances(); j++)	{
			for(int attIndex : attIndexes)	i.instance(j).setMissing(attIndex);
		}
	}

	
	
	public static void main(String[] args) throws Exception {
		if(args.length != 2){
			System.out.println("\nUsage: DTCleaner <input.arff> <FDinput.txt>");
			System.out.println("Eample: DTCleaner <data/hospital.arff> <FDlist.txt>");
			System.exit(1);
		}
		
		DTCleaner cleaner = new DTCleaner(args[0],args[1]);
		//Util.mergeAttributes(cleaner.i, new int[]{5,2,3,4});
		//cleaner.seperateViolatedInstances();
		//cleaner.setMissingAtIndex(cleaner.getViolatedInstancs(), new int[]{cleaner.getViolatedInstancs().numAttributes()-1});
		//System.out.println(cleaner.getViolatedInstancs());
	}

}

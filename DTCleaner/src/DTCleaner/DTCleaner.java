package DTCleaner;
import java.util.HashMap;
import java.util.List;

import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;

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
		printViolatingTuplesMap();
	}
	
	private void updateViolated() {
		violatedTuples v = FDUtility.returnViolatedTuples(i, FDs);
		violated = v.instances;
		violatedTuplesMap = v.tupleID;
	}

	public boolean isFDSatisfied(){
		
		return FDUtility.checkFDSatisfiaction(i, FDs);
	}
	
	public void printInstances(){
		System.out.println(i);
	}
	
	public void printViolatingTuplesMap(){
		int count = 0;
		
		//Print header
		System.out.println("\n"+ Utils.padLeft("Num" , 5)+
				"   " + Utils.padLeft("Index" , 5)+
				"   " + " FD");
		
		for(int index : violatedTuplesMap.keySet()){
			if(count == 10) break;
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

	public static void main(String[] args) throws Exception {
		if(args.length != 2){
			System.out.println("\nUsage: DTCleaner <input.arff> <FDinput.txt>");
			System.out.println("Eample: DTCleaner <data/hospital.arff> <FDlist.txt>");
			System.exit(1);
		}
		
		DTCleaner cleaner = new DTCleaner(args[0],args[1]);
	}

}

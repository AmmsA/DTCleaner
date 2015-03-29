package DTCleaner;
import java.util.HashMap;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class DTCleaner {
	
	// Set of FDs
	HashMap<String, String[]> FDs;
	// Dataset instance
	Instances i;
	// Violated instances
	Instances violated;
	
	/**
	 * 
	 * @param dataInput: The input data set location, e.g. data/hospital.arff 
	 * @param FDInput: The FD input, e.g. data/FDs.txt
	 * @throws Exception 
	 */
	public DTCleaner(String dataInput, String FDInput) throws Exception{
		
		// Initialize variables
		// Reading FDs
		FDs = FDUtility.readFDs(FDInput);
		// Reading dataset
		DataSource scource = new DataSource(dataInput);
		System.out.println("\nReading dataset: "+dataInput+"...");
		i = scource.getDataSet();
		System.out.println("\nDataset summary:");
		System.out.println(i.toSummaryString());
		// initialize violated instances, same header as original instance.
		violated = new Instances(i,0);
		
	}
	

	public static void main(String[] args) {
		if(args.length != 2){
			System.out.println("\nUsage: DTCleaner <input.arff> <FDinput.txt>");
			System.out.println("Eample: DTCleaner <data/hospital.arff> <FDlist.txt>");
			System.exit(1);
		}
		
		DTCleaner cleaner = new DTCleaner(args[0],args[1]);
	}

}

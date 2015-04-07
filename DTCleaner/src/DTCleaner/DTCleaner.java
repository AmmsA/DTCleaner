package DTCleaner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Multiset;

import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;


public class DTCleaner{
	
	// Set of FDs
	HashMap<String, String[]> FDs;
	// Set of CFDs
	Multiset<CFD> CFDs;
	// Dataset instance
	private Instances i;
	// Violated instances
	private Instances violated;
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
		
		// Reading CFDs
		CFDs = CFDUtility.readCFDs("data/CFDs");
		System.out.println("\nCFDs summary:");
		System.out.println(CFDUtility.toSummaryString(i, CFDs));

		/*// Reading FDs
		FDs = FDUtility.readFDs(FDInput);
		System.out.println("\nFDs summary:");
		System.out.println(FDUtility.toSummaryString(i, FDs));
		*/
	
		// initialize violated instances, same header as original instance.
		CFDupdateViolated();
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
	 * 2- HashMap that maps the tuple index in the dataset and a list of CFDs
	 * 		that it violates.
	 */
	private void CFDupdateViolated() {
		violatedTuples v = CFDUtility.returnViolatedTuples(i, CFDs);
		violated = v.instances;
		violatedTuplesMap = v.tupleID;
	}

	/**
	 * Finds the violating tuples and returns
	 * 1- Instances violated: contains the list of violating instances.
	 * 2- HashMap that maps the tuple index in the dataset and a list of FDs
	 * 		that it violates.
	 */
	private void FDupdateViolated() {
		violatedTuples v = FDUtility.returnViolatedTuples(i, FDs);
		violated = v.instances;
		violatedTuplesMap = v.tupleID;
	}
	
	/**
	 * Returns the set of violating instances
	 * @return
	 */
	public Instances getViolatedInstancs(){
		return violated;
	}
	
	/**
	 * Returns the set of instances
	 * @return instances
	 */
	public Instances getInstancs(){
		return i;
	}
	
	/**
	 * Checks wheather the data instance satisfies our FDs
	 * @return boolean
	 */
	public boolean isFDSatisfied(){
		
		return FDUtility.checkFDSatisfaction(i, FDs);
	}
	
	/**
	 * Prints the instances to console.
	 */
	public void printInstances(){
		System.out.println(i);
	}

	/**
	 * Prints the violating instances to console.
	 */
	public void printViolatingInstances(){
		System.out.println(violated);
	}
	
	/**
	 * Prints the violating tuple index and the FDs that it violates.
	 */
	public void printFDViolatingTuplesMap(){
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
	 * Prints the violating tuple index and the CFDs that it violates.
	 */
	public void printCFDViolatingTuplesMap(){
		int count = 0;
		
		//Print header
		System.out.println("\n"+ Utils.padLeft("Num" , 5)+
				"   " + Utils.padLeft("Index" , 5)+
				"   " + " CFD");
		
		for(int index : violatedTuplesMap.keySet()){
			StringBuilder row = new StringBuilder();
			row.append(Utils.padLeft("" + (count++), 5)+"   ");
			row.append(Utils.padLeft("" + (index), 5)+" : ");
			for(String CFD : violatedTuplesMap.get(index)){
				row.append(CFD+ " | ");
			}
			
			//remove the last " | " chars
			row.delete(row.length()-3, row.length());
			
			System.out.println(row);
		}
	}
	
	/**
	 * Replaces violating entries (w.r.t FDs) in the list of violating tuples with missing values
	 */
	public void replaceFDViolatingTuplesWithMissing(){
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
	 * Replaces violating entries (w.r.t CFDs) in the list of violating tuples with missing values
	 */
	public void replaceCDFViolatingTuplesWithMissing(){
		for(CFD cfd : CFDs){
			ArrayList<Integer> attrIndexes = new ArrayList<Integer>();
			attrIndexes.add(cfd.getRHS().getKey());
			for(SimpleImmutableEntry<Integer, String> lhs : cfd.getPremise()){
				attrIndexes.add(lhs.getKey());
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

	/**
	 * Makes the classification model for each CFD and produces predictions for the test file.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void makeModel() throws IOException, InterruptedException{
		int folder = 1;
		System.out.println(CFDs.size());
		
		// this will allow us to keep track of which CFDs we made a model for. We can't make one for each because some CFDs 
		// will have the same premise and they will be considered as one CFD
		HashSet<CFD>  seen = new HashSet<CFD>();
		
		for(CFD cfd : CFDs){
			
			// check if we have already dealt with this CFD
			if(seen.contains(cfd)) continue;
			
			System.out.println("\nMaking model for: " + CFDUtility.CFDtoString(i, cfd.CFDToString()));
			
			TreeSet<Integer> targets = new TreeSet<Integer>();
			for(SimpleImmutableEntry<Integer, String> lhs : cfd.getPremise()){
				targets.add(lhs.getKey());
			}
			
			targets.add(cfd.getRHS().getKey());
			
			for(CFD otherCFDsWithSamePremise : CFDs){
				if(cfd.getPremise().equals(otherCFDsWithSamePremise.getPremise())){
					targets.add(otherCFDsWithSamePremise.getRHS().getKey());
					seen.add(otherCFDsWithSamePremise);
				}
			}
			
			Util.saveArff(i, "exp/"+folder+"/train.arff");
			Util.saveArff(violated, "exp/"+folder+"/test.arff");
			Util.makeSettingFile("exp/"+folder+"/train.arff", "exp/"+folder+"/test.arff", targets, HeuristicType.Gain, "exp/"+folder+"/");
			
			System.out.println("computing...");
			
			// Run a java app in a separate system process
			Process proc = Runtime.getRuntime().exec("java -jar lib/Clus.jar " + "exp/"+folder+"/setting.s" );
			BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line;
			while((line = input.readLine()) != null){
			    System.out.println(line);
			}
			
			input.close();
			
			folder++;
			
			seen.add(cfd);
		}
	}
	
	/**
	 * Replaces the errornous entries by the predictions made by the model.
	 */
	public void replaceByPredictions(){
		//TODO
	}
	
	
	
	
	public static void main(String[] args) throws Exception {
		if(args.length != 2){
			System.out.println("\nUsage: DTCleaner <input.arff> <FDinput.txt>");
			System.out.println("Eample: DTCleaner <data/hospital.arff> <FDlist.txt>");
			System.exit(1);
		}
		
		DTCleaner cleaner = new DTCleaner(args[0],args[1]);
		cleaner.seperateViolatedInstances();
		cleaner.printInstances();
		cleaner.printViolatingInstances();
		
		cleaner.makeModel();
		
		//System.out.println(cleaner.getViolatedInstancs());
	}

}

package DTCleaner;

import java.io.*;
import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;

import com.google.common.collect.Multiset;

import weka.core.Instance;
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
	// Number of CFDs when merged by premise
	private int CFDsMergedSize;
	// copy of correct (no noise added) dataset instance. Not modified and used for testing how many tuples were correctly classified
	private Instances orig;
	private Set<String> origInstancesSet; // assuming orig doesn't contain any duplicates
	/**
	 * 
	 * @param dataInput: The input data set location, e.g. data/hospital.arff 
	 * @param FDInput: The FD input, e.g. data/FDs.txt
	 * @throws Exception 
	 */
	public DTCleaner(String dataInput, String CFDInput) throws Exception{
		
		// Initialize variables
		// Reading dataset
		DataSource scource = new DataSource(dataInput);
		System.out.println("\nReading dataset: "+dataInput+"...");
		i = scource.getDataSet();
		System.out.println("\nDataset summary:");
		System.out.println(i.toSummaryString());
		scource = new DataSource("data/hospitalFewerAttr.arff");
		orig = scource.getDataSet();
		origInstancesSet = new LinkedHashSet<String>();
		for(int j = 0; j < orig.numInstances(); j++){
			Instance inst = orig.instance(j);
			origInstancesSet.add(inst.toString());
		}
		
		
		// Reading CFDs
		CFDs = CFDUtility.readCFDs(CFDInput);
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
	 * Returns the number of CFDs when merged by premise
	 * @return CFDsMergedSize
	 */
	public int getCFDsMergedSize(){
		return CFDsMergedSize;
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
		
		// this will allow us to keep track of which CFDs we made a model for. We can't make one for each because some CFDs 
		// will have the same premise and they will be considered as one CFD
		HashSet<CFD>  seen = new HashSet<CFD>();
		
		for(CFD cfd : CFDs){
			
			// check if we have already dealt with this CFD
			if(seen.contains(cfd)) continue;
			
			System.out.println("\nMaking model..");// for: " + CFDUtility.CFDtoString(i, cfd.CFDToString()));
			
			TreeSet<Integer> targets = new TreeSet<Integer>();
			for(SimpleImmutableEntry<Integer, String> lhs : cfd.getPremise()){
				targets.add(lhs.getKey());
			}
			

			for(CFD otherCFDsWithSamePremise : CFDs){
				if(otherCFDsWithSamePremise.equals(cfd)) continue;
				
				TreeSet<Integer> otherTargets = new TreeSet<Integer>();
				for(SimpleImmutableEntry<Integer, String> lhs : otherCFDsWithSamePremise.getPremise()){
					otherTargets.add(lhs.getKey());
				}
				

				if(otherTargets.equals(targets)){
					seen.add(otherCFDsWithSamePremise);
				}
			}
			
			//add rhs values of CFDs that have same premise keys
			for(CFD seenCFD : seen){
				targets.add(seenCFD.getRHS().getKey());
			}
			
			targets.add(cfd.getRHS().getKey());

			
			Util.saveArff(i, "exp/"+folder+"/train.arff");
			Util.saveArff(violated, "exp/"+folder+"/test.arff");
			Util.makeSettingFile("exp/"+folder+"/train.arff", "exp/"+folder+"/test.arff", targets, HeuristicType.Gain, "exp/"+folder+"/");
			
			System.out.println("computing...");


			long start = System.nanoTime();    
			

			// Run a java app in a separate system process
			Process proc = Runtime.getRuntime().exec("java -jar lib/Clus.jar " + "exp/"+folder+"/setting.s" );
			BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line;
			while((line = input.readLine()) != null){
			    System.out.println(line);
			}

			long elapsedTime = System.nanoTime() - start;
			System.out.println("Elapsed Time: " + elapsedTime);
			
			input.close();
			
			folder++;
			
			seen.add(cfd);
		}
		
		CFDsMergedSize = folder-1;
	}
	
	/**
	 * Replaces the errornous entries by the predictions made by the model.
	 * @throws IOException 
	 */
	public void replaceByPredictions() throws IOException{
		
		System.out.println("\nReplacing errornous entries with the predicted values");
		
		for(int folder = 1; folder <= CFDsMergedSize; folder++){
			
			// numOfTargets holds the number of target attributes for this model
			int numOfTargets = -1;
			// open predictions file
			FileInputStream fs = new FileInputStream("exp/"+folder+"/setting.s");
			BufferedReader br = new BufferedReader(new InputStreamReader(fs));
			
			String line = br.readLine();
			String [] targetAttr = null; 
			while(line != null){
				if(line.contains("Target = ")){
					targetAttr = line.substring(line.indexOf('=')+1, line.length()).split(",");
				}
				if(line.contains("TargetSize = ")) {
					line = line.replaceAll("\\D+","");
					numOfTargets = Integer.parseInt(line);
				}else line = br.readLine();
			}
			
			if(numOfTargets < 0 || targetAttr == null){
				System.out.println("\nError: Coudln't complete method replaceByPredictions()");
				System.exit(1);
			}
			
		
			// open predictions file
			FileInputStream fs0 = new FileInputStream("exp/"+folder+"/setting.test.pred.arff");
			BufferedReader br0 = new BufferedReader(new InputStreamReader(fs0));
			
			// skip lines until we reach line containing "@DATA"
			
			String predictedLine = br0.readLine();
			while(predictedLine != null){
				if(predictedLine.contentEquals("@DATA")) {
					predictedLine = br0.readLine();
					break;
				}else predictedLine = br0.readLine();
			}
			
			// open test file.
			FileInputStream fs1 = new FileInputStream("exp/"+folder+"/test.arff");
			BufferedReader br1 = new BufferedReader(new InputStreamReader(fs1));

		    String tmpFileName = "exp/"+folder+"/testCleaned.arff";
		    BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFileName));
		    
			String testLine = br1.readLine();
			bw.write(testLine);
			while(testLine != null){
				if(testLine.toLowerCase().contentEquals("@data")) {
					testLine = br1.readLine();
					break;
				}else testLine = br1.readLine();
				bw.write(testLine+"\n");
			}
			
			while(testLine != null){			
				
				System.out.println("replacing: " + predictedLine.substring(0, Util.nthOccurrence(predictedLine, ',', numOfTargets)));
				System.out.println("To       : " + predictedLine.substring(Util.nthOccurrence(predictedLine, ',', numOfTargets)+1, Util.nthOccurrence(predictedLine, ',', numOfTargets*2)) );
				System.out.println("In       : " + testLine+ "\n------------------");
				
				String [] predictedValues = predictedLine.substring(Util.nthOccurrence(predictedLine, ',', numOfTargets)+1, Util.nthOccurrence(predictedLine, ',', numOfTargets*2)).split(",");
				String [] testLineValues = testLine.split(",");
				
				int predictedValuesIndex = 0;
				for(String t : targetAttr){
					int tar = Integer.parseInt(t.replaceAll("[^0-9]", ""));
					testLineValues[tar - 1] = predictedValues[predictedValuesIndex];
					predictedValuesIndex++;
				}
				
				StringBuilder replacedTestLine = new StringBuilder();
				for(String s : testLineValues){
					replacedTestLine.append(s+",");
				}
				replacedTestLine.deleteCharAt(replacedTestLine.length()-1);

				
				testLine = replacedTestLine.toString();

				bw.write(testLine+"\n");
				
				predictedLine = br0.readLine();
				testLine = br1.readLine();
			}
			
			fs.close();
			br.close();
			fs0.close();
			br0.close();
			fs1.close();
			br1.close();
			bw.close();
			
			try {
				printClassificationAccuracy("exp/"+folder+"/testCleaned.arff");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	public void printClassificationAccuracy(String dataInput) throws Exception{

		System.out.println("\nCalculate how many tuples were correctly classified...");

		DataSource scource = new DataSource(dataInput);
		Instances testCleaned = scource.getDataSet();
		
	
		System.out.println("\nThe following tuples were wrongly classified: ");
		int correctCount = 0;
		for(int j = 0; j < testCleaned.numInstances(); j++){
			if(origInstancesSet.contains(testCleaned.instance(j).toString())) correctCount++;
			else System.out.println(testCleaned.instance(j).toString());
		}
		float percent = (correctCount * 100.0f) / testCleaned.numInstances();
		System.out.println("\n"+percent+"%: "+ correctCount + " out of " + testCleaned.numInstances() + " correctly classified." );
	}
	
	
	
	
	public static void main(String[] args) throws Exception {
		if(args.length != 2){
			System.out.println("\nUsage: DTCleaner <input.arff> <FDinput.txt>");
			System.out.println("Eample: DTCleaner <data/hospital.arff> <FDlist.txt>");
			System.exit(1);
		}
		
		DTCleaner cleaner = new DTCleaner(args[0],args[1]);
		cleaner.seperateViolatedInstances();
		cleaner.makeModel();

		cleaner.replaceByPredictions();
		
	}

}

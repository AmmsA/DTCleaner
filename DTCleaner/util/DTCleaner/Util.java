package DTCleaner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
/**
 * Utility methods
 */
public class Util {

	/**
	 * Converts an ArrayList<Integer> to an int[] array.
	 * @param integers
	 * @return
	 */
	public static int[] convertIntegers(List<Integer> integers)
	{
	    int[] ret = new int[integers.size()];
	    for (int i=0; i < ret.length; i++)
	    {
	        ret[i] = integers.get(i).intValue();
	    }
	    return ret;
	}
	
	/**
	 * Saves the instances i to an arff file with name being filename
	 * Note: ".arff" should be included.
	 * 
	 * @param i
	 * @param filename
	 * @throws IOException 
	 */
	public static void saveArff(Instances i, String filename) throws IOException{
		System.out.println("\nSaving arff file: " + filename);
		
		ArffSaver saver = new ArffSaver();
		saver.setInstances(i);
		saver.setFile(new File(filename));
		saver.writeBatch();
	}
	
	/**
	 * 
	 * Merges a set of attributes into one and appends it to instance
	 * 
	 * @param instances i is where we want to append (@ end) the new attribute to.
	 * @param indexes of attributes to be merged
	 * @return the instance with the merged attribute appended at the beginning 
	 * @throws Exception
	 */
	public static Instances mergeAttributes(Instances i, int [] indexes) throws Exception{
		StringBuilder name = new StringBuilder();
		
		for(int index : indexes){
			name.append("_" + i.attribute(index).name());
		}
		
		// remove first "_"
		name.deleteCharAt(0);
		
		System.out.println("\nMergeing the attributes into new attribue: " + name.toString()+" ...");
		
		FastVector attValues = new FastVector();
		// holder is used to make sure we only add distinct values in our FastVector
		HashSet<String> holder = new HashSet<String>();
		
		// find all values of the nominal attribute
		for(int j = 0; j < i.numInstances(); j++){
			StringBuilder value = new StringBuilder();
			for(int index : indexes) value.append(" | " + i.instance(j).stringValue(index));
			value.delete(0,3);

			if(!holder.contains(value.toString())){
				attValues.addElement(value.toString());
				holder.add(value.toString());
			}
		}

		// create new nominal attribute
		Attribute newAttr = new Attribute(name.toString(), attValues);
		System.out.println(newAttr);
		
		// add attribute to the end of dataset
		i.insertAttributeAt(newAttr, i.numAttributes());
		
		// set values of the new attribute (that is now in i) accordingly 
		for(int j = 0; j < i.numInstances(); j++){
			StringBuilder value = new StringBuilder();
			for(int index : indexes) value.append(" | " + i.instance(j).stringValue(index));
			value.delete(0,3);
			
			i.instance(j).setValue(i.numAttributes()-1, value.toString());		
		}
		
		return i;
	}
	
	/**
	 * Returns the index of the nth occurance of char c in string str
	 * 
	 * @param str
	 * @param c
	 * @param n
	 * @return pos 
	 */
	public static int nthOccurrence(String str, char c, int n) {
	    int pos = str.indexOf(c, 0);
	    while (--n > 0 && pos != -1)
	        pos = str.indexOf(c, pos+1);
	    return pos;
	}
	
	/**
	 * 
	 * Removes the first and last chars from a string.
	 * 
	 * @param str input String
	 * @return str with first and last chars removed
	 */
	public static String removeFirstAndLastChars(String str){
		return str.substring(1, str.length() - 1);
	}
	
	/**
	 * Makes a setting file for Clus to run
	 * @param dataFile, name of training file
	 * @param testFile, name of test file
	 * @param attributesTarget, the id of targets
	 * @param treeHeuristic, type of heuristic
	 * @param location, of where to save the setting.s file
	 */
	public static void makeSettingFile(String dataFile, String testFile, Set<Integer> targets, HeuristicType treeHeuristic, String location){
		PrintWriter writer;
		try {
			writer = new PrintWriter(location+"/setting.s", "UTF-8");
			
			writer.println();
			writer.println("[Data]");
			writer.println("File = " + dataFile);
			writer.println("TestSet = " + testFile);
			
			writer.println();
			writer.println("[Output]");
			writer.println("WritePredictions = {Test}");
			

			StringBuilder targetsFormatted = new StringBuilder();
			for(int singleTarget : targets) targetsFormatted.append(singleTarget+"-");
			targetsFormatted.deleteCharAt(targetsFormatted.length()-1);
			writer.println();
			writer.println("[Attributes]");
			writer.println("Target = " + targetsFormatted.toString());
			
			writer.println();
			writer.println("[Tree]");
			writer.println("Heuristic = "+ treeHeuristic.toString());
			
			writer.println();
			
			writer.println("%TargetSize = " + targets.size());
			
			
			
			writer.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
}

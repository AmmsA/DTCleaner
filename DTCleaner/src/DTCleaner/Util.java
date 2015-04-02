package DTCleaner;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

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
	 * 
	 * @param i
	 * @param filename
	 * @throws IOException 
	 */
	public static void saveArff(Instances i, String filename) throws IOException{
		System.out.println("\nSaving arff file: " + filename + ".arff ...");
		
		ArffSaver saver = new ArffSaver();
		saver.setInstances(i);
		saver.setFile(new File(filename+".arff"));
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
}

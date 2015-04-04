package DTCleaner;

import java.util.HashMap;
import java.util.List;

import weka.core.Instances;


/**
 * Used to return two objects in returnViolatedTuples
 */
public class violatedTuples {
	public final Instances instances;
	public final HashMap<Integer, List<String>> tupleID;
	
	public violatedTuples(Instances instances, HashMap<Integer, List<String>> tupleID) {
		this.instances = instances;
		this.tupleID = tupleID;
	}
}

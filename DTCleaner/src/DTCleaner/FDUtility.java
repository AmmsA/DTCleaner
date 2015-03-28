package DTCleaner;
import weka.core.Instances;

/**
 * Functional Dependency Utilities
 * 
 */
public class FDUtility {

	public void addFD(Instances i){
		System.out.println("Add new FD. Usage: attribute#->attribute#,attribute# \n e.g. \"1->2,3\"\n");
		for(int j = 1; j <= i.numAttributes();j++) System.out.println(i.attribute(j).name());
	}

	
	public static void main(String[] args) {

	}

}

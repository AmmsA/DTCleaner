package DTCleaner;

import java.util.List;
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
}

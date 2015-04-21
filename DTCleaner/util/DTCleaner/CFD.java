package DTCleaner;

import java.util.LinkedList;
import java.util.AbstractMap.SimpleImmutableEntry;

/**
 * Conditional Functional Dependency Class
 */
public class CFD {
	
	private LinkedList<SimpleImmutableEntry<Integer,String>> premise;
	private SimpleImmutableEntry<Integer,String> RHS;
	
	/**
	 * CFD Constructor.
	 * @param premise
	 * @param RHS
	 */
	public CFD(LinkedList<SimpleImmutableEntry<Integer,String>> premise , SimpleImmutableEntry<Integer,String> RHS){
		this.premise = premise;
		this.RHS = RHS;
	}
	
	/**
	 * Return the premise of the CFD
	 * @return premise
	 */
	public LinkedList<SimpleImmutableEntry<Integer,String>> getPremise(){	return premise; } 
	
	/**
	 * Return the right hand side of the CFD rule as a SimpleImmutableEntry where
	 * key is the id of the attribute, and value is a value in the domain of table[key].
	 * @return SimpleImmutableEntry RHS.
	 */
	public SimpleImmutableEntry<Integer,String> getRHS(){	return RHS; } 
	
	/**
	 * Return a string description of the CFD.
	 * @return String str
	 */
	public String CFDToString(){
		StringBuilder str = new StringBuilder();
		for(SimpleImmutableEntry<Integer, String> lhs : premise){
			str.append(lhs.getKey() + "=" + lhs.getValue());
			str.append(",");
		}
		// delete last ","
		str.deleteCharAt(str.length()-1);
		str.append("->");
		str.append(RHS.getKey() + "=" + RHS.getValue());
		
		return str.toString();
	}
}

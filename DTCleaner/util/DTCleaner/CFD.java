package DTCleaner;

import java.util.LinkedList;
import java.util.AbstractMap.SimpleImmutableEntry;

public class CFD {
	private LinkedList<SimpleImmutableEntry<Integer,String>> premise;
	private SimpleImmutableEntry<Integer,String> rhs;
	public CFD(LinkedList<SimpleImmutableEntry<Integer,String>> premise , SimpleImmutableEntry<Integer,String> rhs){
		this.premise = premise;
		this.rhs = rhs;
	}
	
	public LinkedList<SimpleImmutableEntry<Integer,String>> getPremise(){	return premise; } 
	public SimpleImmutableEntry<Integer,String> getRHS(){	return rhs; } 
	
}

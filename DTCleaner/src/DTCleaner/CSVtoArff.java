package DTCleaner;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.*;

import java.io.File;
import java.util.Scanner;



public class CSVtoArff {

	/**
		Convert a CSV file to an Arff file.
		input: CSV file (comma separated)
		output: Arff file
	 */
	public static void main(String[] args) throws Exception {
		if(args.length != 2){
			System.out.println("\nUsage: CSVtoArff <input.csv> <output.arff>");
			System.out.println("Eample: CSVtoArff <data/hospital.csv> <hospital.arff>");
			System.exit(1);
		}
		
		System.out.println("loading file "+ args[0]);
		
		// load CSV file.
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(args[0]));
		Instances instances = loader.getDataSet();

		//Print Summary
		System.out.println("\nDataset summary:\n");
		System.out.println(instances.toSummaryString());
		
		//Changing type
		instances = changeType(instances);
		
		
		//Print dataset
		Scanner sc = new Scanner(System.in);
		System.out.println("\nDo you want to print the data to console (Y/N)?");
		String answer = sc.next();
		if(answer.toLowerCase().contentEquals("y")) System.out.println(instances);
		
		// Save Arff file.
		System.out.println("\nSaving Arff file...\n");
		ArffSaver saver = new ArffSaver();
		saver.setInstances(instances);
		saver.setFile(new File(args[1]));
		saver.writeBatch();
				
		sc.close();
		System.out.println("\nSaved.\n");

	}
	
	
	/**
	 * Changes type of attributes.
	 */
	private static Instances changeType(Instances i) throws Exception{
		Scanner sc = new Scanner(System.in);
		String answer = "";
		while(!answer.toLowerCase().contentEquals("n")){
			System.out.println("\nDo you want to change the type of some attributes? (Y/N)?");
			answer = sc.next();
			if(answer.toLowerCase().contentEquals("y")){
				System.out.println("Choose from list:\n" +
									"1- From numeric to nominal\n" +
								   	"2- From nominal to numeric");
				int choice = sc.nextInt();
				
				if(choice == 1){
					i = NumToNom(i);
				}else if(choice == 2){
					i = NomToNum(i);
				}else{
					System.out.println("Not a valid choice");
				}
			}
			
		}
		return i;
	}
	
	/**
	 * Converting attributes from Nominal to Numeric.
	 */
	private static Instances NomToNum(Instances i) throws Exception{
		// TODO 
		System.out.println("\n Conversion function isn't implented yet");
		System.out.println(i.toSummaryString());
		return i;
	}

	/**
	 * Converting attributes from Numeric to Nominal.
	 */
	private static Instances NumToNom(Instances i) throws Exception{
		System.out.println("Type range of variables to make nominal, e.g. \"1,3-4,7\"");
		Scanner sc = new Scanner(System.in);
		String range = sc.next();
		
		NumericToNominal convert = new NumericToNominal();
		String[] options = {"-R",range};
		
		convert.setOptions(options);
		convert.setInputFormat(i);
		
		System.out.println("\nConverting...\n");
		
		Instances newI = Filter.useFilter(i, convert);
		System.out.println(newI.toSummaryString());
		
		return newI;
	}

}

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class CSVtoArff {

	/**
		Convert a CSV file to an Arff file.
		input: CSV file (comma seperated)
		output: Arff file
	 * @throws Exception 
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
		loader.setSource(new File("data/hospital.csv"));
		Instances data = loader.getDataSet();
		
		// Save Arff file.
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);
		saver.setFile(new File(args[1]));
		saver.writeBatch();

		//Print Summary
		DataSource source = new DataSource(args[1]);
		Instances instances = source.getDataSet();
		System.out.println("\nDataset summary:\n");
		System.out.println(instances.toSummaryString());
		
		//Changing type
		instances = changeType(instances);
		
		
		//Print dataset
		Scanner sc = new Scanner(System.in);
		System.out.println("\nDo you want to print the data to console (Y/N)?");
		String answer = sc.next();
		if(answer.toLowerCase().contentEquals("y")) System.out.println(instances);
		
		
		
	}
	
	public static Instances changeType(Instances i) throws Exception{
		Scanner sc = new Scanner(System.in);
		String answer = "";
		while(!answer.toLowerCase().contentEquals("n")){
			System.out.println("\nDo you want to change the type of some attributes? (Y/N)?");
			answer = sc.next();
			if(answer.toLowerCase().contentEquals("y")){
				System.out.println("\nFrom numeric to nomianl? (Y/N)?");
				answer = sc.next();
				if(answer.toLowerCase().contentEquals("y")){
					System.out.println("Type range of variables to make nominal, e.g. \"1,3-4,7\"");
					String range = sc.next();
					
					NumericToNominal convert = new NumericToNominal();
					String[] options = {"-R",range};
					
					convert.setOptions(options);
					convert.setInputFormat(i);
					
					System.out.println("\nConverting...");
					
					Instances newI = Filter.useFilter(i, convert);
					i = newI;
					System.out.println(i.toSummaryString());

				}
			}
			
		}
		
		return i;
	}

}

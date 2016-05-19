
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

/*
 * Please replace ROOT with path to the folder with training and test data
 * */
public class Main {

	private static final String ROOT = "C:\\Users\\Eugene\\Documents\\PMP\\Machine Learning\\Lab3\\Task2\\"; 
	
    private static final String TEST_FILE_PATH = ROOT + "pima-indians-diabetes.test";
    private static final String TRAINING_FILE_PATH = ROOT  + "pima-indians-diabetes.train";
	
    private static final String ARFF_TEST_FILE_PATH = TEST_FILE_PATH + ".arff";
    private static final String ARFF_TRAINING_FILE_PATH = TRAINING_FILE_PATH + ".arff";
    
    private static int SamplingCount = 5; 
    private static Discretize filter = null;
	
    public static void main(String[] args) throws Exception {
        Instances trainingInstances = LoadFile(TRAINING_FILE_PATH, ARFF_TRAINING_FILE_PATH);
        
        System.out.println("Building Bagged ID3 from training data...");
        
        BaggedClassifier<ID3Chi> id3 = new BaggedClassifier(SamplingCount, ID3Chi.class);
        id3.buildClassifier(trainingInstances);
        
        System.out.println("Completed ID3 tree training");
        System.out.println(id3.toString());

        Instances testInstances = LoadFile(TEST_FILE_PATH, ARFF_TEST_FILE_PATH);
        
        System.out.println("Running ID3 on test data");

        int hit = 0;
        for(Enumeration<Instance> e = testInstances.enumerateInstances(); e.hasMoreElements();){
        	Instance i = e.nextElement();
        	double d = id3.classifyInstance(i);
        	String c = id3.convertClassValueToString(testInstances.classAttribute(), d);
        	if (d == i.classValue()) {
        		hit++;
        	}
        }
        System.out.println("Accuracy on test data is: " + (double)hit*100/testInstances.numInstances() + "%");
    }
    
    private static Instances LoadFile(String path, String arffPath) throws Exception {
    	
    	java.nio.file.Path fPath = java.nio.file.Paths.get(path);
    	java.nio.file.Path fArffPath = java.nio.file.Paths.get(arffPath);
    	
    	List<String> header = new ArrayList<String>();
    	String headerText = new Scanner(Main.class.getResourceAsStream("header.txt"), "UTF-8").useDelimiter("\\A").next();
    	header.add(headerText);
    	Files.write(fArffPath, header, Charset.defaultCharset(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    	
    	List<String> lines = Files.readAllLines(fPath);
    	Files.write(fArffPath, lines, Charset.defaultCharset(), StandardOpenOption.APPEND, StandardOpenOption.WRITE);    	

        ArffLoader arffTestLoader = new ArffLoader();

        File datasetFile = new File(arffPath);
        arffTestLoader.setFile(datasetFile);

        Instances attributes = arffTestLoader.getStructure();
        Instances dataInstances = arffTestLoader.getDataSet();
        
        for (Enumeration<Attribute> a = attributes.enumerateAttributes(); a.hasMoreElements();){
        	Attribute attr = a.nextElement();
        	System.out.println("Attribute: " + attr.index() + ": type: " + attr.type() + ": " + attr);
        }
       
        dataInstances.setClassIndex(dataInstances.numAttributes()-1);
        
        if (filter == null) {
	        filter = new Discretize();
	        filter.setInputFormat(dataInstances);
        }
        
        dataInstances = Filter.useFilter(dataInstances, filter);
        
        saveCopy(dataInstances, arffPath + "_2");
        
        return dataInstances;
    }
    
    protected static void saveCopy(Instances data, String path) throws Exception {
    	java.nio.file.Path fPath = java.nio.file.Paths.get(path);
    	List<String> text = new ArrayList<String>();
    	text.add(data.toString());
    	Files.write(fPath, text, Charset.defaultCharset(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }    
}

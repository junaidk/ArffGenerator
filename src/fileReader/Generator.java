package fileReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class Generator {

	
	public static void main(String[] args) throws IOException{
		
		// read log files
		Reader r = new Reader();
		
		String pathx = "/home/joni/Documents/Sproj/logs";
		String dir = "/home/joni/Documents/Sproj/";
		
		// petrinet reader
		ReadPnml rp = new ReadPnml(dir,"orig.pnml");
		rp.initTransitionsAndArcsMap();
		rp.addArcsToMap();
		
		
		
		String serviceName = "createInvoice+complete";
		String serviceNameL = "createInvoice";
		
		// from petrinet
		HashMap<String, List<Arc>> transitionsAndArcs = rp.getTransitionsAndArcs();	
		HashMap<String, String> IdToTransitionMap = rp.getIdToTransitionMapping();
		HashMap<String, String> transitionToIdMap = rp.getTransitionToIdMapping();
		
		
		
		// final hashmap
		HashMap<String, List<String>> m3 = new HashMap<String, List<String>>();
		HashMap<String, List<String>> nextServicesMap  = new HashMap<String, List<String>>(); 

		r.getVariablesAndNextServices(pathx, serviceNameL, m3 ,nextServicesMap);
		
		// var List to maintain order
		List<String> varList = r.getVariableList(m3);
		

		// data types 
		HashMap<String, String> varTypes = r.checkVariableType(m3);
		
		// default variable types
		HashMap<String, String> defaultVals = r.getDefalutVarValue(m3);
		
		//System.out.println("def vals");
		//System.out.println(defaultVals);
		//System.out.println(m3);
		

		
		FastVector      atts ;
	    FastVector      attVals = null;
	    Instances       data = null;
	    double[]        vals = null;

	    //## generating header
 	    // 1. set up attributes
	    atts = new FastVector();
		
	    
	    List<String> varValuesTemp;
	    HashMap<String, FastVector> varValue = new HashMap<>();
	    
	    for (String key : varTypes.keySet()){
	    	
	    	if (varTypes.get(key).equals("Nominal")){
	    		
		    	varValuesTemp = m3.get(key);
				
				//removing duplicates
				Set<String> uniqueSet = new HashSet<String>(varValuesTemp);		
				varValuesTemp = new ArrayList<>(uniqueSet);

			    attVals = new FastVector();
			    for (String val : varValuesTemp){
			    	val = val.substring(1, val.length()-1);
			    	//System.out.println("value : " + val);
			    	attVals.addElement(val);
			    }
			    
			    atts.addElement(new Attribute(key, attVals));
	    	}
	    	else //if (varTypes.get(key).equals("Numerical")){
	    	{	
	    		atts.addElement(new Attribute(key));
	    		
	    	}
		    
	    }
	    
	    //##  adding @attibute next service called
	    
/*	    List<Arc> tempArcList = transitionsAndArcs.get(transitionToIdMap.get(serviceName));
	    
	    //System.out.println("lol : " +transitionToIdMap.get(serviceName));
	    
	    //System.out.println("arc list size : " + tempArcList.size());
	    
	    for (Arc arc : tempArcList){
	    	
	    	//System.out.println("frm Arcs" + transitionsAndArcs.size());
	    	System.out.println( IdToTransitionMap.get( arc.target ));
	    	
	    }
	    */
	    
	    //System.out.println(nextServicesMap.get(serviceName.split("\\+")[0]));
	    
	    List<String> nextServicesx = nextServicesMap.get(serviceName.split("\\+")[0]);
	    
	    attVals = new FastVector();
	    for (String val : nextServicesx){
	    	//val = val.substring(1, val.length()-1);
	    	if (val != null){
	    		attVals.addElement(val);
	    	}
	    }
	    
	    atts.addElement(new Attribute("ServiceCalled_XYZ", attVals));
	    

		// 2. create Instances object
		data = new Instances(serviceNameL, atts, 0);
	
		//System.out.println(data);
		
		//adding data object
		File directory = new File(pathx);      
		File[] myarray;  
		myarray=directory.listFiles();
				
		//System.out.println(varList);
		
		String nextServieceInLog = null; 
		String arfData = ""; 
		String tempVarVal;
		for (int j = 0; j < myarray.length; j++)
		{
		       File fPath=myarray[j];
		       HashMap<String, List<String>> md = null;
		       
				try {
					// return hash map of input and outputs before that service
					md = r.readLogForVariabes(fPath, serviceNameL);
					nextServieceInLog =  r.giveNextCalledServices(fPath,serviceNameL );
					/*					
					if (md == null){
						System.out.println("Null on file : " + fPath);
					}
					*/

				} catch (Exception e) {
					//System.out.println("exp");
					e.printStackTrace();
				}

				//System.out.println(md);
				if (md != null){
					for (String var : varList){

						if (md.containsKey(var)){
							tempVarVal = md.get(var).get(0);
							arfData += tempVarVal.substring(1,tempVarVal.length()-1) + ",";	
						}
						else{
							
							arfData += defaultVals.get(var) + ",";
						}
				
					}
					
					arfData += nextServieceInLog + "\n";
				}
		}
	    
		//System.out.println(arfData);
		
		
		FileOutputStream fop = null;
		File file;
		String content = data + arfData;
 
		try {
 
			file = new File("/home/joni/Documents/Sproj/"+ serviceNameL+".arff");
			fop = new FileOutputStream(file);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			// get the content in bytes
			byte[] contentInBytes = content.getBytes();
 
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
 
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	    /*System.out.println("1 log file vars");
	    System.out.println(myarray[990]);
		System.out.println(r.readLogForVariabes(myarray[990], "isAuthorized"));
		
	    */
	
	
	  //System.out.println(r.checkVariableType(m3));
	  //System.out.println(data);
	
	
	}
}

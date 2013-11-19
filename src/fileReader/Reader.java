package fileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
public class Reader {

	
	/*
	public static void main(String[] args){
		
		
		String dir = "/home/joni/Documents/Sproj/logs/";
		
		String path = dir + "log_0";
		
		Reader r = new Reader();
/*		try {
			HashMap<String, List<String>>  varList = r.readFileForServiceName(path, "createInvoicex");

			if (varList != null){	
				System.out.println(varList);
			}
			else{
				
				System.out.println("Aloha null");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		*/
		
		/*
		String serviceName = "createInvoice";
		
		// final hashmap
		HashMap<String, List<String>> m3 = new HashMap<String, List<String>>();
		
		HashMap<String, List<String>> nextServicesMap  = new HashMap<String, List<String>>(); 
		
		List<String> nextServicesList = new ArrayList<String>();
		
		for (int i = 0 ; i < 50 ; i++){
			// temp map
			HashMap<String, List<String>> md = null;

			path = dir + "log_" + i;
						
			try {
				// return hash map of input and outputs before that service
				md = r.readFileForServiceName(path, serviceName);
				
				// return next service from the in put service
				nextServicesList.add( r.giveNextCalledServices(path, serviceName) );
				
			} catch (Exception e) {}

			if (md != null){
				r.mergeHashMaps(m3, md);
			}
		}
		
		
		//removing duplicates
		Set<String> uniqueSet = new HashSet<String>(nextServicesList);		
		List<String> x = new ArrayList<>(uniqueSet);
		nextServicesMap.put(serviceName, x);
		
		System.out.println(m3);
		System.out.println(nextServicesMap);
	}
	
	*/
	
	/*
	 * returns next called services
	 * arg : service name , file path
	 */
	
	public String giveNextCalledServices(String filePath,String serviceName ) throws IOException{
	
		//System.out.println(serviceName);
		
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(new File(filePath)));
			String line = null;

			while((line = br.readLine()) != null){
			
				String[] x = line.split("\\|\\|",9);
	
				
				if (x[1].equals(serviceName)){
					
					line = br.readLine();
					x = line.split("\\|\\|",9);
					
					br.close();
					return x[1];
				}
				
			}
			
			
		}catch(Exception e){}
		
		br.close();
		return null;
		
		
		
	}
	
	/*
	 * util functon 
	 * merge two hash maps
	 */
	
	public  void mergeHashMaps(HashMap<String, List<String>> m3 , HashMap<String, List<String>> md ){

		// merging hash maps
		if (m3 != null ){
			
			for(String key : md.keySet()) {
		
			    if(m3.containsKey(key)) {
			    	List<String> newList = new ArrayList<String>(m3.get(key));
			    	
			    	newList.addAll(md.get(key));
			    	
			    	m3.put(key,	newList);
			    } else {
			    	
			        m3.put(key,md.get(key));
			    }
			}
		}
		else{
			m3  = new HashMap<String, List<String>>(md);	
		}

	}
	
	/*
	 * return variable hashmap for a given service name
	 * returns HashMap<String, List<String>>
	 */
	
	public  HashMap<String, List<String>> readFileForServiceName(String filePath , String serviceName) throws IOException{
		
		
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(new File(filePath)));
			String line = null;
			
			List<String> lines = new ArrayList<String>();
			
			while((line = br.readLine()) != null){
				
				lines.add(line);
				
				String[] x = line.split("\\|\\|",9);

				
				if (x[1].equals(serviceName)){

					HashMap<String, List<String>> varList = getVarsFromLines(lines);
					
					br.close();
					return varList;

				}
				

				
			}
			
			
		}catch(Exception e){}
		
		br.close();
		// service not found in logfile -- return null
		return null;
	}
	
	/*
	 * return variable hashmap from all lines given
	 * returns HashMap<String, List<String>>
	 */
	
	public HashMap<String, List<String>> getVarsFromLines(List<String> lines){
		
		
		HashMap<String, List<String>> vars = new HashMap<>();
		String[] inputs;
		
		for (int j =0 ; j < lines.size(); j++ ){
			
			String line = lines.get(j);
			
			String[] x = line.split("\\|\\|",9);
			
			// input variables
			inputs = x[6].split(",");
		
			for(int i = 0; i < inputs.length ; i++ ){
				
				String[] keyVal = inputs[i].split("=");
				
				//
				if (keyVal.length > 1){
					
					// if variable already exist add value to value list 
					if ( vars.get(keyVal[0]) != null ){
						vars.get(keyVal[0]).add(keyVal[1]);												
					}
					else{ // creat a new array list and add value to it
						
						List<String> values = new ArrayList<String>();
						values.add(keyVal[1]);
						vars.put(keyVal[0], values);
							
					}

					//System.out.println(keyVal[0]+":"+keyVal[1]);		
				}
			}
			
		}
		
		// output variables
		
		for (int j =0 ; j < lines.size()-1; j++ ){
			
			String line = lines.get(j);
			
			String[] x = line.split("\\|\\|",9);
					inputs = x[7].split(",");
				
			for(int i = 0; i < inputs.length ; i++ ){
				
				String[] keyVal = inputs[i].split("=");
				
				//
				if (keyVal.length > 1){
					
					// if variable already exist add value to value list 
					if ( vars.get(keyVal[0]) != null ){
						vars.get(keyVal[0]).add(keyVal[1]);												
					}
					else{ // creat a new array list and add value to it
						
						List<String> values = new ArrayList<String>();
						values.add(keyVal[1]);
						vars.put(keyVal[0], values);
							
					}

					//System.out.println(keyVal[0]+":"+keyVal[1]);		
				}
			}
		}
		
		//System.out.println(vars.size());
		
		return vars;
	}
	
}

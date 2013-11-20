package salman;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;


public class LogFileParser {

	static WSLDParser parser = new WSLDParser("servicing.xml");
	static WekaInputsGenerator generator = null;
	
	public static void main(String[] args){
		
		LogFileParser lp = new LogFileParser();
		HashMap<String, ArrayList<String>> map= lp.logfileParser("C:\\Users\\admin\\Desktop\\logfiles1");
		//HashMap<String, String> allVariables = lp.getMaxVariablesTill("calculateCustomDuty", map,1);
		//System.out.println("allVariables :" + allVariables.size());
	//	FileWriter f = null;// = new File("C:\\Users\\admin\\Desktop\\weka\\vars.csv");
		FileWriter fWr = null;
		try {
			fWr = new FileWriter("C:\\Users\\admin\\Desktop\\weka\\weka.csv");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		BufferedWriter bufferWriter = null;
		BufferedWriter bufferWriterWeka = null;
		/*try {
			bufferWriterWeka = new BufferedWriter( fWr);
	
			generator = new WekaInputsGenerator(allVariables);
			generator.createInputsFile("rabee", bufferWriterWeka);
			
			generator.writeVariablesFromLogfile(allVariables, map.get("sequence_0"), bufferWriterWeka, "Servicing.xml");
			generator.writeVariablesFromLogfile(allVariables, map.get("sequence_4966"), bufferWriterWeka, "Servicing.xml");

			bufferWriterWeka.close();
			fWr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
*/		
				
	}
	
	public void writeColumnNames(HashMap<String, String> allVariables, BufferedWriter writer){
		StringTokenizer tokenizer = null;
		for(String key : allVariables.keySet()){
			try {
		//		tokenizer = new StringTokenizer(key, "||");
			//	key = tokenizer.nextToken();
				writer.write(key.substring(0, key.length()) + ",");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			writer.write("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void writeVariablesFromLogfile(HashMap<String, String> allVariables, ArrayList<String> logfile, BufferedWriter writer){
		
		String newline = System.getProperty("line.separator");
		
		HashMap<String, String> tempVar = null;
		HashMap<String, String> allVarInLog = new HashMap<String, String>();
	
		for(String line : logfile){
			tempVar = getVariablesFromLine(line);
			
			for(String key : tempVar.keySet()){
				allVarInLog.put(key, tempVar.get(key));
			}
			
		}
		
		
		String value = null;
		
		for(String temp : allVariables.keySet()){
			if(allVarInLog.get(temp) == null){
				value = parser.getVariableType(temp);
				if(value.equals("string")){
					value = "null";
				}else if(value.equals("numeric")){
					value = "" + Integer.MIN_VALUE;
				}else if (value.equals("nominal")){
					value = "" + Integer.MIN_VALUE;
				}else if(value.equals("{TRUE, FALSE}")){
					value = "FALSE";
				}
			}else{
				value = allVarInLog.get(temp);
			}
			try {
			//	System.out.println("writing " + value);
				
				writer.write(value +",");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			writer.write(newline);
		
	//		write.write(" this is new line ");
			//writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public HashMap<String, ArrayList<String>> logfileParser(String path){

		HashMap <String, ArrayList<String>> filesMap = new HashMap<String, ArrayList<String>>();
		
		List<File> results = new ArrayList<File>();
		
		
		
		File[] files = new File(path).listFiles();
		BufferedReader br = null;
		for(File file: files){
			if(file.isFile()){
				try {

					ArrayList<String> temp = new ArrayList<String>();
					br = new BufferedReader(new FileReader(file));
					String line;
					while((line = br.readLine()) != null){
						temp.add(line);
						//	System.out.println("here");
					}
					filesMap.put(file.getName(), temp);
					br.close();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}
		}


		return filesMap;
	}

	public HashMap<String, String> getMaxVariablesTill(String urlCat, HashMap<String, ArrayList<String>> map, int depth, ArrayList<ArrayList<String>> branches){

		HashMap<String, String> lineVariables;
		HashMap<String, String> logfileVariables = new HashMap<String, String>();
		
		
		
		for(String key : map.keySet()){
		
			int i = 0;
			ArrayList<String> temp = map.get(key);
			ArrayList<Integer> index = indexOfServiceCalled(depth, temp, urlCat, branches);
			int start;
			int end;
			
			
			
			
			
			for(String line : temp){
				if(index.get(0) == -1){
					break;
				}else{
					start = index.get(0);
					if(start != 0) start++;
					end = index.get(1);
				}
				
		
				if(i< start || i > end){ 
					i++;
					continue;
				}
				i++;
				//if(i> end) continue;
				String type = null;
				System.out.println("here2");
				lineVariables = getVariablesFromLine(line);
				System.out.println("size lineVar: " + lineVariables.size());
				for( String parameter: lineVariables.keySet() ){
					
					if( logfileVariables.get( parameter ) == null ){
						
						logfileVariables.put(parameter, parser.getVariableType(parameter));
					}
				}	
			}
		}
		return logfileVariables;
	}
	
	public ArrayList<Integer> indexOfServiceCalled(int depth, ArrayList<String> logfile, String srcCat, ArrayList<ArrayList<String>> branches){

		boolean found = false;
		int startDepth = 0;
		int endDepth = 0;
		int currentDepth = 0;
		
		int currentIndex = 0;
		int startIndex = 0;
		int endIndex = 0;
		ArrayList<Integer> branchInfo = null;
		for(String temp: logfile){
			if(temp.contains(srcCat)){
				currentDepth++;
				if(currentDepth + 1 == depth){
						startIndex = currentIndex;
					}
			}
			
			
			if(currentDepth == depth){
			
				endDepth = currentDepth;
				endIndex = currentIndex;
				
				branchInfo = getExecutedBranch(endIndex + 1, logfile, branches);
				if( branchInfo != null ){
					endIndex = endIndex + branchInfo.get(1);
				}
				found = true;
				break;
			}
			currentIndex++;
			
		}
		ArrayList<Integer> index = new ArrayList<Integer>();
		
		if(found == true){
			index.add(startIndex);
			index.add(endIndex);
			if( branchInfo != null ){
				index.add( branchInfo.get(0) );
				
			}else{
				index.add(-1);
			}
		}else{
			index.add(-1);
			index.add(-1);
			index.add(-1);
		}
		return index;
	}

	public static HashMap<String, String> getVariablesFromLine(String line){

		HashMap<String, String> map = new HashMap<String, String>();
		ArrayList<String> inputList = new ArrayList<String>();
		ArrayList<String> returnList = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(line, "||");
		
		ArrayList<String> tokens = new ArrayList<String>();

		while(tokenizer.hasMoreTokens()){
			tokens.add(tokenizer.nextToken());
		}

		String inputs = null;
		String output = null;

		if(line.substring(line.length()-4, line.length()).equals("||||")){
			inputs = tokens.get(tokens.size()-1);
		}else{
			inputs = tokens.get(tokens.size()-2);
			output = tokens.get(tokens.size()-1);

		}

		tokenizer = new StringTokenizer(inputs, ",");

		String temp;
		while(tokenizer.hasMoreTokens()){
			temp = tokenizer.nextToken();

			int position = temp.indexOf("=");
			String tempVariable = temp.substring(0,position);
			temp = temp.substring(position+1, temp.length() - 1);
			temp = temp.substring(1, temp.length() );
	//		tempVariable = tokens.get(1)+"||"+tempVariable;
			tempVariable = tokens.get(1)+"||"+tempVariable;
			map.put(tempVariable, temp);
		}
		

		if(output != null){
			tokenizer = new StringTokenizer(output, "=");
			String appended = tokens.get(1);
			while(tokenizer.hasMoreTokens()){
				output = tokenizer.nextToken();
				output= tokenizer.nextToken();
				output = output.substring(1, output.length()-1);
				map.put(appended+"||return", output);
				
			}
		}
	
		return map;
	}

	public ArrayList<Integer> getExecutedBranch(int logIteratorIndex, ArrayList<String> logfile, ArrayList<ArrayList<String>> branches){
		
		int counter=0, branchIndex = -1, size= -1;
		ArrayList<String> branch;
		for(int j=0; j < branches.size(); j++){
			counter = 0;
			branch =  branches.get(j);
			for(int i = logIteratorIndex; ((i < logIteratorIndex + branch.size()) && i < logfile.size() ); i++){
				
				if(logfile.get(i).contains(branch.get(i-logIteratorIndex))){
					
					counter++;
					
				}
				
			}
			
			if( counter == branch.size() && size < branch.size() ){
				branchIndex = j;
				size = branch.size();
			}
		}
		
		ArrayList<Integer> result;
		if( size > 0 ){
			result = new ArrayList<Integer>();
			result.add(branchIndex);
			result.add(size);
			return result;
		}else{
			return null;
		}
		
	}
}

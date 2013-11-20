package salman;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;


public class WekaInputsGenerator {



	HashMap<String, String> allVariables;
	String name;
	static WSLDParser parser;
	public WekaInputsGenerator(HashMap <String, String> allVariables){
		this.allVariables = allVariables;
		this.name = name;
		
	
	}	
	
	public void writeVariablesFromLogfile(HashMap<String, String> allVariables, ArrayList<String> logfile, BufferedWriter writer, String wsdlName){
	
		parser = new WSLDParser(wsdlName);
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


	public void createInputsFile(String name, BufferedWriter writer){

		HashMap<String, String> map = new HashMap<String, String>();
		int i = 0;
		System.out.println(allVariables.size());
		try {
			writer.write("@relation " + name +"\n\n");

			for(String temp: allVariables.keySet()){
				i++;
				writer.write("@attribute " + temp + " ");

				if(allVariables.get(temp).equals("nominal")){
					writer.write("{TRUE, FALSE}");
				}else{
					writer.write(allVariables.get(temp));
				}

				writer.write("\n");
			}
			
			writer.write("\n");
			writer.write("@data\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
	
	public void addServicesAttribute(ArrayList<ArrayList<String>> branches, BufferedWriter writer){
		
		String toAdd = "{";
		
		for(ArrayList<String> branch : branches){
			for(String service : branch){
				toAdd += service + "&";
			}
		

			toAdd = toAdd.substring(0, toAdd.length()-1);
			toAdd += ", ";
		}
		toAdd = toAdd.substring(0, toAdd.length() -1) + "}";
		
		try {
			writer.write(toAdd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public String getServicesValue(int index, ArrayList<ArrayList<String>> branches){
		
		if(index == -1){
			return null;
		}
		
		ArrayList<String> servicesCalled = branches.get(index);
		String servicesConcatenated = "";
		
		if(servicesCalled == null){
			return null;
		}
				
		for(String tempService : servicesCalled){
			servicesConcatenated +=  tempService+"&";
		}
		
		return servicesConcatenated.substring(0, servicesConcatenated.length()-1);
		
		
	}

	public void writeValues(BufferedWriter writer, ArrayList<String> logfile, BufferedWriter write, WSLDParser parser){

		HashMap<String, String> tempVar = null;
		HashMap<String, String> allVarInLog = new HashMap<String, String>();

		try {
			writer.write("\n");
			writer.write("@data");
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
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
				writer.write(value+",");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		


	}

	public static HashMap<String, String> getVariablesFromLine(String line){

		//				System.out.println("********************");
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
		//	System.out.println(line.substring(line.length()-4, line.length()));
		if(line.substring(line.length()-4, line.length()).equals("||||")){
			inputs = tokens.get(tokens.size()-1);
		}else{
			inputs = tokens.get(tokens.size()-2);
			output = tokens.get(tokens.size()-1);

		}

		//System.out.println("INPUTS: " + inputs);
		tokenizer = new StringTokenizer(inputs, ",");

		String temp;
		//	System.out.println("tokens: " + tokenizer.countTokens());
		while(tokenizer.hasMoreTokens()){
			temp = tokenizer.nextToken();
			//					System.out.println("this is temp: `" + temp);

			int position = temp.indexOf("=");
			//	System.out.println(position);
			String tempVariable = temp.substring(0,position);
			//		System.out.print(tempVariable + ": ");
			temp = temp.substring(position+1, temp.length() - 1);
			temp = temp.substring(1, temp.length() );
			//		System.out.println("TEMP "+temp);
			//					tempVariable = tokens.get(1)+"||"+tokens.get(2)+"||"+tempVariable;
			tempVariable = tokens.get(1)+"||"+tempVariable;
			map.put(tempVariable, temp);
		}


		if(output != null){
			tokenizer = new StringTokenizer(output, "=");
			//					String appended = tokens.get(0) + "||"  + tokens.get(2) + "||";
			String appended = tokens.get(1);
			//		System.out.println(appended);
			while(tokenizer.hasMoreTokens()){
				output = tokenizer.nextToken();
				output= tokenizer.nextToken();
				output = output.substring(1, output.length()-1);
				//			System.out.println("return = " + output);
				map.put(appended+"||return", output);

			}
		}

		return map;
	}


}

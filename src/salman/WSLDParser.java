package salman;


import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class WSLDParser {
	Hashtable<String, String> types= null;
	
	public static void main(String[] argv){
		Hashtable<String, String>  table;//= new Hashtable<String, String>();

		//test();
		table = extractVariableVsType("Servicing.xml");
		Scanner scan = new Scanner(System.in);

		System.out.println(table.size());
		while(true){
			System.out.println("enter name: ");
			String name = scan.nextLine();
			System.out.println(table.get(name));
		}
	}
	
	public WSLDParser(String path){
	
		types = extractVariableVsType(path);
		
	}
	
	public String getVariableType(String variable){
		
		String type = null;
		if(variable.contains("return")){
			StringTokenizer tokenizer = new StringTokenizer(variable, "||");
			variable = tokenizer.nextToken() + "Response||" +	tokenizer.nextToken();
		}

		System.out.println("The variable is: " + variable);
		type = types.get(variable);
		return type;
		
	}

	public static Hashtable<String, String> extractVariableVsType(String path){

		Hashtable<String, String> variables = new Hashtable<String, String>();
		Hashtable<String,Integer> ports = new Hashtable<String,Integer>();
		String elementName = null;

		File xmlFile = null;
		DocumentBuilderFactory dbFact = null;
		DocumentBuilder dBuilder = null;
		Document doc = null;

		try{
			xmlFile = new File(path);
			dbFact = DocumentBuilderFactory.newInstance();
			dBuilder = dbFact.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

		}catch(Exception e){
			e.printStackTrace();
		}
		NodeList nList = doc.getElementsByTagName("*");


		//	NodeList nList = doc.getElementsByTagName("xs:element");
		for(int i = 0; i < nList.getLength(); i++){
			Node node = nList.item(i);
			elementName = node.getNodeName();
			//				System.out.println(name);
			if (elementName.contains("element")){
				break;
			}	

		}
		System.out.println(elementName);
		String nameSpace = elementName.replace("element", ""); 

		System.out.println(nameSpace);

		nList = doc.getElementsByTagName(elementName);
		System.out.println("length list: " + nList.getLength());
		int k =0;
		for(int i = 0 ; i < nList.getLength(); i++ ){
			Node nNode = nList.item(i);
			if(nNode.getNodeType()==Node.ELEMENT_NODE){
				//	System.out.println("here");
				Element element = (Element) nNode;
				String name = element.getAttribute("name");


				String type = element.getAttribute("type");
				if(type.equals(nameSpace+"string")){
					//			System.out.println("string");
					type="string";
				}else if(type.equals(nameSpace+"int")){
					//		System.out.println("int");
					type="numeric";
				}else if(type.equals(nameSpace+"double")){
					type = "numeric";
				}else if(type.equals(nameSpace+"boolean")){
					type = "{TRUE, FALSE}";
				}
				else{
					//		System.out.println("nominal");
					type="nominal";
				}
				System.out.println("type: " + type);
				//	variables.put(name,type);
			//	if(name.equals("return")){
				//	System.out.println("not here");
					if(nNode.getParentNode().getParentNode().getParentNode().getNodeType() == nNode.ELEMENT_NODE && nNode.hasAttributes()){

						Element port = (Element) nNode.getParentNode().getParentNode().getParentNode();
						
						String portName = port.getAttribute("name");
						if(name.equals("return")){
							variables.put(portName+"||return", type);
							System.out.println(portName+"||return");
						}else{
							variables.put(portName + "||" + name, type);
							System.out.println(portName + "||" + name);
						}
						ports.remove(portName);
						ports.put(portName, 1);
					}else if(!type.equals("")){
			//			System.out.println("here");
						variables.put(name, type);
					}
				//}else{
				//	variables.put(name,type);
				//}
			}

		}
		Enumeration<String> e = ports.keys();
		while(e.hasMoreElements()){
			String key = e.nextElement();
			int value = ports.get(key);
			if( value==0 ){
				variables.put(key+"||return", null);
			}
		}

		return variables;

	}
}



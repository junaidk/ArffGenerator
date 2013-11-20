package fileReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class ReadPnml {

	public static NodeList t ;
	public static NodeList p ;
	public static NodeList a ;
	
	private HashMap<String, List<Arc>> transitionsAndArcs ;

	public  ReadPnml(String dirPath, String fileName ) {
		
		//String dir = "/home/joni/Documents/Sproj/";	
		String path = dirPath + fileName;
		Document d = readFileToDocument(path);
		t = d.getElementsByTagName("transition");
		p = d.getElementsByTagName("place");
		a = d.getElementsByTagName("arc");
		
		transitionsAndArcs = new HashMap<>();
		
	}
	
	public HashMap<String, List<Arc>> getTransitionsAndArcs(){
		
		return this.transitionsAndArcs;
	}
	
/*	
	public static void main(String[] args){
		
		
		String dir = "/home/joni/Documents/Sproj/";
		
		String path = dir + "orig.pnml";
		
		ReadPnml rp = new ReadPnml(dir,"orig.pnml");
*/		
//		Document d = rp.readFileToDocument(path);
		
//		t = d.getElementsByTagName("transition");
//		p = d.getElementsByTagName("place");
//		a = d.getElementsByTagName("arc");
		
		
		//System.out.println((t.item(1).getAttributes()).item(0));
		
		// transitions and Arcs
/*
		HashMap<String, List<Arc>> transitionsAndArcs = new HashMap<>();
		
		
		// adding transitions to hashmap
		String temp;
		String transitionId ;
		for (int i=0;i<t.getLength();i++){
			temp = (t.item(i).getAttributes()).item(0).toString();

			transitionId = temp.split("=")[1];
			List<Arc> dx = new ArrayList<>();
			
			transitionsAndArcs.put(transitionId, dx);
			
		}
		
		//System.out.println(transitionsAndArcs);
		
		// adding first arcs
		String src,dest;
		for (int i=0;i<a.getLength();i++){
			temp = (a.item(i).getAttributes()).item(0).toString().split("=")[1];
			src = (a.item(i).getAttributes()).item(1).toString().split("=")[1];
			dest = (a.item(i).getAttributes()).item(2).toString().split("=")[1];
			//System.out.println(temp + src + dest);
			
			Arc ax = new Arc();
			ax.arcId = temp;
			ax.source = src;
			ax.target = dest;
			ax.isOutFromTransition = true;
			
			//System.out.println(src);
			if (transitionsAndArcs.get(src) != null){
				transitionsAndArcs.get(src).add(ax);
			}
		
		}
		
*/		// adding remaining arcs
/*
		List<Arc> arcsWithTransitionAsSrc ;
		
		List<Arc> backArcs;
		
		for ( String transition : transitionsAndArcs.keySet()){
			arcsWithTransitionAsSrc = transitionsAndArcs.get(transition);	// starting arcs
			backArcs = new ArrayList<Arc>();
			for ( Arc arc : arcsWithTransitionAsSrc  ){
				
				//System.out.println("Starting arcs for "+transition +": "+arc.arcId);
				// arc.target
				backArcs.addAll( rp.getArcsForSrc( arc.target  ));
			}
			transitionsAndArcs.put(transition, backArcs);
		}
		
*/		
/*
		rp.initTransitionsAndArcsMap();
		rp.addArcsToMap();
		
		System.out.println(transitionsAndArcs);
		// testing
		List<Arc> tempx;
		for (String transition : transitionsAndArcs.keySet()){
			
			tempx = transitionsAndArcs.get(transition);
			System.out.println("transition : "+transition);
			
			for( Arc tempArc : tempx){
				
				
				//System.out.println("Tar   : "+ tempArc.target);
				System.out.println("is true : "+ tempArc.isOutFromTransition);

				
			}
			
		}
		
		System.out.println(rp.getIdToTransitionMapping());

				
	}
*/	
	// initialize transitions and arcs hash map with all transitions 
	// and startig outgoing arcs
	public void initTransitionsAndArcsMap(){
		
		
		// adding transitions to hashmap
		String temp;
		String transitionId ;
		for (int i=0;i<t.getLength();i++){
			temp = (t.item(i).getAttributes()).item(0).toString();

			transitionId = temp.split("=")[1];
			List<Arc> dx = new ArrayList<>();
			
			transitionsAndArcs.put(transitionId, dx);
			
		}
		
		//System.out.println(transitionsAndArcs);
		
		// adding first arcs
		String src,dest;
		for (int i=0;i<a.getLength();i++){
			temp = (a.item(i).getAttributes()).item(0).toString().split("=")[1];
			src = (a.item(i).getAttributes()).item(1).toString().split("=")[1];
			dest = (a.item(i).getAttributes()).item(2).toString().split("=")[1];
			//System.out.println(temp + src + dest);
			
			Arc ax = new Arc();
			ax.arcId = temp;
			ax.source = src;
			ax.target = dest;
			ax.isOutFromTransition = true;
			
			//System.out.println(src);
			if (transitionsAndArcs.get(src) != null){
				transitionsAndArcs.get(src).add(ax);
			}
		
		}
		
	}
	
	// adding all the arcs that go from given transition to next immidiate 
	// transitions
	public void addArcsToMap(){
		
		List<Arc> arcsWithTransitionAsSrc ;
		
		List<Arc> backArcs;
		
		for ( String transition : transitionsAndArcs.keySet()){
			arcsWithTransitionAsSrc = transitionsAndArcs.get(transition);	// starting arcs
			backArcs = new ArrayList<Arc>();
			for ( Arc arc : arcsWithTransitionAsSrc  ){
				
				//System.out.println("Starting arcs for "+transition +": "+arc.arcId);
				// arc.target
				backArcs.addAll(getArcsForSrc( arc.target  ));
			}
			transitionsAndArcs.put(transition, backArcs);
		}

		
	}
	// get transition to transition id mapping
	public HashMap<String, String> getIdToTransitionMapping(){
		
		HashMap<String, String > idToNameMap = new HashMap<>();
		String transitionId,transitionName;
		
		for (int i=0 ; i < t.getLength(); i++){
			
			transitionId = (t.item(i).getAttributes()).item(0).toString().split("=")[1];
			transitionName = t.item(i).getChildNodes().item(0).getChildNodes().item(0).getTextContent().toString();
			
			idToNameMap.put(transitionId, transitionName);
			
		}
		
		
		return idToNameMap;
	}
	
	// get transition id to transition mapping
		public HashMap<String, String> getTransitionToIdMapping(){
			
			HashMap<String, String > idToNameMap = new HashMap<>();
			String transitionId,transitionName;
			
			for (int i=0 ; i < t.getLength(); i++){
				
				transitionId = (t.item(i).getAttributes()).item(0).toString().split("=")[1];
				transitionName = t.item(i).getChildNodes().item(0).getChildNodes().item(0).getTextContent().toString();
				
				idToNameMap.put(transitionName,transitionId);
				
			}
			
			
			return idToNameMap;
		}
		
	// gives arc where given place is starting point
	// arg : placeID
	public List<Arc> getArcsForSrc(String srcID){
		
		List<Arc> arcsForGivenSrc= new ArrayList<Arc>();
		
		String temp,src,dest;
		
		for (int i=0;i<a.getLength();i++){
			
			temp = (a.item(i).getAttributes()).item(0).toString().split("=")[1]; // id
			src  = (a.item(i).getAttributes()).item(1).toString().split("=")[1];	
			dest  = (a.item(i).getAttributes()).item(2).toString().split("=")[1];
			
/*			if (srcID.equals(src)){
				
				System.out.println(srcID +" found");
			}*/
			
			if (srcID.equals(src)){
				
				Arc ax = new Arc();
				ax.arcId = temp;
				ax.source = src;
				ax.target = dest;
				ax.isOutFromTransition = false;

				arcsForGivenSrc.add(ax);
				
				
			}
			
		}
		
		//System.out.println("return arc size : " + arcsForGivenSrc.size());
		return arcsForGivenSrc;
	}
	

	/*
	 * xml documnet reder
	 */
	public Document readFileToDocument(String filePath){
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}

class Arc{
	
	public String arcId;
	public String source;
	public String target;
	public boolean isOutFromTransition;
	
}
class Transition{
	
	public String transitionId;
	public String transitionName;
	
}


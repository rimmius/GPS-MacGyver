package macgyver;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFileChooser;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Node;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

public class OsmParser implements Runnable{
	private int nodeCount;
	private int wayCount;
	public String filename;
	public static ArrayList<Node> nodes = new ArrayList<Node>();
	public static ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	public static HashMap<Long, Integer> hashMap = new HashMap<Long, Integer>();
	
	JMapViewer map;
	/**
	 * Author: Fredrik Gustafsson
	 * @param filename
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public OsmParser(String filename) throws SAXException, IOException{
	    this.filename = filename;
	    InputSource inputSource = new InputSource(new FileReader(filename));
	    parse(inputSource);
	}
	public OsmParser(){}
	/**
     * Author: Fredrik Gustafsson
     * Thread
     */
	public void run(){
	    try {
            InputSource inputSource = new InputSource(new FileReader(filename));
            try {
                parse(inputSource);
            } catch (SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
	}
	
	public static void main(String[] args) throws SAXException, IOException
	
	{
//	    File f;
//	    JFileChooser jfc = new JFileChooser();
//        jfc.showOpenDialog(null);
//        f = new File(jfc.getSelectedFile().getPath());
//        
//		filename = f.toString();
//		System.out.println(filename);
//		InputSource inputSource = new InputSource(new FileReader(filename));
//		OsmParser osmParser = new OsmParser();
//		osmParser.parse(inputSource);
	}
	public String getPath(){
	    return filename;
	}
	public void parse(InputSource inputSource) throws SAXException, IOException
	{
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		OsmSaxParser parser = new OsmSaxParser();
		xmlReader.setContentHandler(parser);
		xmlReader.parse(inputSource);
	}
	/**
	 * Authors: Palina Kolas, Fredrik Gustafsson
	 * @param id
	 * @param lat
	 * @param lon
	 * @param tags
	 */
	protected void onNode(
			String id,
			float lat, 
			float lon,
			Map<String,String> tags){
		nodeCount++;
		vertices.add(new Vertex(id, lat, lon));
		long tempLong = Long.parseLong(id);
		hashMap.put(tempLong, vertices.size()-1);
	}
	/**
	 * Authors: Fredrik Gustafsson, Palina Kolas
	 * @param id
	 * @param nodeIdList
	 * @param tags
	 */
	protected void onWay(
			String id,
			List<String> nodeIdList,
			Map<String, String> tags)
	{
		wayCount++;
		addPath(nodeIdList, tags);
	}
	/**
	 * Author: Fredrik Gustafsson
	 * @param nodeIdList
	 * @param tags
	 */
	public void addPath(List<String> nodeIdList, Map<String, String> tags){
	    for (int j = 0; j < nodeIdList.size(); j++){
	        if (j == nodeIdList.size()-1)
	            break;
//    	    Vertex number1 = bSearch(nodeIdList.get(j), 0, vertices.size()-1);
//    	    Vertex number2 = bSearch(nodeIdList.get(j+1), 0, vertices.size()-1);
	        if (tags.containsKey("highway")){
	        int number1Int = hashMap.get(Long.parseLong(nodeIdList.get(j)));
	        Vertex number1 = vertices.get(number1Int);
	        int number2Int = hashMap.get(Long.parseLong(nodeIdList.get(j+1)));
            Vertex number2 = vertices.get(number2Int);
    	    number1.tags = tags;
    	    double distance = getDistance(number1.lat, number1.lon, number2.lat, number2.lon);
    	    Edge temp = new Edge(number2, distance, tags);
            number1.adjacencies.add(temp);
    	    if (!temp.tags.containsValue("oneway"))
    	        number2.adjacencies.add(new Edge(number1, distance, tags));
	        }
	    }
	    
	}
	/**
	 * Author: Fredrik Gustafsson
	 * 
	 * binary searches the list of vertices.
	 * @param name : name of vertex
	 * @param min : startpos of search
	 * @param max : endpos of search
	 * @return Vertex 
	 */
	public Vertex bSearch(String name, int min, int max){
        int mid;

        while(min <= max){
            mid = min + ( max - min ) / 2;
            if ((max - min) < 2){
                if (vertices.get(max).name.equals(name))
                    return vertices.get(max);
                else if (vertices.get(min).name.equals(name))
                    return vertices.get(min);
            }
            long nr1 = Long.parseLong(vertices.get(mid).name);
            long nr2 = Long.parseLong(name);
            if(nr1 > nr2)
                max = mid - 1;
            else if(nr1 < nr2)
                min = mid + 1;
            else{
               
                return vertices.get(mid);
                
            }
        }
        return null;
	}
	/**
	 * Author: Fredrik Gustafsson
	 * Calculating distance between two vertex with haversines formula
	 * 
	 * @param lat1 : latitude of first vertex
	 * @param lon1 : longitide of first vertex
	 * @param lat2 : latitude of second vertex
	 * @param lon2 : longitude of second vertex
	 * @return double value of distance
	 */
	public double getDistance(double lat1, double lon1, double lat2, double lon2){

        int nRadius = 6371; 

        double nDLat = (lat2 - lat1) * (Math.PI/180);
        double nDLon = (lon2 - lon1) * (Math.PI/180);
        double nA = Math.pow(Math.sin(nDLat/2), 2 ) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(nDLon/2), 2);
     
        double nC = 2 * Math.atan2( Math.sqrt(nA), Math.sqrt( 1 - nA ));
        double nD = nRadius * nC;
        return nD;
	}
	
		class OsmSaxParser
		extends DefaultHandler2
	{
		private static final int ELEMENTTYPE_NODE = 1;
		private static final int ELEMENTTYPE_WAY = 2;
		private static final int ELEMENTTYPE_NODEREF = 3;
		private static final int ELEMENTTYPE_TAG = 4;
		
		private final Map<String, Integer> elementTypeMap = 
			new HashMap<String, Integer>() {{
				put("node",ELEMENTTYPE_NODE);
				put("way",ELEMENTTYPE_WAY);
				put("nd",ELEMENTTYPE_NODEREF);
				put("tag",ELEMENTTYPE_TAG);
			}};
			
		private String currentWayId;
		private Map<String, String> currentTags;
		private List<String> currentNodeIdList;
		private String currentType;
		private NodeData currentNode;
		
		@Override
		public void endElement(String uri, String localName, String name)
				throws SAXException
		{
			Integer elementType = elementTypeMap.get(name);
			if (elementType==null) return;
			
			switch (elementType) {
			case ELEMENTTYPE_NODE: currentType = null; break;
			case ELEMENTTYPE_WAY: currentType = null; break;
			}
			
			switch (elementType) {
			case ELEMENTTYPE_WAY: 
				onWay(currentWayId, currentNodeIdList, currentTags);
				currentNodeIdList = null;
				currentTags = null;
			case ELEMENTTYPE_NODE:
				onNode(currentNode.id, 
						currentNode.lat, 
						currentNode.lon, 
						currentTags);
				currentTags = null;
			}
		}

		@Override
		public void startElement(
				String uri, 
				String localName, 
				String name,
				Attributes attributes) 
			throws SAXException
		{
			Integer elementType = elementTypeMap.get(name);
			if (elementType==null) return;
			
			switch (elementType) {
			case ELEMENTTYPE_NODE:
				currentNode = new NodeData();
				currentNode.id = attributes.getValue("id");
				currentNode.lat = Float.parseFloat(attributes.getValue("lat"));
				currentNode.lon = Float.parseFloat(attributes.getValue("lon"));
				currentTags = new HashMap<String, String>();
				currentType = "node";
				break;
			case ELEMENTTYPE_WAY:
				currentWayId = attributes.getValue("id");
				currentTags = new HashMap<String, String>();
				currentNodeIdList = new LinkedList<String>();
				currentType = "way";
				break;
			case ELEMENTTYPE_NODEREF:
				currentNodeIdList.add(attributes.getValue("ref"));
				break;
			case ELEMENTTYPE_TAG:
				if ("way".equals(currentType) || "node".equals(currentType)) {
					currentTags.put(
							attributes.getValue("k"), 
							attributes.getValue("v"));
				}
				break;
			}
		}
		
		class NodeData
		{
			String id;
			float lat;
			float lon;
		}
	}

	public int getNodeCount()
	{
		return nodeCount;
	}
	public int getWayCount()
	{
		return wayCount;
	}

}
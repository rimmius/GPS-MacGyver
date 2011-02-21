package org.openstreetmap.gui.jmapviewer;

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

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

public class OsmParser
{
	private int nodeCount;
	private int wayCount;
	static String filename;
	public static ArrayList<Node> nodes = new ArrayList<Node>();
	public static void main(String[] args) throws SAXException, IOException
	{
	    File f;
	    JFileChooser jfc = new JFileChooser();
        jfc.showOpenDialog(null);
        f = new File(jfc.getSelectedFile().getPath());
        
		filename = f.toString();
		System.out.println(filename);
		InputSource inputSource = new InputSource(new FileReader(filename));
		OsmParser osmParser = new OsmParser();
		osmParser.parse(inputSource);
	}
	public static String getPath(){
	    return filename;
	}
	public void parse(InputSource inputSource) throws SAXException, IOException
	{
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		OsmSaxParser parser = new OsmSaxParser();
		xmlReader.setContentHandler(parser);
		xmlReader.parse(inputSource);
		
	}
	
	protected void onNode(
			String id,
			float lat, 
			float lon,
			Map<String,String> tags){
		nodeCount++;
		nodes.add(new Node(lon, lat, id));
		System.out.println("node: id="+id
                +",lat="+lat
                +",lon="+lon
                +",tags="+tags);
	}
	
	protected void onWay(
			String id,
			List<String> nodeIdList,
			Map<String, String> tags)
	{
		wayCount++;
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
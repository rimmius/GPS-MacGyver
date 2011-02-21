package org.openstreetmap.gui.jmapviewer;


import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PrintingOsmParser extends OsmParser
{
    
	public static void main(String[] args) throws SAXException, IOException
	{
	    
		InputSource inputSource = new InputSource(new FileReader(getPath()));
		OsmParser osmParser = new PrintingOsmParser();
		osmParser.parse(inputSource);
		
		System.err.println("nodes: "+osmParser.getNodeCount()
				+",ways: "+osmParser.getWayCount());
	}

	@Override
	protected void onNode(
			String id, 
			float lat, 
			float lon,
			Map<String,String> tags)
	{
		super.onNode(id, lat, lon, tags);
	    //
		System.out.println("node: id="+id
				+",lat="+lat
				+",lon="+lon
				+",tags="+tags);
	}

	@Override
	protected void onWay(String id, List<String> nodeIdList,
			Map<String, String> tags)
	{
		super.onWay(id, nodeIdList, tags);
		System.out.println("way: id="+id
				+",nodeIdList="+nodeIdList
				+",tags="+tags);
	}
}

package macgyver;



import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LOCParser {
    /**
     * @param Sorush Arefipour
     */
    public ArrayList<Geocache> getGeoInfo(String fileName) {

        ArrayList<Geocache> Geocaches =  new ArrayList<Geocache>();

        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            File file = new File(fileName);

            if (file.exists()) {
                Document doc = db.parse(file);
                Element docEle = doc.getDocumentElement();

                System.out.println("Root element of the document: "+ docEle.getNodeName());
                NodeList wayPointsList = docEle.getElementsByTagName("waypoint");
                System.out.println("Total waypoint: " + wayPointsList.getLength());

                if (wayPointsList != null && wayPointsList.getLength() > 0) {
                    for (int i = 0; i < wayPointsList.getLength(); i++) {

                        Geocache geocaches = new Geocache();

                        Node node = wayPointsList.item(i);

                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element e = (Element) node;

                            NodeList nodeList = e.getElementsByTagName("name");
                            //                          System.out.println("Name: "+ nodeList.item(0).getChildNodes().item(0).getNodeValue());
                            geocaches.setName(nodeList.item(0).getChildNodes().item(0).getNodeValue());

                            nodeList = e.getElementsByTagName("coord");
                            //                          System.out.println("Coordinates: Latitude: "+ nodeList.item(0).getAttributes().item(0).getNodeValue()
                            //                                  + "   Longitude: " + nodeList.item(0).getAttributes().item(1).getNodeValue());
                            geocaches.setLatitude(Double.parseDouble(nodeList.item(0).getAttributes().item(0).getNodeValue()));
                            geocaches.setLongitude(Double.parseDouble(nodeList.item(0).getAttributes().item(1).getNodeValue()));

                            nodeList = e.getElementsByTagName("type");
                            //                            System.out.println("Type: "+ nodeList.item(0).getChildNodes().item(0).getNodeValue());
                            geocaches.setType(nodeList.item(0).getChildNodes().item(0).getNodeValue());

                            nodeList = e.getElementsByTagName("link");
                            System.out.println("Link: "+ nodeList.item(0).getChildNodes().item(0).getNodeValue());
                            geocaches.setLink(nodeList.item(0).getChildNodes().item(0).getNodeValue());                        

                        }
                        Geocaches.add(geocaches);
                    }
                } else { System.exit(1); }
            }
        } catch (Exception e) { System.out.println(e); }
        return Geocaches;
    }
    public static void main(String[] args) {

        LOCParser parser = new LOCParser();
        try{
            parser.getGeoInfo("E:\\geocaching.xml");
        }catch (Exception e) { System.out.println("File could not be found"); }
    }
}
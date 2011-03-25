package macgyver;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;

import org.xml.sax.SAXException;

public class Start extends JFrame{
    OsmParser parser;
    Point center;
    public Start() throws SAXException, IOException{
        parser = new OsmParser("/home/gustehn/Skrivbord/liten.osm");
    }
    public void paint(Graphics g){
//        int x1 = LonToX(OsmParser.vertices.get(0).lon);
//        int y1 = LatToY(OsmParser.vertices.get(0).lat);
//        g.drawOval(x1, y1, 100, 100);
        for (int i = 0; i < OsmParser.vertices.size(); i++){
            for (int j = 0; j < OsmParser.vertices.get(i).adjacencies.size(); j++){
                int x1 = LonToX(OsmParser.vertices.get(i).lon);
                int y1 = LatToY(OsmParser.vertices.get(i).lat);
//                g.drawOval(x1, y1, 10, 10);
//                if (OsmParser.vertices.get(i).adjacencies.get(j).tags.containsValue("footway")){
//                    int x1 = LonToX(OsmParser.vertices.get(i).lon);
//                    int y1 = LatToY(OsmParser.vertices.get(i).lat);
                    int x2 = LonToX(OsmParser.vertices.get(i).adjacencies.get(j).target.lon);
                    int y2 = LatToY(OsmParser.vertices.get(i).adjacencies.get(j).target.lat);
//                    System.out.println(x1 + ", " + y1 + "," + x2 + "," + y2);
                    g.drawLine(x1, y1, x2, y2);
//                }
            }
        }
    }
    public int LonToX(double aLongitude) {
        int x = convertLat(Double.toString(aLongitude));
        int max = 115645;
        int min = 115534;
        int length = max - min;
        x = x - min;
        double procent = (double)x / length;
        double xD = procent * getWidth();
        x = (int)xD;
        x -= (getWidth() / 2);
        
        return x;
    }
    public int LatToY(double aLatitude) {
        int y = convertLat(Double.toString(aLatitude));
        int max = 574159;
        int min = 574239;
        y = y - min;
        double length = max - min;
        double procent = (double)y / length;
        double yD = procent * getWidth();
        y = (int)yD;
        return y;
    }
    public static void main(String[] args) throws SAXException, IOException{
        JFrame frame = new Start();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width, screenSize.height);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public int convertLat(String lat){
        if (lat.substring(0,1).equals("-")){
            lat = lat.substring(1, lat.length());
        }
        String degString = lat.substring(0, 2);
        String latRemainder  = ("0." + lat.substring(3, lat.length()));
        double remDouble = Double.parseDouble(latRemainder);
        remDouble = remDouble * 60;
        String remString = Double.toString(remDouble);
        String remStringNew = remString.substring(0, 2);
        String minValue = remStringNew;
        if (minValue.length() == 1){
            minValue = "0" + minValue;
        }
        String secString = ("0." + remString.substring(3, remString.length()));
        double secDouble = Double.parseDouble(secString);
        secDouble = secDouble * 60;
        secDouble = Math.round(secDouble);
        String secString2 = Double.toString(secDouble);
        if (secString2.length() == 3){
            if (Integer.parseInt(secString2.substring(0, 1)) >= 5){
                secString2 = secString2 + "0";
            }
            else{
                secString2 = "0" + secString2;
            }
        }
        double degDouble = Double.parseDouble(degString);
        double minDouble = Double.parseDouble(minValue);
        
        String degString2 = degString + minValue + secString2;
        degString2 = degString2.trim();
        double degDouble2 = Double.parseDouble(degString2);
        return((int)degDouble2);
        
    }
}

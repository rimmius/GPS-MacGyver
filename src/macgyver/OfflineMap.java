package macgyver;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import org.openstreetmap.*;
import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.xml.sax.SAXException;

import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
/**
 * 
 * @author Fredrik Gustafsson 2011
 *
 */
public class OfflineMap extends JPanel implements MouseListener{
    Image map;
    List<MapMarkerDot> mapMarkerList = new ArrayList<MapMarkerDot>();
    private double MAX_LAT = 57.7023900;
    private double MIN_LAT = 57.7088500;
    private double MIN_LON = 11.9258300;
    private double MAX_LON = 11.9429500;
    static OfflineMap mapp;
    int mouseX;
    int mouseY;
    JPopupMenu popUp = new JPopupMenu();
    private Component selectedComponent;
    public static List<Vertex> path = null;
    public static List<Vertex> shortestPath = null;
    static int recentNr = 100;
    static int targetNr = 100;
    JMenuItem showShort = new JMenuItem("Show the shortest path");
    JMenuItem fromCurrentJM = new JMenuItem("From My Current Position");
    JMenuItem delPoint = new JMenuItem("Delete this point");
    JMenuItem item = new JMenuItem("Set Destination");
    Image build = new ImageIcon("s.gif").getImage();
    public OfflineMap(Image map) throws SAXException, IOException{
        Thread thread1 = new Thread(new OsmParser("gbglast.osm"));
        thread1.run();
        this.map = map;
        addMouseListener(this);
        JFrame frame = new JFrame();
        frame.setSize(map.getWidth(null), map.getHeight(null));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        addButtons();
        frame.setContentPane(this);
        frame.setVisible(true);
        MapMarkerDot curPos = new MapMarkerDot(57.706855655355845, 11.937026381492615);
        mapMarkerList.add(curPos);
        this.add(new PopUp());
        addPopUp();
        repaint();
    }
    public void addButtons(){
        JButton plus = new JButton("+");
        JButton minus = new JButton("-");
        plus.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                zoomIn();
            }
        });
        minus.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                zoomOut();
            }
        });
        JButton right = new JButton("->");
        JButton left = new JButton("<-");
        right.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                panRight();
            }
        });
        left.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                panLeft();
            }
        });
        
        JButton down = new JButton("↑");
        JButton up = new JButton("↓");
        down.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                panDown();
            }
        });
        up.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                panUp();
            }
        });
        this.add(plus);
        this.add(minus);
        this.add(left);
        this.add(right);
        this.add(up);
        this.add(down);
        this.validate();
    }
    public void addPopUp(){
        
        item.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                MapMarkerDot temp = new MapMarkerDot(YToLat(mouseY), XToLon(mouseX));
                mapMarkerList.add(temp);     
                repaint();
            }
        });
        popUp.add(item);
        /**
         * Author: Fredrik Gustafsson
         * deletes selected mapdot and shortest path if available
         */
        
        delPoint.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                removePoint(recentNr);
            }
        });
        popUp.add(delPoint);
        delPoint.setVisible(false);
        /**
         * Author: Fredrik Gustafsson
         * Shows the shortest path to selected mapdot
         */
        fromCurrentJM.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                targetNr = recentNr;
                shortestPath = null;
                path = null;
                calcDistance(0, recentNr);
                fromCurrentJM.setVisible(false);
                repaint();
            }
        });
        popUp.add(fromCurrentJM);
        fromCurrentJM.setVisible(false);
        showShort.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                showShort.setVisible(false);
                item.setVisible(false);
                delPoint.setVisible(false);
                fromCurrentJM.setVisible(true);
                popUp.setVisible(true);
            }
        });
        popUp.add(showShort);
        showShort.setVisible(false);
    } 
    public void zoomIn(){
        double latLength = MAX_LAT - MIN_LAT;
        latLength = latLength / 4;
        MIN_LAT = MIN_LAT + latLength;
        MAX_LAT = MAX_LAT - latLength;
        
        double lonLength = MAX_LON - MIN_LON;
        lonLength = lonLength / 4;
        MIN_LON = MIN_LON + lonLength;
        MAX_LON = MAX_LON - lonLength;
        this.repaint();
    }
    public void zoomOut(){
        double latLength = MAX_LAT - MIN_LAT;
        latLength = latLength / 4;
        MIN_LAT = MIN_LAT - latLength;
        MAX_LAT = MAX_LAT + latLength;
        
        double lonLength = MAX_LON - MIN_LON;
        lonLength = lonLength / 4;
        MIN_LON = MIN_LON - lonLength;
        MAX_LON = MAX_LON + lonLength;
        this.repaint();
    }
    public void panUp(){
        double latLength = MAX_LAT - MIN_LAT;
        latLength = latLength / 4;
        MIN_LAT = MIN_LAT + latLength;
        MAX_LAT = MAX_LAT + latLength;
        this.repaint();
    }
    public void panDown(){
        double latLength = MAX_LAT - MIN_LAT;
        latLength = latLength / 4;
        MIN_LAT = MIN_LAT - latLength;
        MAX_LAT = MAX_LAT - latLength;

        this.repaint();
    }
    public void panRight(){
        double lonLength = MAX_LON - MIN_LON;
        lonLength = lonLength / 4;
        MIN_LON = MIN_LON + lonLength;
        MAX_LON = MAX_LON + lonLength;
        this.repaint();
    }
    public void panLeft(){
        double lonLength = MAX_LON - MIN_LON;
        lonLength = lonLength / 4;
        MIN_LON = MIN_LON - lonLength;
        MAX_LON = MAX_LON - lonLength;
        this.repaint();
    }
    public void calcDistance(int sourceNr, int targetNr){
        double lat1 = mapMarkerList.get(sourceNr).getLat();
        double lon1 = mapMarkerList.get(sourceNr).getLon();
            
        double lat2 = mapMarkerList.get(targetNr).getLat();
        double lon2 = mapMarkerList.get(targetNr).getLon();
        int nearest = nearestVertex(lon1, lat1);
        int nearest2 = nearestVertex(lon2, lat2); 
        System.out.println(OsmParser.vertices.get(nearest2).name);
        Macgyver.computePaths(OsmParser.vertices.get(nearest));
        path = getShortPathTo(OsmParser.vertices.get(nearest2));
        shortestPath = new ArrayList<Vertex>();
        double shortDist = 0;
        for (int i = path.size()-1; i > 1; i--){
            shortDist += getDistance(path.get(i-1).lon, path.get(i-1).lat, path.get(i).lon, path.get(i).lat);
        }
        shortDist = shortDist * 100000;
        int distInt = (int)shortDist;
//        distlabel.setText("distance to destination: " + distInt + " meters");
//        distlabel.setVisible(true);
//        panel.validate();
        System.out.println(path.size());
        Vertex myPos = new Vertex("1", mapMarkerList.get(sourceNr).getLat(), mapMarkerList.get(sourceNr).getLon());
        path.add(0, myPos);
        System.out.println(path.size());
        Vertex lastPos = new Vertex("2", mapMarkerList.get(targetNr).getLat(), mapMarkerList.get(targetNr).getLon());
        path.add(lastPos);
        System.out.println(path.size());
        shortestPath = path;
        this.validate();
    }
    public List<Vertex> getShortPathTo(Vertex target){
        List<Vertex> paths = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous){
            paths.add(vertex);
        }
        Collections.reverse(paths);
        return paths;
    }
    /**
     * Calculates the nearest Vertex in vertices list of the actual position
     * Author: Fredrik Gustafsson
     * @param lon1
     * @param lat1
     * @return
     */
    public int nearestVertex(double lon1, double lat1){
        double minDistance = 1000;
        int nearest = -1;
        for (int i = 0; i < OsmParser.vertices.size(); i++) {
            //double checkThisValue = Math.sqrt(((lon1-OsmParser.vertices.get(i).lon) * (lon1-OsmParser.vertices.get(i).lon)) + ((lat1 - OsmParser.vertices.get(i).lat) * (lat1 - OsmParser.vertices.get(i).lat)));
            double checkThisValue = getDistance(lon1, lat1, OsmParser.vertices.get(i).lon, OsmParser.vertices.get(i).lat);
            if (checkThisValue < 0){
                checkThisValue = checkThisValue *-1;
            }
            if (checkThisValue < minDistance && OsmParser.vertices.get(i).adjacencies.size() > 0){
                if (!OsmParser.vertices.get(i).adjacencies.get(0).tags.containsKey("building") && !OsmParser.vertices.get(i).adjacencies.get(0).tags.isEmpty() && !OsmParser.vertices.get(0).adjacencies.get(0).tags.containsValue("school")){
                    if (!OsmParser.vertices.get(i).adjacencies.get(0).tags.containsValue("raceway")){
                        if (!OsmParser.vertices.get(i).adjacencies.get(0).tags.containsValue("construction")){
                            if (!OsmParser.vertices.get(i).adjacencies.get(0).tags.containsValue("coastline")){
                                if (!OsmParser.vertices.get(i).adjacencies.get(0).tags.containsValue("golf_course")){
                                    if (!OsmParser.vertices.get(i).adjacencies.get(0).tags.containsValue("parking")){
                                        if (!OsmParser.vertices.get(i).adjacencies.get(0).tags.containsKey("railway")){
                                            if (!OsmParser.vertices.get(i).adjacencies.get(0).tags.containsKey("waterway") && 
                                                    !OsmParser.vertices.get(i).adjacencies.get(0).tags.containsKey("fixme") && 
                                                    !OsmParser.vertices.get(i).adjacencies.get(0).tags.containsKey("natural") && 
                                                    !OsmParser.vertices.get(i).adjacencies.get(0).tags.containsKey("boundary") && 
                                                    !OsmParser.vertices.get(i).adjacencies.get(0).tags.containsKey("barrier") && 
                                                    !OsmParser.vertices.get(i).adjacencies.get(0).tags.containsValue("bus")){
                                                minDistance = checkThisValue;
                                                nearest = i;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return nearest;
    }
    public double getDistance(double lon1, double lat1, double lon2, double lat2){
        return Math.sqrt(((lon1 - lon2) * (lon1 - lon2)) + ((lat1 - lat2) * (lat1 - lat2)));
    }
    public void removePoint(int nr){
        mapMarkerList.remove(nr);
        if (targetNr == nr){
            if (shortestPath != null){
                shortestPath = null;
//                distlabel.setText("");
                this.validate();
            }
        }
        repaint();
        this.validate();
    }
    public void paint(Graphics g){
        super.paint(g);
        for (int i = 0; i < OsmParser.vertices.size(); i++){
            int x1 = LonToX(OsmParser.vertices.get(i).lon);
            int y1 = LatToY(OsmParser.vertices.get(i).lat);
//            if (OsmParser.vertices.get(i).tags != null){
//                if (OsmParser.vertices.get(i).tags.containsKey("name") && OsmParser.vertices.get(i).tags.containsKey("highway")){
//                    g.drawString(OsmParser.vertices.get(i).tags.get("name"), x1, y1);
//                }
//            }
            for (int j = 0; j < OsmParser.vertices.get(i).adjacencies.size(); j++){
                
                int x2 = LonToX(OsmParser.vertices.get(i).adjacencies.get(j).target.lon);
                int y2 = LatToY(OsmParser.vertices.get(i).adjacencies.get(j).target.lat);
                g.setColor(Color.BLACK);
                g.drawLine(x1, y1, x2, y2);
            }
        }
        for (int i = 0; i < mapMarkerList.size(); i++) {
            Point p = new Point(LonToX(mapMarkerList.get(i).getLon()), LatToY(mapMarkerList.get(i).getLat()));
                int size_h = 5;
                int size = size_h * 2;
                g.setColor(Color.BLUE);
                g.fillOval(p.x - size_h, p.y - size_h, size, size);
                g.setColor(Color.BLACK);
                g.drawOval(p.x - size_h, p.y - size_h, size, size);
        }
        if (mapMarkerList.size() > 1){
            
            if (shortestPath != null){
                Graphics2D g2d = (Graphics2D)g;
                int width = 4;
                g2d.setStroke(new BasicStroke(width));
                g2d.setColor(Color.BLUE);
                for (int i = 1; i < shortestPath.size(); i++){
                    int x1 = (int)LonToX(shortestPath.get(i-1).lon);
                    int y1 = (int)LatToY(shortestPath.get(i-1).lat);
                    int x2 = (int)LonToX(shortestPath.get(i).lon);
                    int y2 = (int)LatToY(shortestPath.get(i).lat);
                    g2d.drawLine(x1, y1, x2, y2); 
                }
            }
        }
    }
    public static void main(String[] args) throws SAXException, IOException{
        Image img = new ImageIcon("offlinemap.png").getImage();
        mapp = new OfflineMap(img);
    }
    public int LonToX(double aLongitude) {
        double yD = aLongitude - MIN_LON;
        double length = MAX_LON - MIN_LON;
        double procent = yD / length;
        yD = procent * getWidth();
        int y = (int)yD;
        return y;
    }
    public int LatToY(double aLatitude) {
        double yD = aLatitude - MIN_LAT;
        double length = MAX_LAT - MIN_LAT;
        double procent = yD / length;
        yD = procent * getHeight();
        int y = (int)yD;
        return y;
    }
    public double YToLat(int y) {
        double length = getHeight();
        double procent = y / length;
        double latLength = MAX_LAT - MIN_LAT;
        procent = latLength * procent;
        procent = MIN_LAT + procent;
        return procent;
    }
    public double XToLon(int x) {
        
        double length = getWidth();
        double procent = x / length;
        double lonLength = MAX_LON - MIN_LON;
        procent = lonLength * procent;
        procent = MIN_LON + procent;
        return procent;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        checkPopup(e);
        
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        checkPopup(e);
    }
    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        checkPopup(e);
    }
    @Override
    public void mousePressed(MouseEvent e) {
        checkPopup(e);
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        checkPopup(e);
    }
    private void checkPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            mouseX = e.getX();
            mouseY = e.getY();
            for (int i = 1; i < mapMarkerList.size(); i++){
                int x = LonToX(mapMarkerList.get(i).getLon());
                int y = LatToY(mapMarkerList.get(i).getLat());
                System.out.println(mouseX + ", " + mouseY + ", - " + x + ", " + y);
                int xmin = x-11;
                int xmax = x+11;
                int ymin = y-11;
                int ymax = y+11;
                if ((mouseX > xmin) && (mouseX < xmax)){
                    if ((mouseY > ymin) && (mouseY < ymax)){
                        showShort.setVisible(true);
                        delPoint.setVisible(true);
                        fromCurrentJM.setVisible(false);
                        item.setVisible(false);
                        popUp.validate();
                        recentNr = i;
                        break;
                    }
                }
                if ((mouseX > xmax) || (mouseX < xmin)){
                    if((mouseY > ymax) || (mouseY < ymin)){
                        showShort.setVisible(false);
                        delPoint.setVisible(false);
                        fromCurrentJM.setVisible(false);
                        item.setVisible(true);
                        popUp.validate();
                    }
                }
            }
            if (mapMarkerList.size() == 1){
                showShort.setVisible(false);
                delPoint.setVisible(false);
                item.setVisible(true);
                popUp.validate();
            }
          selectedComponent = e.getComponent(  );
          popUp.show(e.getComponent(  ), e.getX(  ), e.getY(  ));
        }
    }
}

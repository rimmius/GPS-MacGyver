package macgyver;

import org.openstreetmap.*;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.io.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

import javax.swing.JFileChooser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.TileController;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

public class Macgyver extends JFrame{
    private Component selectedComponent;
    private static final long serialVersionUID = 1L;
    public static int counter = 0;
    final JMapViewer map;
    int mouseY;
    int mouseX;
    static int targetNr = 100;
    static int sourceNr = 100;
    static int recentNr = 100;
    public static ArrayList<MapMarkerDot> dots = new ArrayList<MapMarkerDot>();
    TileController tileController;
    boolean paintIt = false;
    double myLong;
    double myLat;
    String filename;
    OsmParser parser;
    public static List<Vertex> path = null;
    public static List<Vertex> shortestPath = null;
    JPanel locPanel = new JPanel(new GridBagLayout());
    JPopupMenu popUp = new JPopupMenu();
    public static Thread thread1;
    static int latestSource;
    static int latestTarget;
    JPanel panel = new JPanel();
    JLabel distlabel = new JLabel();

    public Macgyver() throws InterruptedException, SAXException, IOException {
        super("GPS MacGyver");
        setSize(400, 400);
         map = new JMapViewer();
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        add(panel, BorderLayout.NORTH);
        add(map, BorderLayout.CENTER);
        add(locPanel, BorderLayout.EAST);
        /**
         * Author: Fredrik Gustafsson
         * 
         * Sets destination with blue mapdot on map
         */
        final JMenuItem item = new JMenuItem("Set Destination");
        
        item.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                MapMarkerDot temp = new MapMarkerDot(Color.BLUE, map.getPosition(mouseX, mouseY).getLat(), map.getPosition(mouseX, mouseY).getLon());
                map.addMapMarker(temp);
            }
        });
        popUp.add(item);
        /**
         * Author: Fredrik Gustafsson
         * deletes selected mapdot and shortest path if available
         */
        final JMenuItem delPoint = new JMenuItem("Delete this point");
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
        final JMenuItem showShort = new JMenuItem("Show the shortest path");
        final JMenuItem fromCurrentJM = new JMenuItem("From My Current Position");
        
        fromCurrentJM.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                targetNr = recentNr;
                shortestPath = null;
                path = null;
                calcDistance(0, recentNr);
                fromCurrentJM.setVisible(false);
                map.repaint();
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
        /**
         * Author : Fredrik Gustafsson, max mirkia
         * 
         */
        map.addMouseListener( new MouseAdapter(  ) {
            public void mousePressed(MouseEvent e) { checkPopup(e); }
            public void mouseClicked(MouseEvent e) { checkPopup(e); }
            public void mouseReleased(MouseEvent e) { checkPopup(e); }
            private void checkPopup(MouseEvent e) {
              if (e.isPopupTrigger()) {
                  mouseX = e.getX();
                  mouseY = e.getY();
                  for (int i = 1; i < JMapViewer.mapMarkerList.size(); i++){
                      int x = (int)map.getMapPosition(JMapViewer.mapMarkerList.get(i).getLat(), JMapViewer.mapMarkerList.get(i).getLon(), false).getX();
                      int y = (int)map.getMapPosition(JMapViewer.mapMarkerList.get(i).getLat(), JMapViewer.mapMarkerList.get(i).getLon(), false).getY();
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
                  if (JMapViewer.mapMarkerList.size() == 1){
                      showShort.setVisible(false);
                      delPoint.setVisible(false);
                      item.setVisible(true);
                      popUp.validate();
                  }
                selectedComponent = e.getComponent(  );
                popUp.show(e.getComponent(  ), e.getX(  ), e.getY(  ));
              }
            }
          });

       
        map.setDisplayPositionByLatLon(57.706855655355845, 11.937026381492615, 13);
        map.addMapMarker(new MapMarkerDot(57.706855655355845, 11.937026381492615));
        
        map.add(new PopUp());
        //
        /**
         * Fredrik Gustafsson
         */
        JButton current = new JButton("Show current location");
        panel.add(current);
        /**
         * Author: Fredrik Gustafsson
         */
        JButton readLocFile = new JButton("Read loc-file");
        panel.add(readLocFile);
        readLocFile.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                readLocFile();
            }
        });
        
        /**
         * Author: Fredrik Gustafsson
         * 
         */
        current.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                Main main = new Main();
                Main.main(null);
                
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException es) {
                    // TODO Auto-generated catch block
                    es.printStackTrace();
                }
                Thread threaden = new Thread(new Main(map));
                threaden.start();
                map.setDisplayPositionByLatLon(main.getLatitude(), main.getLongitude(), 13);
            }
        });

//        File f;
//        JFileChooser jfc = new JFileChooser();
//        jfc.showOpenDialog(null);
//        f = new File(jfc.getSelectedFile().getPath());
//        
//        filename = f.toString();
//        System.out.println(filename);
        JFrame waitingF = new JFrame("Please wait");
        JPanel wPanel = new JPanel();
        JLabel label = new JLabel("Please wait while loading the ways and nodes.");
        waitingF.setSize(400, 150);
        waitingF.setLocationRelativeTo(null);
        waitingF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        waitingF.setVisible(true);
        wPanel.add(label);
        waitingF.add(wPanel);
        
        thread1 = new Thread(new OsmParser("gbglast.osm"));
        thread1.run();
        waitingF.dispose();
        setVisible(true);
//        parser = new OsmParser("/home/gustehn/Skrivbord/sweden.osm");
        for (int i = 0; i < OsmParser.vertices.size(); i++){
            if (OsmParser.vertices.get(i).adjacencies.size() == 0){
                OsmParser.vertices.remove(i);
            }
        }
        panel.add(distlabel);
    }
    /**
     * removePoint
     * Author: Fredrik Gustafsson
     * @param nr : number of the mapdot that should be removed
     */
    public void removePoint(int nr){
        JMapViewer.mapMarkerList.remove(nr);
        if (targetNr == nr){
            if (shortestPath != null){
                shortestPath = null;
                distlabel.setText("");
                panel.validate();
            }
        }
        map.repaint();
        map.validate();
    }
    /**
     * Author: Fredrik Gustafsson
     * Allows user to specify loc-file and creates new locparser object and creates a new mapdot
     */
    public void readLocFile(){
        File f;
        JFileChooser jfc = new JFileChooser();
        jfc.showOpenDialog(null);
        f = new File(jfc.getSelectedFile().getPath());
        GridBagConstraints c = new GridBagConstraints();
        String locfilename = f.toString();
        LOCParser locp = new LOCParser();
        final ArrayList<Geocache> geocaches = locp.getGeoInfo(locfilename);
        final ArrayList<JCheckBox> checkbox = new ArrayList<JCheckBox>();
        for (int i = 0; i < geocaches.size(); i++){
            checkbox.add(new JCheckBox(geocaches.get(i).getName()));
        }
        for (int i = 0; i < checkbox.size(); i++){
            c.gridx = 0;
            c.gridy = i;
            locPanel.add(checkbox.get(i), c);
        }
        JButton chooseGeo = new JButton("Choose this Geocache");
        c.gridx = 0;
        c.gridy = checkbox.size();
        chooseGeo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                for (int i = 0; i < checkbox.size(); i++){
                    if(checkbox.get(i).isSelected()){
                        MapMarkerDot temp = new MapMarkerDot(Color.BLUE, geocaches.get(i).getLatitude(), geocaches.get(i).getLongitude());
                        map.addMapMarker(temp);
                    }
                }
            }
        });
        locPanel.add(chooseGeo, c);
        this.validate();
    }
    /**
     * calcDistance
     * Author: Fredrik Gustafsson
     * Starts the shortest path algorithm.
     *
     * 
     */
    public void calcDistance(int sourceNr, int targetNr){
        double lat1 = JMapViewer.mapMarkerList.get(sourceNr).getLat();
        double lon1 = JMapViewer.mapMarkerList.get(sourceNr).getLon();
            
        double lat2 = JMapViewer.mapMarkerList.get(targetNr).getLat();
        double lon2 = JMapViewer.mapMarkerList.get(targetNr).getLon();
        System.out.println(lat2 + ", " + lon2);
        int nearest = nearestVertex(lon1, lat1);
        int nearest2 = nearestVertex(lon2, lat2); 
        System.out.println(OsmParser.vertices.get(nearest2).name);
        computePaths(OsmParser.vertices.get(nearest));
//        Vertex source = OsmParser.vertices.get(nearest);
//        Vertex target = OsmParser.vertices.get(nearest2);
//        OsmParser.vertices.remove(nearest);
//        OsmParser.vertices.remove(nearest2);
//        OsmParser.vertices.set(0, source);
//        OsmParser.vertices.add(target);
//        computePaths(OsmParser.vertices.get(0));
//        
//        path = getShortPathTo(OsmParser.vertices.get(OsmParser.vertices.size()-1));
        path = getShortPathTo(OsmParser.vertices.get(nearest2));
        shortestPath = new ArrayList<Vertex>();
        double shortDist = 0;
        for (int i = path.size()-1; i > 1; i--){
            shortDist += getDistance(path.get(i-1).lon, path.get(i-1).lat, path.get(i).lon, path.get(i).lat);
        }
        shortDist = shortDist * 100000;
        int distInt = (int)shortDist;
        distlabel.setText("distance to destination: " + distInt + " meters");
        distlabel.setVisible(true);
        panel.validate();
        shortestPath = path;
        map.validate();
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
        System.out.println("something");
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
    /**
     * Author: Fredrik Gustafsson
     * @param lon1
     * @param lat1
     * @param lon2
     * @param lat2
     * @return
     */
    public double getDistance(double lon1, double lat1, double lon2, double lat2){
        return Math.sqrt(((lon1 - lon2) * (lon1 - lon2)) + ((lat1 - lat2) * (lat1 - lat2)));
    }
    /**
     * Author: Fredrik Gustafsson
     * @param source : starting position
     */
    public static void computePaths(Vertex source){
        source.minDistance = 0;
        PriorityQueue<Vertex> vqueue = new PriorityQueue<Vertex>();
        vqueue.add(source);
        
        while(!vqueue.isEmpty()){
            Vertex u = vqueue.poll();
            for (Edge e : u.adjacencies){
                Vertex v = e.target;
                if (v.tags != null){
                    if (!v.tags.containsValue("cycleway") || !v.tags.containsValue("footway")){
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
                if (distanceThroughU < v.minDistance){
                    vqueue.remove(v);
                    v.minDistance = distanceThroughU;
                    v.previous = u;
                    vqueue.add(v);
                }
                }
                }
            }
        }
    }
    /**
     * Author: Fredrik Gustafsson
     * @param target
     * @return - ArrayList with shortest path
     */
    public List<Vertex> getShortPathTo(Vertex target){
        List<Vertex> paths = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous){
            paths.add(vertex);
        }
        Collections.reverse(paths);
        return paths;
    }
    /**
     * Author: Fredrik Gustafsson
     * @return : ArrayList with shortest path
     */
    public static List<Vertex> getShortestPath(){
        return shortestPath;
    }
    public static void main(String[] args) throws InterruptedException, SAXException, IOException {
        new Macgyver();
    }
}

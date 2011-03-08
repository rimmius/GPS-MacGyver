package org.openstreetmap.gui.jmapviewer;




import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
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

import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

public class Demo extends JFrame implements ActionListener{
    private Component selectedComponent;
    private static final long serialVersionUID = 1L;
    public static int counter = 0;
    final JMapViewer map;
    int mouseY;
    int mouseX;
    public static ArrayList<MapMarkerDot> dots = new ArrayList<MapMarkerDot>();
    TileController tileController;
    boolean paintIt = false;
    double myLong;
    double myLat;
    String filename;
    OsmParser parser;
    public static List<Vertex> path = null;
    public static List<Vertex> shortestPath = null;
    public Demo() throws InterruptedException, SAXException, IOException {
        super("JMapViewer Demo");
        setSize(400, 400);
         map = new JMapViewer();
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        JPanel panel = new JPanel();
        JPanel helpPanel = new JPanel();
        add(panel, BorderLayout.NORTH);
        add(helpPanel, BorderLayout.SOUTH);
        JLabel helpLabel = new JLabel("Use right mouse button to move,\n "
                + "left double click or mouse wheel to zoom.");
        helpPanel.add(helpLabel);
        JButton button = new JButton("setDisplayToFitMapMarkers");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                map.setDisplayToFitMapMarkers();
            }
        });
        JComboBox tileSourceSelector = new JComboBox(new TileSource[] { new OsmTileSource.Mapnik(),
                new OsmTileSource.TilesAtHome(), new OsmTileSource.CycleMap() });
        tileSourceSelector.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                map.setTileSource((TileSource) e.getItem());
            }
        });
        JComboBox tileLoaderSelector;
        try {
            tileLoaderSelector = new JComboBox(new TileLoader[] { new OsmFileCacheTileLoader(map),
                    new OsmTileLoader(map) });
        } catch (IOException e) {
            tileLoaderSelector = new JComboBox(new TileLoader[] { new OsmTileLoader(map) });
        }
        tileLoaderSelector.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                map.setTileLoader((TileLoader) e.getItem());
            }
        });
        map.setTileLoader((TileLoader) tileLoaderSelector.getSelectedItem());
        final JCheckBox showMapMarker = new JCheckBox("Map markers visible");
        showMapMarker.setSelected(map.getMapMarkersVisible());
        showMapMarker.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                map.setMapMarkerVisible(showMapMarker.isSelected());
            }
        });
        panel.add(showMapMarker);
        
        add(map, BorderLayout.CENTER);
        
        /**
         * maxmirkia, Fredrik Gustafsson
         */
        
        final JPopupMenu popUp = new JPopupMenu();
        JMenuItem item = new JMenuItem("Set Destination");
        item.addActionListener(this);
        popUp.add(item);
        int x,y;
        map.addMouseListener( new MouseAdapter(  ) {
            public void mousePressed(MouseEvent e) { checkPopup(e); }
            public void mouseClicked(MouseEvent e) { checkPopup(e); }
            public void mouseReleased(MouseEvent e) { checkPopup(e); }
            private void checkPopup(MouseEvent e) {
              if (e.isPopupTrigger()) {
                selectedComponent = e.getComponent(  );
                popUp.show(e.getComponent(  ), e.getX(  ), e.getY(  ));
                mouseX = e.getX();
                mouseY = e.getY();
              }
            }
          });
        //map.addMapMarker(new MapMarkerDot(Color.BLUE, 57.71031468792876, 11.925959587097168));
        //map.addMapMarker(new MapMarkerDot();
        map.setDisplayPositionByLatLon(57.71031468792876, 11.925959587097168, 15);
        
        map.addMapMarker(new MapMarkerDot(57.706855655355845, 11.937026381492615));
        map.add(new PopUp());
        //
        /**
         * Fredrik Gustafsson
         */
        JButton current = new JButton("Show current location");
        panel.add(current);
        JButton chooseCo = new JButton("Choose coordinates");
        panel.add(chooseCo);
        chooseCo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String longitudeString = JOptionPane.showInputDialog(null,"Enter longitude","Longitude");
                String latitudeString = JOptionPane.showInputDialog(null,"Enter latitude","Latitude");
                double latitudeDouble = Double.parseDouble(latitudeString);
                double longitudeDouble = Double.parseDouble(longitudeString);
                MapMarkerDot marker = new MapMarkerDot(latitudeDouble, longitudeDouble);
                map.addMapMarker(marker);
            }
        });
        
        current.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                Main main = new Main();
                main.main(null);
                
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
        JButton resetDest = new JButton("Reset destination");
        resetDest.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                resetShortestPath();
            }
        });
        panel.add(resetDest);
        File f;
        JFileChooser jfc = new JFileChooser();
        jfc.showOpenDialog(null);
        f = new File(jfc.getSelectedFile().getPath());
        
        filename = f.toString();
        System.out.println(filename);
        JFrame waitingF = new JFrame("Please wait");
        JPanel wPanel = new JPanel();
        JLabel label = new JLabel("Please wait while loading the tiles.");
        waitingF.setSize(300, 150);
        waitingF.setLocationRelativeTo(null);
        waitingF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        waitingF.setVisible(true);
        wPanel.add(label);
        waitingF.add(wPanel);
        
//        InputSource inputSource = new InputSource(new FileReader(filename));
            Thread thread1 = new Thread(new OsmParser(filename));
//            OsmParser osmParser = new OsmParser();
//            osmParser.parse(inputSource);
            thread1.run();
//        MapMarkerDot temp = new MapMarkerDot(Color.BLUE, 57.70762085506806, 11.93789005279541);
//        map.addMapMarker(temp);
//        calcDistance();
            waitingF.dispose();
    }
    /**
     * @param args
     * @throws InterruptedException 
     */
    
    /**
     * maxmirkia, Fredrik Gustafsson
     */
    public void actionPerformed(ActionEvent e) {
        MapMarkerDot temp = new MapMarkerDot(Color.BLUE, map.getPosition(mouseX, mouseY).getLat(), map.getPosition(mouseX, mouseY).getLon());
        map.addMapMarker(temp);
        dots.add(temp);
        System.out.println(temp.lat + ", " + temp.lon);
        if (map.mapMarkerList.size() > 1){
            calcDistance();
        }
    }
    public void calcDistance(){
        double lat1 = map.mapMarkerList.get(0).getLat();
        double lon1 = map.mapMarkerList.get(0).getLon();
        System.out.println(lat1 + ", " + lon1);
            
        double lat2 = map.mapMarkerList.get(1).getLat();
        double lon2 = map.mapMarkerList.get(1).getLon();
           
        int nearest = nearestVertex(lon1, lat1);
        int nearest2 = nearestVertex(lon2, lat2); 
    
        Vertex source = OsmParser.vertices.get(nearest);
        Vertex target = OsmParser.vertices.get(nearest2);
        OsmParser.vertices.remove(nearest);
        OsmParser.vertices.remove(nearest2);
        OsmParser.vertices.set(0, source);
        OsmParser.vertices.add(target);
        computePaths(OsmParser.vertices.get(0));
        
        path = getShortPathTo(OsmParser.vertices.get(OsmParser.vertices.size()-1));
        shortestPath = new ArrayList<Vertex>();
        shortestPath = path;
    }
    public int nearestVertex(double lon1, double lat1){
        double minDistance = 1000;
        int nearest = -1;
        
        for (int i = 0; i < OsmParser.vertices.size(); i++) {
            double checkThisValue = Math.sqrt(((lon1-OsmParser.vertices.get(i).lon) * (lon1-OsmParser.vertices.get(i).lon)) + ((lat1 - OsmParser.vertices.get(i).lat) * (lat1 - OsmParser.vertices.get(i).lat)));
            if (checkThisValue < minDistance && OsmParser.vertices.get(i).adjacencies.size() > 0 && !OsmParser.vertices.get(i).adjacencies.get(0).tags.containsKey("building")){
                if (!OsmParser.vertices.get(i).adjacencies.get(0).tags.containsValue("raceway")){

                    if (!OsmParser.vertices.get(i).adjacencies.get(0).tags.containsValue("coastline")){
                        minDistance = checkThisValue;
                        nearest = i;
                    }
                }
            }
        }
        return nearest;
    }
    
    public static void computePaths(Vertex source){
        source.minDistance = 0;
        PriorityQueue<Vertex> vqueue = new PriorityQueue<Vertex>();
        vqueue.add(source);
        
        while(!vqueue.isEmpty()){
            Vertex u = vqueue.poll();
            for (Edge e : u.adjacencies){
                Vertex v = e.target;
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
    public static List<Vertex> getShortPathTo(Vertex target){
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous){
            path.add(vertex);
        }
        Collections.reverse(path);
        return path;
    }
    public static List<Vertex> getShortestPath(){
        return shortestPath;
    }
    public void resetShortestPath(){
        shortestPath = null;
        map.mapMarkerList.remove(1);
        map.repaint();
    }
    public static void main(String[] args) throws InterruptedException, SAXException, IOException {
        new Demo().setVisible(true);
    }
}

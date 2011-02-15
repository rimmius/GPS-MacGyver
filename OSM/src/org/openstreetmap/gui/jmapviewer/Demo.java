package org.openstreetmap.gui.jmapviewer;

//License: GPL. Copyright 2008 by Jan Peter Stotz


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

import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

/**
 *
 * Demonstrates the usage of {@link JMapViewer}
 *
 * @author Jan Peter Stotz
 *
 */
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
    public Demo() throws InterruptedException {
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
        panel.add(tileSourceSelector);
        panel.add(tileLoaderSelector);
        final JCheckBox showMapMarker = new JCheckBox("Map markers visible");
        showMapMarker.setSelected(map.getMapMarkersVisible());
        showMapMarker.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                map.setMapMarkerVisible(showMapMarker.isSelected());
            }
        });
        panel.add(showMapMarker);
        final JCheckBox showTileGrid = new JCheckBox("Tile grid visible");
        showTileGrid.setSelected(map.isTileGridVisible());
        showTileGrid.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                map.setTileGridVisible(showTileGrid.isSelected());
            }
        });
        panel.add(showTileGrid);
        final JCheckBox showZoomControls = new JCheckBox("Show zoom controls");
        showZoomControls.setSelected(map.getZoomContolsVisible());
        showZoomControls.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                map.setZoomContolsVisible(showZoomControls.isSelected());
            }
        });
        panel.add(showZoomControls);
       // panel.add(button);
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
        
        map.addMapMarker(new MapMarkerDot(57.71031468792876, 11.925959587097168));
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
    }
   
    public static void main(String[] args) throws InterruptedException {
        new Demo().setVisible(true);
    }
}

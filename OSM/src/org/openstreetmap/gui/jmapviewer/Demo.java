package org.openstreetmap.gui.jmapviewer;

//License: GPL. Copyright 2008 by Jan Peter Stotz


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
<<<<<<< HEAD
import javax.swing.JMenuItem;
=======
import javax.swing.JOptionPane;
>>>>>>> ca2546dd29bbf7b5b4b556ce15af101d67c510c3
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
    public Demo() throws InterruptedException {
        super("JMapViewer Demo");
        setSize(400, 400);
        final JMapViewer map = new JMapViewer();
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
         * maxmirkia
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
               // System.out.println("alloo");
              }
            }
          });
        map.addMapMarker(new MapMarkerDot(Color.BLUE, 49.814284999, 8.642065999));
        //map.addMapMarker(new MapMarkerDot();
        map.setDisplayPositionByLatLon(49.814284999, 8.642065999, 15);
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
                counter++;
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
     * maxmirkia
     */
    public void actionPerformed(ActionEvent e) {  
        System.out.println();
      }
    public static void main(String[] args) throws InterruptedException {
        new Demo().setVisible(true);
    }

}

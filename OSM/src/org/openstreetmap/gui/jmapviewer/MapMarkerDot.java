package org.openstreetmap.gui.jmapviewer;

//License: GPL. Copyright 2008 by Jan Peter Stotz

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

public class MapMarkerDot implements MapMarker {

    double lat;
    double lon;
    Color color;
    JMapViewer map;
    Demo demo;
    public MapMarkerDot(double lat, double lon) {
        this(Color.YELLOW, lat, lon);
    }

    public MapMarkerDot(Color color, double lat, double lon) {
        super();
        this.color = color;
        this.lat = lat;
        this.lon = lon;

    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void paint(Graphics g, Point position) {
        int size_h = 5;
        int size = size_h * 2;
        g.setColor(color);
        g.fillOval(position.x - size_h, position.y - size_h, size, size);
        g.setColor(Color.BLACK);
        g.drawOval(position.x - size_h, position.y - size_h, size, size);
       
    }
    @Override
    public String toString() {
        return "MapMarker at " + lat + " " + lon;
    }

}

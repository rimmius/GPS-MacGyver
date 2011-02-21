package org.openstreetmap.gui.jmapviewer;

public class Node {
    
    private float lon;
    private float lat;
    private String id;
    
    public Node(float lon, float lat, String id){
        this.lon = lon;
        this.lat = lat;
        this.id = id;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
    public double getLon() {
        return lon;
    }
    public void setLon(float lon) {
        this.lon = lon;
    }
    public double getLat() {
        return lat;
    }
    public void setLat(float lat) {
        this.lat = lat;
    }
    
}

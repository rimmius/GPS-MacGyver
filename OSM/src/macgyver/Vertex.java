package macgyver;


import java.util.ArrayList;
import java.util.Map;


public class Vertex implements Comparable<Vertex>{
    public final String name;
    public ArrayList<Edge> adjacencies;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;
    public double lat;
    public double lon;
    public Map<String, String> tags;
    public Vertex(String argName, double lat, double lon){
        name = argName;
        this.lat = lat;
        this.lon = lon;
        adjacencies = new ArrayList<Edge>();
    }
    public String toString(){
        return name;
    }
    public int compareTo(Vertex other){
        return Double.compare(minDistance, other.minDistance);
    }
    
}


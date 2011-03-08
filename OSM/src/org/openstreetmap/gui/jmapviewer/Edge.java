package org.openstreetmap.gui.jmapviewer;

import java.util.HashMap;
import java.util.Map;

public class Edge {
    public final Vertex target;
    public final double weight;
    public Map<String, String> tags;
    public Edge(Vertex argTarget, double argWeight, Map<String, String> tags){
        target = argTarget;
        weight = argWeight;
        this.tags = tags;
    }
}

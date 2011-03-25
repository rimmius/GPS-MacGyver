package org.openstreetmap.gui.jmapviewer;

//License: GPL. Copyright 2008 by Jan Peter Stotz

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

public abstract class JMapController {

    protected JMapViewer map;

    public JMapController(JMapViewer map) {
        this.map = map;
        if (this instanceof MouseListener)
            map.addMouseListener((MouseListener) this);
        if (this instanceof MouseWheelListener)
            map.addMouseWheelListener((MouseWheelListener) this);
        if (this instanceof MouseMotionListener)
            map.addMouseMotionListener((MouseMotionListener) this);
    }

}

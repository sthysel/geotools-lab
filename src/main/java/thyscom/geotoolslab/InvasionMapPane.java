package thyscom.geotoolslab;

/*
 *    GeoTools - The Open Source Java GIS Tookit
 *    http://geotools.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This file is hereby placed into the Public Domain. This means anyone is
 *    free to do whatever they wish with this file. Use it well and enjoy!
 */
import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.net.URL;


import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapPane;

/**
 *
 * @author Michael Bedward
 * @author Thys Meintjes
 */
public class InvasionMapPane extends JMapPane {

    private Alien alien;

    public InvasionMapPane() throws IOException {
        URL url = InvasionMapPane.class.getResource("/mapdata/countries.shp");
        FileDataStore store = FileDataStoreFinder.getDataStore(url);
        FeatureSource featureSource = store.getFeatureSource();

        // Create a map context and add our shapefile to it
        MapContext map = new DefaultMapContext();
        Style style = SLD.createPolygonStyle(Color.BLACK, Color.CYAN, 1.0F);
        map.addLayer(featureSource, style);

        setMapContext(map);
        setRenderer(new StreamingRenderer());
        
    }
    
  
    // OK this is bad - FIXME
    public void setAlien(Alien alien) {
        this.alien = alien;
    }

    // We override the JMapPane paintComponent method so that when
    // the map needs to be redrawn (e.g. after the frame has been
    // resized) the animation is stopped until rendering is complete.
    @Override
    protected void paintComponent(Graphics g) {
        alien.getTimer().stop();
        super.paintComponent(g);
    }

    // We override the JMapPane onRenderingCompleted method to
    // restart the animation after the map has been drawn.
    @Override
    public void onRenderingCompleted() {
        super.onRenderingCompleted();
        alien.spriteBackground = null;
        alien.getTimer().start();
    }
}
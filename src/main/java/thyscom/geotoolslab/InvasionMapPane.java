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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.Timer;

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
 * Simple example of an animated object (known as a 'sprite')
 * moving over a map. The object is a spaceship
 * moving over a map of country boundaries.
 *
 * @author Michael Bedward
 * @author Thys Meintjes
 */
public class InvasionMapPane extends JMapPane {

    private static Alien alien;
    
    // Arbitrary distance to move at each step of the animation
    // in world units.
    // This animation will be driven by a timer which fires
    // every 200 milliseconds. Each time it fires the drawSprite
    // method is called to update the animation
    private Timer animationTimer = new Timer(200, new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            alien.drawSprite();
        }
    });

    // We override the JMapPane paintComponent method so that when
    // the map needs to be redrawn (e.g. after the frame has been
    // resized) the animation is stopped until rendering is complete.
    @Override
    protected void paintComponent(Graphics g) {
        animationTimer.stop();
        super.paintComponent(g);
    }

    // We override the JMapPane onRenderingCompleted method to
    // restart the animation after the map has been drawn.
    @Override
    public void onRenderingCompleted() {
        super.onRenderingCompleted();
        alien.spriteBackground = null;
        animationTimer.start();
    }

    // Main application method
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Animation example");
        InvasionMapPane mapPane = new InvasionMapPane();
        frame.getContentPane().add(mapPane);
        frame.setSize(800, 500);

        URL url = InvasionMapPane.class.getResource("/mapdata/countries.shp");
        FileDataStore store = FileDataStoreFinder.getDataStore(url);
        FeatureSource featureSource = store.getFeatureSource();

        // Create a map context and add our shapefile to it
        MapContext map = new DefaultMapContext();
        Style style = SLD.createPolygonStyle(Color.BLACK, Color.CYAN, 1.0F);
        map.addLayer(featureSource, style);

        mapPane.setMapContext(map);
        mapPane.setRenderer(new StreamingRenderer());

        alien = new Alien(mapPane);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
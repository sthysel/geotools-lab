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
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.swing.JMapPane;
import org.opengis.filter.FilterFactory2;
import org.opengis.style.ContrastMethod;

/**
 *
 * @author Michael Bedward
 * @author Thys Meintjes
 */
public class InvasionMapPane extends JMapPane {

    private Fleet fleet;

    public InvasionMapPane(Fleet fleet) throws IOException {

        this.fleet = fleet;
        MapContext map = new DefaultMapContext();
        
//        
//        URL url = InvasionMapPane.class.getResource("/mapdata/countries.shp");
//        FileDataStore store = FileDataStoreFinder.getDataStore(url);
//        FeatureSource featureSource = store.getFeatureSource();
//        Style style = SLD.createPolygonStyle(Color.BLACK, Color.CYAN, 1.0F);
//        map.addLayer(featureSource, style);

        addTiff(InvasionMapPane.class.getResource("/mapdata/potchmap.tif"), map);
        addTiff(InvasionMapPane.class.getResource("/mapdata/potchphoto.tif"), map);

        setMapContext(map);
        setRenderer(new StreamingRenderer());

    }

    private void addTiff(URL url, MapContext map) {
        AbstractGridFormat pformat = GridFormatFinder.findFormat(url);
        AbstractGridCoverage2DReader preader = pformat.getReader(url);
        map.addLayer(preader, createGreyscaleStyle(1));
    }

    /**
     * Create a Style to display the specified band of the GeoTIFF image
     * as a greyscale layer.
     * <p>
     * This method is a helper for createGreyScale() and is also called directly
     * by the displayLayers() method when the application first starts.
     *
     * @param band the image band to use for the greyscale display
     *
     * @return a new Style instance to render the image in greyscale
     */
    private Style createGreyscaleStyle(int band) {
        StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
        ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NORMALIZE);
        SelectedChannelType sct = sf.createSelectedChannelType(String.valueOf(band), ce);

        RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
        ChannelSelection sel = sf.channelSelection(sct);
        sym.setChannelSelection(sel);

        return SLD.wrapSymbolizers(sym);
    }
    // We override the JMapPane paintComponent method so that when
    // the map needs to be redrawn (e.g. after the frame has been
    // resized) the animation is stopped until rendering is complete.

    @Override
    protected void paintComponent(Graphics g) {
        //fleet.pauseInvasion();
        super.paintComponent(g);
    }

    // We override the JMapPane onRenderingCompleted method to
    // restart the animation after the map has been drawn.
    @Override
    public void onRenderingCompleted() {
        super.onRenderingCompleted();
        //fleet.resumeInvasion();
    }
}
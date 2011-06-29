package thyscom.geotoolsframelab;

import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.swing.JMapFrame;
import java.net.URL;

import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.Style;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.style.ContrastMethod;

/**
 *
 * @author thys
 */
public class MapLab {

    private DefaultMapContext map;

    public static void main(String[] args) throws Exception {
        MapLab lab = new MapLab();
        lab.display();
    }

    private void display() {
        map = new DefaultMapContext();
        addLayer(MapLab.class.getResource("/mapdata/potchmap.tif"), map);
        addLayer(MapLab.class.getResource("/mapdata/potchphoto.tif"), map);

        // Create a JMapFrame with custom toolbar buttons
        JMapFrame mapFrame = new JMapFrame(map);
        mapFrame.enableToolBar(true);
        mapFrame.enableStatusBar(true);


        // Display the map frame. When it is closed the application will exit
        mapFrame.setSize(800, 600);
        mapFrame.setVisible(true);
    }

    private void addLayer(URL url, MapContext map) {
        AbstractGridFormat pformat = GridFormatFinder.findFormat(url);
        AbstractGridCoverage2DReader preader = pformat.getReader(url);
        map.addLayer(preader, createGreyscaleStyle(1));
    }

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
}

package thyscom.geotoolslab;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.apache.batik.transcoder.TranscoderException;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.swing.JMapPane;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author thys
 */
public class Alien {

    // private static final Image SPRITE_IMAGE = new ImageIcon(InvasionMapPane.class.getResource("/svg/air1.svg")).getImage();
    private static Image SPRITE_IMAGE;
    private final double movementDistance = 1.0;
    private final Random rand = new Random();
    private final JMapPane mapPane;
    // X and Y direction of the flying saucer where a value of
    // 1 indicates increasing ordinate and -1 is decreasing
    private int xdir;
    private int ydir;
    // The rectangle (in world coordinates) that defines the flying
    // saucer's current position
    private ReferencedEnvelope spriteEnv;
    public Raster spriteBackground;
    private boolean firstDisplay = true;

    public Alien(InvasionMapPane mapPane) {
        this.mapPane = mapPane;

        try {
            SPRITE_IMAGE = new SVGIcon(this.getClass().getClassLoader().getResource("svg/air1.svg").toString(), 40, 40).getImage();
        } catch (TranscoderException ex) {
            Logger.getLogger(Alien.class.getName()).log(Level.SEVERE, null, ex);
        }
        // SPRITE_IMAGE = loadImageFromDisk( this.getClass().getClassLoader().getResource("sprites/alien.gif"));

        xdir = getRandomDirection();
        ydir = getRandomDirection();
    }

    private Image loadImageFromDisk(URL url) throws IOException {
        BufferedImage sourceImage = ImageIO.read(url);
        // create an accelerated image of the right size to store our sprite in
        Image image = getGraphicsConfiguration().createCompatibleImage(sourceImage.getWidth(), sourceImage.getHeight(), Transparency.BITMASK);
        // draw our source image into the accelerated image
        image.getGraphics().drawImage(sourceImage, 0, 0, null);

        return image;
    }

    private GraphicsConfiguration getGraphicsConfiguration() throws HeadlessException {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }

    private int getRandomDirection() {
        int val = rand.nextInt(2) - 1;
        if (val >= 0) {
            return 1;
        } else {
            return -1;
        }
    }

    // This is the top-level animation method. It erases
    // the sprite (if showing), updates its position and then
    // draws it.
    public void drawSprite() throws IOException {
        if (firstDisplay) {
            setSpritePosition();
            firstDisplay = false;
        }

        Graphics2D g2d = (Graphics2D) mapPane.getGraphics();
        eraseSprite(g2d);
        moveSprite();
        paintSprite(g2d);
    }

    // Erase the sprite by replacing the background map section that
    // was cached when the sprite was last drawn.
    private void eraseSprite(Graphics2D g2d) {
        if (spriteBackground != null) {
            Rectangle rect = spriteBackground.getBounds();

            BufferedImage image = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);
            Raster child = spriteBackground.createChild(rect.x, rect.y, rect.width, rect.height, 0, 0, null);

            image.setData(child);

            g2d.setBackground(mapPane.getBackground());
            g2d.clearRect(rect.x, rect.y, rect.width, rect.height);
            g2d.drawImage(image, rect.x, rect.y, null);
            spriteBackground = null;
        }
    }

    // Update the sprite's location. In this example we are simply
    // moving at 45 degrees to the map edges and bouncing off when an
    // edge is reached.
    private void moveSprite() {
        ReferencedEnvelope displayArea = mapPane.getDisplayArea();

        DirectPosition2D lower = new DirectPosition2D();
        DirectPosition2D upper = new DirectPosition2D();

        double xdelta = 0, ydelta = 0;

        boolean done = false;
        while (!done) {
            lower.setLocation(spriteEnv.getLowerCorner());
            upper.setLocation(spriteEnv.getUpperCorner());

            xdelta = xdir * movementDistance;
            ydelta = ydir * movementDistance;

            lower.setLocation(lower.getX() + xdelta, lower.getY() + ydelta);
            upper.setLocation(upper.getX() + xdelta, upper.getY() + ydelta);

            boolean lowerIn = displayArea.contains(lower);
            boolean upperIn = displayArea.contains(upper);

            if (lowerIn && upperIn) {
                done = true;

            } else if (!lowerIn) {
                if (lower.x < displayArea.getMinX()) {
                    xdir = -xdir;
                } else if (lower.y < displayArea.getMinY()) {
                    ydir = -ydir;
                }

            } else if (!upperIn) {
                if (upper.x > displayArea.getMaxX()) {
                    xdir = -xdir;
                } else if (upper.y > displayArea.getMaxY()) {
                    ydir = -ydir;
                }
            }
        }

        spriteEnv.translate(xdelta, ydelta);
    }

    // Paint the sprite: before displaying the sprite image we
    // cache that part of the background map image that will be
    // covered by the sprite.
    private void paintSprite(Graphics2D g2d) {
        Rectangle bounds = getSpriteScreenPos();
        spriteBackground = mapPane.getBaseImage().getData(bounds);
        g2d.drawImage(SPRITE_IMAGE, bounds.x, bounds.y, null);
    }

    // Set the sprite's intial position
    private void setSpritePosition() throws IOException {
        ReferencedEnvelope worldBounds = null;
        worldBounds = mapPane.getMapContext().getLayerBounds();


        CoordinateReferenceSystem crs = worldBounds.getCoordinateReferenceSystem();
        System.out.println(crs);

        Rectangle screenBounds = mapPane.getVisibleRect();
        int w = SPRITE_IMAGE.getWidth(null);
        int h = SPRITE_IMAGE.getHeight(null);

        int x = screenBounds.x + rand.nextInt(screenBounds.width - w);
        int y = screenBounds.y + rand.nextInt(screenBounds.height - h);

        Rectangle rect = new Rectangle(x, y, w, h);
        AffineTransform tr = mapPane.getScreenToWorldTransform();
        System.out.println(tr);
        Rectangle2D rworld = tr.createTransformedShape(rect).getBounds2D();

        spriteEnv = new ReferencedEnvelope(rworld, crs);
    }

    // Get the position of the sprite as screen coordinates
    private Rectangle getSpriteScreenPos() {
        AffineTransform tr = mapPane.getWorldToScreenTransform();

        Point2D lowerCorner = new Point2D.Double(spriteEnv.getMinX(), spriteEnv.getMinY());
        Point2D upperCorner = new Point2D.Double(spriteEnv.getMaxX(), spriteEnv.getMaxY());

        Point2D p0 = tr.transform(lowerCorner, null);
        Point2D p1 = tr.transform(upperCorner, null);

        Rectangle r = new Rectangle();
        r.setFrameFromDiagonal(p0, p1);
        return r;
    }
}

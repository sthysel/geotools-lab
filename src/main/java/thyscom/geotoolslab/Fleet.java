package thyscom.geotoolslab;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Timer;

/**
 * The fleet of aliens
 * @author thys
 */
public class Fleet {

    private int DELAY = 50;
    private List<Alien> aliens;

    Fleet() {
        aliens = new ArrayList<Alien>();
    }

    /**
     * Register alien
     */
    public void addAlien(Alien alien) {
        aliens.add(alien);
    }

    /**
     * All aliens
     * @return 
     */
    public List<Alien> getFleet() {
        return Collections.unmodifiableList(aliens);
    }
    // Arbitrary distance to move at each step of the animation
    // in world units.
    // This animation will be driven by a timer which fires
    // every 200 milliseconds. Each time it fires the drawSprite
    // method is called to update the animation
    private Timer animationTimer = new Timer(DELAY, new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            animate();
        }
    });

    /**
     * Invasion continues
     */
    public void resumeInvasion() {
        animationTimer.start();
    }

    /**
     * Invasion paused
     */
    public void pauseInvasion() {
        animationTimer.stop();
    }

    private void animate() {
        for (Alien a : aliens) {
            a.drawSprite();
        }
    }
}

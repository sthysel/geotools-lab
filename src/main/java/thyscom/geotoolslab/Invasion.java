package thyscom.geotoolslab;

import java.io.IOException;
import javax.swing.JFrame;
import org.geotools.swing.JMapFrame;

/**
 * Invasion Simulation.
 * @author thys
 */
public class Invasion {

    Invasion() throws IOException {
        JMapFrame frame = new JMapFrame();
        frame.setSize(800, 800);

        Fleet fleet = new Fleet();
        InvasionMapPane mapPane = new InvasionMapPane(fleet);
        // frame.getContentPane().add(mapPane);
        frame.setContentPane(mapPane);



        for (int i = 0; i <= 10; i++) {
            Alien alien = new Alien(mapPane);
            fleet.addAlien(alien);
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        fleet.resumeInvasion();

    }

    public static void main(String[] args) throws Exception {
        Invasion invasion = new Invasion();
    }
}

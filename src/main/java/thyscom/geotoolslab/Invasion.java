package thyscom.geotoolslab;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 * Invasion Simulation.
 * @author thys
 */
public class Invasion {

    Invasion() throws IOException {
        JFrame frame = new JFrame("They will eat the fat people first");
        
        Fleet fleet = new Fleet();
        InvasionMapPane mapPane = new InvasionMapPane(fleet);
        frame.getContentPane().add(mapPane);
        frame.setSize(800, 500);

       
        for(int i = 0; i <= 10; i++) {
            Alien alien = new Alien(mapPane);
            fleet.addAlien(alien);
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    public static void main(String[] args) throws Exception {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    Invasion invasion = new Invasion();
                } catch (IOException ex) {
                    Logger.getLogger(Invasion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}

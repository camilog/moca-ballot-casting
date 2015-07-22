import javax.swing.*;
import java.awt.*;

public class CaptureQRBallot_Reader_swing {

    static public void main(String[] args) {

        JFrame frame = new JFrame("Ballot Casting");

        // What to do when the frame close
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Label emptyLabel = new Label();
        frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);

        frame.pack();

        frame.setVisible(true);

    }


}

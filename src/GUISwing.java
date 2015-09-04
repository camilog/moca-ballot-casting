/*
import javax.swing.*;
import java.awt.*;

public class GUISwing extends JFrame {

    public GUISwing() {
        initComponents();
    }

    private void initComponents() {

        setTitle("Cast Ballot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton configurationButton = new JButton("Configure Bulletin Board address");
        JButton castBallotButton = new JButton("Cast Ballot");

        configurationButton.addActionListener(e -> showConfigurationWindow());

        castBallotButton.addActionListener(e -> showCastBallotWindow());

        setLayout(new GridLayout(2, 1));
        add(configurationButton);
        add(castBallotButton);
        setLocationRelativeTo(null);
        pack();

    }

    private void showConfigurationWindow() {
        JFrame frame = new JFrame("Configure Bulletin Board address");

        JLabel addressLabel = new JLabel("Ingrese dirección del Bulletin Board");
        JTextField addressTextField = new JTextField();

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(e -> {
            String newAddress = addressTextField.getText();

        });
    }

    private void showCastBallotWindow() {

    }

    static public void main(String[] args) {

        EventQueue.invokeLater(() -> new GUISwing().setVisible(true));

    }


}
*/

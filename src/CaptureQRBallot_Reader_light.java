import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.gui.dialog.TextInputDialog;
import com.googlecode.lanterna.screen.Screen;

import java.io.*;

public class CaptureQRBallot_Reader_light extends Window {

    public CaptureQRBallot_Reader_light() {
        super("Ballot Casting");

        // Add button to cast ballot
        addComponent(new Button("Cast Ballot", () -> {
            // Retrieve ID of the voter to verify signature later
            String voterId = TextInputDialog.showTextInputBox(getOwner(), "Voter ID", "Type the ID of the Voter", "", 10);

            // Read QR-Code with the encrypted vote + signature
            String encryptedBallotWithSignature = TextInputDialog.showTextInputBox(getOwner(), "Cast Ballot", "Read QR-Code", "", 1000);

            try {
                // Check signature and upload (cast) encrypted vote
                CaptureQRBallot_Reader_CORE.procedure(voterId, encryptedBallotWithSignature);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Final message in case of success
            MessageBox.showMessageBox(getOwner(), "Finalizado", "Su voto ha sido guardado");
        }));

        // Add button to finalize application
        addComponent(new Button("Exit application", () -> {
            // Close window properly and finalize application
            getOwner().getScreen().clear();
            getOwner().getScreen().refresh();
            getOwner().getScreen().setCursorPosition(0, 0);
            getOwner().getScreen().refresh();
            getOwner().getScreen().stopScreen();
            System.exit(0);
        }));
    }

    static public void main(String[] args) throws IOException {

        // Create window to display options
        CaptureQRBallot_Reader_light myWindow = new CaptureQRBallot_Reader_light();
        GUIScreen guiScreen = TerminalFacade.createGUIScreen();
        Screen screen = guiScreen.getScreen();

        // Start and configuration of the screen
        screen.startScreen();
        guiScreen.showWindow(myWindow, GUIScreen.Position.CENTER);
        screen.refresh();

        // Stopping screen at finalize application
        screen.stopScreen();

    }

}

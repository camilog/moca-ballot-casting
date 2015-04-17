import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.gui.dialog.TextInputDialog;
import com.googlecode.lanterna.screen.Screen;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.Random;

public class CaptureQRBallot_Reader extends Window {

    public CaptureQRBallot_Reader() {
        super("Ballot Casting");

        addComponent(new Button("Cast Ballot", () -> {
            String voterId = TextInputDialog.showTextInputBox(getOwner(), "Voter ID", "Type the ID of the Voter", "", 10);
            String encryptedBallotWithSignature = TextInputDialog.showTextInputBox(getOwner(), "Cast Ballot", "Read QR-Code", "", 1000);

            try {
                procedure(voterId, encryptedBallotWithSignature);
            } catch (IOException e) {
                e.printStackTrace();
            }

            MessageBox.showMessageBox(getOwner(), "Finalizado", "Su voto ha sido guardado");
        }));

        addComponent(new Button("Exit application", () -> {
            // Salirse del window
            getOwner().getScreen().clear();
            getOwner().getScreen().refresh();
            getOwner().getScreen().setCursorPosition(0, 0);
            getOwner().getScreen().refresh();
            getOwner().getScreen().stopScreen();
            System.exit(0);
        }));
    }

    static public void main(String[] args) throws IOException {

        CaptureQRBallot_Reader myWindow = new CaptureQRBallot_Reader();
        GUIScreen guiScreen = TerminalFacade.createGUIScreen();
        Screen screen = guiScreen.getScreen();

        screen.startScreen();
        guiScreen.showWindow(myWindow, GUIScreen.Position.CENTER);
        screen.refresh();
        screen.stopScreen();

    }

    static private void procedure(String voterId, String encryptedBallotWithSignature) throws IOException {

        int sep = Integer.parseInt(encryptedBallotWithSignature.substring(0, 3));

        BigInteger ballot = new BigInteger(encryptedBallotWithSignature.substring(3, sep+3));
        byte[] signature = new BigInteger(encryptedBallotWithSignature.substring(sep+3)).toByteArray();

        // Verify signature
        try {
            if (!verifySign(signature, ballot, voterId)) {
                // Hacer algo si la firma es inválida
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: Verificar que no haya votado dos veces

        int random = new Random().nextInt();
        random = random > 0 ? random : random*-1;
        // TODO: setear random buscando que no haya sido utilizado antes

        ObjectOutputStream encryptedBallotFile;
        File dir1 = new File("ballots");
        dir1.mkdir();

        try {
            encryptedBallotFile = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("ballots/" + random)));
            encryptedBallotFile.writeObject(ballot);
            encryptedBallotFile.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static private boolean verifySign(byte[] sign, BigInteger message, String id) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        PublicKey publicKey;
        ObjectInputStream oin_public = new ObjectInputStream(new BufferedInputStream(new FileInputStream("publicKeys/" + id + "publicKey.key")));
        publicKey = (PublicKey) oin_public.readObject();

        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(publicKey);

        signature.update(message.toByteArray());

        // Se comprueba la veracidad de la firma
        return signature.verify(sign);

    }

}

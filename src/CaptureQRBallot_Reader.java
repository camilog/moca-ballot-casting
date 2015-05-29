import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.gui.dialog.TextInputDialog;
import com.googlecode.lanterna.screen.Screen;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CaptureQRBallot_Reader extends Window {

    private static final String bbServer = "http://cjgomez.duckdns.org:3000/ballots";
    private static final String publicKeysServer = "http://cjgomez.duckdns.org:3000/public_keys";

    public CaptureQRBallot_Reader() {
        super("Ballot Casting");

        // Add button to cast ballot
        addComponent(new Button("Cast Ballot", () -> {
            // Retrieve ID of the voter to verify signature later
            String voterId = TextInputDialog.showTextInputBox(getOwner(), "Voter ID", "Type the ID of the Voter", "", 10);

            // Read QR-Code with the encrypted vote + signature
            String encryptedBallotWithSignature = TextInputDialog.showTextInputBox(getOwner(), "Cast Ballot", "Read QR-Code", "", 1000);

            try {
                // Check signature and upload (cast) encrypted vote
                procedure(voterId, encryptedBallotWithSignature);
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
        CaptureQRBallot_Reader myWindow = new CaptureQRBallot_Reader();
        GUIScreen guiScreen = TerminalFacade.createGUIScreen();
        Screen screen = guiScreen.getScreen();

        // Start and configuration of the screen
        screen.startScreen();
        guiScreen.showWindow(myWindow, GUIScreen.Position.CENTER);
        screen.refresh();

        // Stopping screen at finalize application
        screen.stopScreen();

    }

    static private void procedure(String voterId, String encryptedBallotWithSignature) throws IOException {

        // Separate text from ballot in: length of ballot(sep) + ballot + signature, and create BigInteger ballot and byte[] signature
        int sep = Integer.parseInt(encryptedBallotWithSignature.substring(0, 3));
        String encryptedBallotString = encryptedBallotWithSignature.substring(3, sep+3);
        String signatureString = encryptedBallotWithSignature.substring(sep+3);

        // Create the BigInteger ballot and byte[] signature
        BigInteger ballot = new BigInteger(encryptedBallotString);
        byte[] signature = new BigInteger(signatureString).toByteArray();

        // Download Public Key of the voter from the BB
        String publicKeyString = downloadPublicKey(publicKeysServer, voterId);

        // Verify signature
        /*try {
            if (!verifySign(signature, ballot, voterId)) {
                // TODO: Do something if signature is invalid
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        // TODO: Verify that the voter hadn't had casted already a ballot

        // Asign random number to the vote casted
        // int random = new Random().nextInt();
        // random = random > 0 ? random : random*-1;
        // TODO: set random to a number never used before

        // Create directory for ballots if it's not created
        // File dir1 = new File("ballots");
        // dir1.mkdir();

        // Save ballot in a file with a random number as name
        /*
        ObjectOutputStream encryptedBallotFile;
        try {
            encryptedBallotFile = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("ballots/" + random)));
            encryptedBallotFile.writeObject(ballot);
            encryptedBallotFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        // Upload (cast) to the BB of the ballot
        upload(bbServer, voterId, encryptedBallotString, signatureString);

    }

    // Function to verify signature within the ballot
    static private boolean verifySign(byte[] sign, BigInteger message, String id) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {

        // Download Public Key of the voter from the BB
        String publicKeyString = downloadPublicKey(publicKeysServer, id);

        // Decodify the String and create the variable PublicKey
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString.getBytes("utf-8"));
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory publicKeyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = publicKeyFactory.generatePublic(publicSpec);

        // Retrieve public key of the voter from the folder publicKeys/
        // ObjectInputStream oin_public = new ObjectInputStream(new BufferedInputStream(new FileInputStream("publicKeys/" + id + "publicKey.key")));
        // PublicKey publicKey = (PublicKey) oin_public.readObject();

        // Set-up scheme of signature, this case is SHA256 with RSA
        Signature signature = Signature.getInstance("SHA256withRSA");

        // Set-up of the verification of the signature, giving the public key and the message
        signature.initVerify(publicKey);
        signature.update(message.toByteArray());

        // Checks the veracity of the signature, returning true or false
        return signature.verify(sign);

    }

    // Retrieve JSON of the PublicKey from the BB
    static private String downloadPublicKey(String publicKeyServer, String voterId) throws IOException {
        // Set the URL to GET the public key of the voterId
        String url = publicKeyServer + "/voter/" + voterId;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Add request header
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.getResponseCode();

        // Receive the response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // TODO: Process the response as a JSON object

        return "TODO";

    }

    // Upload of the ballot as a JSON to the bbServer
    static private void upload(String bbServer, String voterId, String encryption, String signature) throws IOException {
        // Set the URL where to POST the ballot
        URL obj = new URL(bbServer);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        // Create JSON with the parameters
        String urlParameters = "{\"ballot\":{\"voter\":" + voterId + ",\"enc\":" + encryption + ",\"sign\":" + signature + "}}";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        con.getResponseCode();
    }

}

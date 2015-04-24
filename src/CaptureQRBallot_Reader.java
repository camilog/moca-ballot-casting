import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.gui.Window;
import com.googlecode.lanterna.gui.component.Button;
import com.googlecode.lanterna.gui.dialog.MessageBox;
import com.googlecode.lanterna.gui.dialog.TextInputDialog;
import com.googlecode.lanterna.screen.Screen;

import java.io.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.*;
import java.util.Random;

public class CaptureQRBallot_Reader extends Window {

    public CaptureQRBallot_Reader() {
        super("Ballot Casting");

        // Add button to cast ballot
        addComponent(new Button("Cast Ballot", () -> {
            // Retrieve ID of the voter to verify signature later
            String voterId = TextInputDialog.showTextInputBox(getOwner(), "Voter ID", "Type the ID of the Voter", "", 10);

            // Read QR-Code with the encrypted ballot + signature
            String encryptedBallotWithSignature = TextInputDialog.showTextInputBox(getOwner(), "Cast Ballot", "Read QR-Code", "", 1000);

            try {
                // Check signature and store (cast) encrypted ballot
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
        BigInteger ballot = new BigInteger(encryptedBallotWithSignature.substring(3, sep+3));
        byte[] signature = new BigInteger(encryptedBallotWithSignature.substring(sep+3)).toByteArray();

        // Verify signature
        try {
            if (!verifySign(signature, ballot, voterId)) {
                // TODO: Do something if signature is invalid
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: Verify that the voter hadn't had casted already a ballot

        // Asign random number to the vote casted
        int random = new Random().nextInt();
        random = random > 0 ? random : random*-1;
        // TODO: set random to a number never used before

        // Create directory for ballots if it's not created
        File dir1 = new File("ballots");
        dir1.mkdir();

        // Save ballot in a file with a random number as name
        ObjectOutputStream encryptedBallotFile;
        try {
            encryptedBallotFile = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("ballots/" + random)));
            encryptedBallotFile.writeObject(ballot);
            encryptedBallotFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Upload to the BB of the ballot just casted
        File ballotFile = new File("ballots/" + random);
        upload("172.30.65.162:12345", "user", "pass", "ballots/ballot_" + random, ballotFile);

    }

    // Function to verify signature within the ballot
    static private boolean verifySign(byte[] sign, BigInteger message, String id) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        // Retrieve public key of the voter from the folder publicKeys/
        ObjectInputStream oin_public = new ObjectInputStream(new BufferedInputStream(new FileInputStream("publicKeys/" + id + "publicKey.key")));
        PublicKey publicKey = (PublicKey) oin_public.readObject();

        // Set-up scheme of signature, this case is SHA256 with RSA
        Signature signature = Signature.getInstance("SHA256withRSA");

        // Set-up of the verification of the signature, giving the public key and the message
        signature.initVerify(publicKey);
        signature.update(message.toByteArray());

        // Checks the veracity of the signature, returning true or false
        return signature.verify(sign);

    }

    // Upload of the file
    /**
     * Upload a file to a FTP server. A FTP URL is generated with the
     * following syntax:
     * ftp://user:password@host:port/filePath;type=i.
     *
     * @param ftpServer , FTP server address (optional port ':portNumber').
     * @param user , Optional user name to login.
     * @param password , Optional password for user.
     * @param fileName , Destination file name on FTP server (with optional
     *            preceding relative path, e.g. "myDir/myFile.txt").
     * @param source , Source file to upload.
     * @throws IOException on error.
     */
    public static void upload( String ftpServer, String user, String password,
                        String fileName, File source ) throws IOException
    {
        if (ftpServer != null && fileName != null && source != null)
        {
            StringBuffer sb = new StringBuffer( "ftp://" );
            // check for authentication else assume its anonymous access.
            if (user != null && password != null)
            {
                sb.append( user );
                sb.append( ':' );
                sb.append( password );
                sb.append( '@' );
            }
            sb.append( ftpServer );
            sb.append( '/' );
            sb.append( fileName );
         /*
          * type ==&gt; a=ASCII mode, i=image (binary) mode, d= file directory
          * listing
          */
            sb.append( ";type=i" );

            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try
            {
                URL url = new URL( sb.toString() );
                URLConnection urlc = url.openConnection();

                bos = new BufferedOutputStream( urlc.getOutputStream() );
                bis = new BufferedInputStream( new FileInputStream( source ) );

                int i;
                // read byte by byte until end of stream
                while ((i = bis.read()) != -1)
                {
                    bos.write( i );
                }
            }
            finally
            {
                if (bis != null)
                    try
                    {
                        bis.close();
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                if (bos != null)
                    try
                    {
                        bos.close();
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
            }
        }
        else
        {
            System.out.println( "Input not available." );
        }
    }


}

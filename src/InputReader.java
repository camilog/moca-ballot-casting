import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class InputReader {

    private static String bulletinBoardAddress = "";
    private static String ballotsSubDomain = "/ballots";
    private static final String votersPublicKeysSubDomain = "/voters_public_keys";

    // TODO: Implement user and pass verification in order to upload the voter public key
    // private static String user, pass;

    // Main procedure if the casting process
    static protected void procedure(String voterId, String encryptedBallotWithSignature) throws IOException {

        // Separate text from ballot in: encryptedVote and signature
        String[] encryptionAndSignature = separateBallot(encryptedBallotWithSignature);
        String encryptedVoteString = encryptionAndSignature[0];
        String signatureString = encryptionAndSignature[1];

        // Create the BigInteger ballot and byte[] signature
        BigInteger ballot = new BigInteger(encryptedVoteString);
        byte[] signature = new BigInteger(signatureString).toByteArray();

        // Verify signature
        try {
            if (!verifySignature(signature, ballot, voterId)) {
                // TODO: Do something if signature is invalid
                System.out.println("Invalid Signature");
                System.exit(0);
            }
        } catch (Exception e) {e.printStackTrace();}

        // TODO: Verify that the voter did not casted already a ballot

        // Upload (cast) of the ballot to the BB
        uploadBallot(voterId, encryptedVoteString, signatureString);

    }

    // Separate the read ballot into encrypted vote and signature
    private static String[] separateBallot(String encryptedBallotWithSignature) {
        return encryptedBallotWithSignature.split("#");
    }

    // Function to verify signature within the ballot
    static private boolean verifySignature(byte[] ballotSignature, BigInteger message, String id) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {

        // Download Public Key of the voter from the BB
        String publicKeyString = downloadPublicKey(id);

        // Decode the String and create the variable PublicKey
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString.getBytes("utf-8"));
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory publicKeyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = publicKeyFactory.generatePublic(publicSpec);

        // Set-up scheme of signature, this case is SHA256 with RSA
        Signature signature = Signature.getInstance("SHA256withRSA");

        // Set-up of the verification of the signature, giving the public key and the message
        signature.initVerify(publicKey);
        signature.update(message.toByteArray());

        // Checks the veracity of the ballot's signature, returning true or false
        return signature.verify(ballotSignature);

    }

    // Download public key of the voter from the BB
    static private String downloadPublicKey(String voterId) throws IOException {

        // Set the URL to GET the public key of the voterId
        URL obj = new URL(bulletinBoardAddress + votersPublicKeysSubDomain + "/" + voterId);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Add request header
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.getResponseCode();

        // Receive the response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Serialize the JSON response to an Object (AuthorityPublicKeyResponse)
        String jsonString = response.toString();
        Gson gson = new Gson();
        VoterPublicKeyResponse voterPublicKeyResponse = gson.fromJson(jsonString, VoterPublicKeyResponse.class);

        return voterPublicKeyResponse.value;

    }

    // Upload of the ballot as a JSON to the bbServer
    static private void uploadBallot(String voterId, String encryption, String signature) throws IOException {
        // Set the URL where to POST the ballot
        URL obj = new URL(bulletinBoardAddress + ballotsSubDomain);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        // Create JSON with the parameters
        String urlParameters = "{\"voter_id\":" + voterId + ",\"encrypted_vote\":" + encryption + ",\"signature\":" + signature + "}";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        con.getResponseCode();
    }

    // Function to set up the bulletin board address
    protected static void setBBAddress(String newAddress) {
        bulletinBoardAddress = newAddress;
    }

    // Function to retrieve the bulletin board address
    protected static String getBBAddress() {
        return bulletinBoardAddress;
    }

}

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.Random;

public class CaptureQRBallot_Reader {

    static public void main(String[] args) throws IOException {
        System.out.println("Captura del voto electrónico, previa verificación de la firma.\n");

        System.out.print("Ingrese el RUT (id) del votante: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String voterId = br.readLine();

        System.out.println("Acerque Código QR del voto encriptado a la camara.");
        procedure(voterId);

        System.out.println("\nVoto registrado exitosamente en ballots/");
        System.out.println("Pedir al votante que deposite voto plano en la urna");
        System.out.println("Proceso Finalizado");
    }

    static private void procedure(String voterId) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String encryptedBallotWithSignature = br.readLine();

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

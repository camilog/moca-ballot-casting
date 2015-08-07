import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class InputCamera extends JFrame implements Runnable, ThreadFactory {

    private static final long serialVersionUID = 6441489157408381878L;

    private Executor executor = Executors.newSingleThreadExecutor(this);
    private Webcam webcam;
    private WebcamPanel panel;

    private String id;

    public InputCamera(String id) {
        super();
        this.id = id;
        setLayout(new FlowLayout());
        setTitle("Read QR / Bar Code With Webcam");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension size = WebcamResolution.VGA.getSize();

        webcam = Webcam.getWebcams().get(1);
        webcam.setViewSize(size);

        panel = new WebcamPanel(webcam);
        panel.setPreferredSize(size);

        add(panel);
        pack();
        setVisible(true);
        executor.execute(this);
    }

    @Override
    public void run() {
        while(true) {

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            com.google.zxing.Result result = null;
            BufferedImage image;

            if (webcam.isOpen()) {

                if ((image = webcam.getImage()) == null)
                    continue;

                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                try {
                    result = new MultiFormatReader().decode(bitmap);
                } catch (NotFoundException e) {
                    // There is no QR Code in the image
                }

            }

            if (result != null) {

                // Almacenar voto
                String ballotWithSignature = result.toString();
                int sep = Integer.parseInt(ballotWithSignature.substring(0, 3));

                BigInteger ballot = new BigInteger(ballotWithSignature.substring(3, sep+3));
                byte[] signature = new BigInteger(ballotWithSignature.substring(sep+3)).toByteArray();

                // Verify signature
                try {
                    if (!verifySign(signature, ballot, id)) {
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

                ObjectOutputStream ballotFile;
                File dir1 = new File("ballots");
                dir1.mkdir();

                try {
                    ballotFile = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("ballots/" + random)));
                    ballotFile.writeObject(ballot);
                    ballotFile.close();
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

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

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "example-runner");
        t.setDaemon(true);
        return t;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Captura del voto electrónico, previa verificación de la firma.\n");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Ingrese el RUT (id) del votante: ");
        String id = br.readLine();
        System.out.println("Acerque Código QR del voto encriptado a la camara.");
        new InputCamera(id);

        // TODO: esperar que terminen todos los threads

        System.out.println("\nVoto registrado exitosamente en ballots/");
        System.out.println("Pedir al votante que deposite voto plano en la urna");
        System.out.println("Proceso Finalizado");
    }

}

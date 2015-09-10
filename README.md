# Ballot Casting (moca-ballot-casting)
Fifth part of the [*MoCa QR*](http://mocaqr.niclabs.cl) Voting System project.

Records and uploads the encrypted ballot of the selection of the voter to the Bulletin Board server, subsequently to verify the signature of the voter.

## Files
1. **InputReader.java**: Main class of the program which uses as input device a QR-codes reader. There are all the logic and methods for the recording of the QR-code, download the public information, verify the signature, and finally upload the encrypted value to the Bulletin Board server.

2. **InputCamera.java**: Main class of the program which uses as input device a camera. There are all the logic and methods for the recording of the QR-code, download the public information, verify the signature, and finally upload the encrypted value to the Bulletin Board server.

3. **GUIJavaFX.java**: Class that manages the JavaFX GUI environment. his environment also needs the presence of the following files: mainWindow.fxml, configWindow.fxml, castingWindow.fxml, MainWindowController.java, ConfigWindowController.java, CastingWindowController.java, javaFx.css and background.jpg.

4. **VoterPublicKeyResponse.java**: Class for the creation of the Voter Public Key object after the retrieving of the JSON from the Bulletin Board server.

5. **VoterPublicKey.java**: Class for the creation of the object after the retrieving of the JSON from the Bulletin Board server.

## External Libraries
1. **[ZXing](https://github.com/zxing/zxing)**: Java library for the 1D/2D barcode image processing.
2. **[Gson](https://github.com/google/gson)**: Java library to convert Java Object to their JSON representation and viceversa.
3. **[Webcam Capture API](https://github.com/sarxos/webcam-capture)**: Java library to access directly from code to build-in or connected via USB cameras.

## How to Use
* Download the .jar file [here](https://github.com/CamiloG/moca_qr/blob/master/Precinct_Apps/CastBallot_light.jar?raw=true).
* Put the file castBallot.jar in the project folder.
* Execute ballotVerification.jar with `$ java -jar castBallot.jar`

### Configuration
* First of all you have to configure the root address for the Bulletin Board server. Select 'Configure Bulletin Board address' and introduce the address.
* The address is now shown on the top box of the window.

### Casting Process (using InputReader)
* Select 'Ballot Casting'.
* First, the program asks for the voter id who is casting the ballot.
* Next, the program asks to read the encrypted QR-code value.
* The program downloads the public information of the voter and verifies the signature. 
* If success, the program uploads the encrypted ballot to the Bulletin Board server.
* After this, the program finishes, waiting for the next voter to cast her ballot.

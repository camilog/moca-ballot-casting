# CastBallot
Fifth part of the [*MoCa QR*](https://github.com/CamiloG/moca_qr) Voting System project.

Records and uploads the encrypted ballot of the selection of the voter to the Bulletin Board server, subsequently to verify the signature of the voter.

## Files
1. **InputReader.java**: Main class of the program which uses as input device a QR-codes reader. There are all the logic and methods for the recording of the QR-code, download the public information, verify the signature, and finally upload the encrypted value to the Bulletin Board server.

2. **GUILanterna.java**: Class that manages the Lanterna GUI environment, made to run on console-text-only devices (Raspberry PI for example).

3. **GUISwing.java**: Class that manages the Java-Swing GUI environment, for all the devices that can run a graphics interface.

4. **InputCamera.java**: Main class of the program which uses as input device a camera. There are all the logic and methods for the recording of the QR-code, download the public information, verify the signature, and finally upload the encrypted value to the Bulletin Board server.

5. **VoterPublicKey.java**: Class for the creation of the object after the retrieving of the JSON from the Bulletin Board server.

## How to Use
* Download the .jar file [here](https://github.com/CamiloG/moca_qr/blob/master/Precinct_Apps/CastBallot_light.jar?raw=true).
* Put the file castBallot.jar in the project folder.
* Execute ballotVerification.jar with `$ java -jar castBallot.jar`

### Configuration
* First of all you have to configure the root address for the Bulletin Board server. Select 'Configure Bulletin Board address' and introduce the address.
* The address is now shown on the top box of the window.

### Casting Process
* Select 'Ballot Casting'.
* First, the program asks for the voter id who is casting the ballot.
* Next, the program asks to read the encrypted QR-code value.
* The program downloads the public information of the voter and verifies the signature. 
* If success, the program uploads the encrypted ballot to the Bulletin Board server.
* After this, the program finishes, waiting for the next voter to cast her ballot.
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.io.IOException;

public class CastingWindowController {

    @FXML
    private TextField voter_id, encryption_and_signature;

    @FXML
    public void handleParametersReadyButtonAction(ActionEvent actionEvent) throws IOException {

        String voterId = voter_id.getText();
        String encryptionAndSignature = encryption_and_signature.getText();
        InputReader.procedure(voterId, encryptionAndSignature);

        // Close window
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();

    }
}

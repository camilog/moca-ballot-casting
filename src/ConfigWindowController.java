import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;

public class ConfigWindowController {

    @FXML
    private TextField new_bb_address_textfield;

    @FXML
    protected void handleNewBBAddressButtonAction(ActionEvent actionEvent) {

        // Retrieve new BB address
        String newBBAddress = new_bb_address_textfield.getText();

        // Set new BB address
        InputReader.setBBAddress(newBBAddress);

        // TODO: Update label showing BB address

        // Close window
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();

    }

}

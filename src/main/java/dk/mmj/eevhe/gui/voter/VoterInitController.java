package dk.mmj.eevhe.gui.voter;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

import static dk.mmj.eevhe.gui.Utilities.isBulletinBoard;

public class VoterInitController {
    @FXML
    public TextField idInput;
    @FXML
    public TextField bbAddress;
    @FXML
    public Button configure;

    public Consumer<ConfigurationTuple> onFinish;

    @FXML
    public void initialize() {
        configure.setOnAction(event -> {
            final boolean isValid = isBulletinBoard(bbAddress.getText());

            if (!isValid) {
                final Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Configuration Error");
                alert.setTitle("Invalid BulletinBoard");
                alert.setContentText("The supplied address does not point at a BulletinBoard. Please verify the address.");
                alert.show();
                event.consume();
            } else {
                onFinish.accept(new ConfigurationTuple(idInput.getText(), bbAddress.getText()));
            }
        });

    }

    public void setOnFinish(Consumer<ConfigurationTuple> onFinish) {
        this.onFinish = onFinish;
    }

    public void disableId() {
        idInput.setDisable(true);
    }

    public static class ConfigurationTuple {
        private final String id;
        private final String address;

        public ConfigurationTuple(String id, String address) {
            this.id = id;
            this.address = address;
        }

        public String getId() {
            return id;
        }

        public String getAddress() {
            return address;
        }
    }

}

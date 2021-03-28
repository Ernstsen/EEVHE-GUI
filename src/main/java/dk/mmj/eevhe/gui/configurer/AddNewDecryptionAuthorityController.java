package dk.mmj.eevhe.gui.configurer;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class AddNewDecryptionAuthorityController {

    @FXML
    public TextField addressInput;
    @FXML
    public TextField idInput;
    @FXML
    public Button add;
    @FXML
    public Button cancel;

    private Consumer<ConfigurerManager.DAInfo> callback;
    private Runnable onCancel;

    @FXML
    public void initialize() {
        add.setOnAction(event -> {
            final String idText = idInput.getText();
            final String address = addressInput.getText();
            if (idText == null || idText.isEmpty()
                    || address == null || address.isEmpty()) {
                final Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Creation Error");
                alert.setTitle("Failed to create Decryption Authority");
                alert.setContentText("Must supply both Id and Address");
                alert.show();
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idText);
            } catch (NumberFormatException e) {
                final Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Creation Error");
                alert.setTitle("Failed to create Decryption Authority");
                alert.setContentText("Id must be an integer");
                alert.show();
                return;
            }
            callback.accept(new ConfigurerManager.DAInfo(id, address));
        });
        cancel.setOnAction(event -> onCancel.run());
    }

    void onSave(Consumer<ConfigurerManager.DAInfo> callback) {
        this.callback = callback;
    }

    void onCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }
}

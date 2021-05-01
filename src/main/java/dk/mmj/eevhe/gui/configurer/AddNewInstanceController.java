package dk.mmj.eevhe.gui.configurer;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class AddNewInstanceController {

    @FXML
    public TextField addressInput;
    @FXML
    public TextField idInput;
    @FXML
    public Button add;
    @FXML
    public Button cancel;
    @FXML
    public ChoiceBox<InstanceType> typeChoice;

    private Consumer<ConfigurerManager.InstanceInfo> callback;
    private Runnable onCancel;

    @FXML
    public void initialize() {
        typeChoice.getItems().add(InstanceType.DA);
        typeChoice.getItems().add(InstanceType.BB);
        typeChoice.getSelectionModel().selectFirst();

        add.setOnAction(event -> {
            final String idText = idInput.getText();
            final String address = addressInput.getText();
            if (idText == null || idText.isEmpty()
                    || address == null || address.isEmpty() || typeChoice.getValue() == null) {
                final Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Creation Error");
                alert.setTitle("Failed to create Decryption Authority");
                alert.setContentText("Must supply both Id, Address and type");
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
            callback.accept(new ConfigurerManager.InstanceInfo(id, address, typeChoice.getValue()));
        });
        cancel.setOnAction(event -> onCancel.run());
    }

    void onSave(Consumer<ConfigurerManager.InstanceInfo> callback) {
        this.callback = callback;
    }

    void onCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }
}

package dk.mmj.eevhe.gui.configurer;

import dk.mmj.eevhe.entities.Candidate;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class AddNewCandidateController {


    @FXML
    public Label idxLabel;
    @FXML
    public TextField nameInput;
    @FXML
    public TextField description;
    @FXML
    public Button cancel;
    @FXML
    public Button add;

    private Integer idx;
    private Consumer<Candidate> callback;
    private Runnable onCancel;

    @FXML
    public void initialize() {
        add.setOnAction(event -> {
            final String nameText = nameInput.getText();
            final String descriptionText = description.getText();
            if (nameText == null || nameText.isEmpty()
                    || descriptionText == null || descriptionText.isEmpty()) {
                final Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Creation Error");
                alert.setTitle("Failed to create Candidate");
                alert.setContentText("Must supply both name and description");
                alert.show();
                return;
            }

            callback.accept(new Candidate(idx, nameText, descriptionText));
        });
        cancel.setOnAction(event -> onCancel.run());
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
        idxLabel.setText(idx.toString());
    }

    void onSave(Consumer<Candidate> callback) {
        this.callback = callback;
    }

    void onCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }
}

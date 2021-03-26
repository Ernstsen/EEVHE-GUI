package dk.mmj.eevhe.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ChooserController {

    @FXML
    public Button configureButton;

    @FXML
    public Button voteButton;

    @FXML
    public Button fetchButton;

    @FXML
    public void initialize() {
        configureButton.setOnAction(event -> {
            setAllInactive();
            GUI.INSTANCE.setAction(Action.CONFIGURE);
        });
        voteButton.setOnAction(event -> {
            setAllInactive();
            GUI.INSTANCE.setAction(Action.VOTE);
        });
        fetchButton.setOnAction(event -> {
            setAllInactive();
            GUI.INSTANCE.setAction(Action.FETCH);
        });

    }

    private void setAllInactive() {
        configureButton.setDisable(true);
        voteButton.setDisable(true);
        fetchButton.setDisable(true);
    }

}

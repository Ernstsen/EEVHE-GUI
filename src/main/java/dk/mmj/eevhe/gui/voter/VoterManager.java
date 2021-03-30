package dk.mmj.eevhe.gui.voter;

import dk.mmj.eevhe.entities.Candidate;
import dk.mmj.eevhe.gui.Manager;
import dk.mmj.eevhe.gui.Utilities;
import dk.mmj.eevhe.gui.wrappers.VoterWrapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

import static dk.mmj.eevhe.gui.Utilities.showDialogueAndWait;

public class VoterManager implements Manager {

    @FXML
    public TableView<Candidate> candidateTable;
    @FXML
    public TableColumn<Integer, Integer> candidateIdx;
    @FXML
    public TableColumn<Integer, String> candidateName;
    @FXML
    public TableColumn<Integer, String> candidateDescription;


    private VoterWrapper wrapper;
    private Stage parentStage;

    @FXML
    public void initialize() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VoterInit.fxml"));

        try {
            final Parent voterInitParent = loader.load();
            final VoterInitController controller = loader.getController();
            final Stage dialogue = new Stage();
            controller.setOnFinish(tuple -> {
                wrapper = new VoterWrapper(tuple.getAddress(), tuple.getId());
                dialogue.close();
            });
            Platform.runLater(() -> {
                showDialogueAndWait(parentStage, voterInitParent, dialogue);

                if (wrapper == null) {
                    parentStage.close();
                    return;
                }

                ObservableList<Candidate> candidates = FXCollections.observableArrayList();
                candidates.addAll(wrapper.getCandidates());
                Utilities.populateCandidateTable(candidateTable, candidates, candidateIdx, candidateName, candidateDescription);

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void manage(Stage parentStage) {
        this.parentStage = parentStage;
        try {
            Parent root = FXMLLoader.load(getClass().getResource("VoterMain.fxml"));
            parentStage.setScene(new Scene(root));
            parentStage.centerOnScreen();
            parentStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource", e);
        }
    }
}

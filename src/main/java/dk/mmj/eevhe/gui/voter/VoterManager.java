package dk.mmj.eevhe.gui.voter;

import dk.mmj.eevhe.entities.Candidate;
import dk.mmj.eevhe.gui.Manager;
import dk.mmj.eevhe.gui.Utilities;
import dk.mmj.eevhe.gui.wrappers.VoteFailedException;
import dk.mmj.eevhe.gui.wrappers.VoterWrapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

import static dk.mmj.eevhe.gui.Utilities.handleUnexpectedException;
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
    @FXML
    public Button voteButton;


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

        voteButton.setOnAction(event -> {
            final TableView.TableViewSelectionModel<Candidate> selectionModel = candidateTable.getSelectionModel();
            final Candidate chosenCandidate = selectionModel.getSelectedItem();

            if (chosenCandidate == null) {
                handleUnexpectedException("Please select a candidate", "No candidate selected");
                event.consume();
                return;
            }

            final Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Are you sure you want to cast your vote for " + chosenCandidate.getName(),
                    ButtonType.OK,
                    ButtonType.NO);
            alert.setHeaderText("Please verify");
            alert.setTitle("Very your candidate");

            ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setOnAction(e -> {
                boolean failed = false;
                try {
                    wrapper.vote(chosenCandidate.getIdx());
                } catch (VoteFailedException voteFailedException) {
                    failed = true;
                    Utilities.handleUnexpectedException(voteFailedException, "Failed to cast vote");
                }
                if (!failed) {
                    final Alert voteCast = new Alert(Alert.AlertType.INFORMATION);
                    voteCast.setTitle("Success");
                    voteCast.setContentText("You have successfully voted for " + chosenCandidate.getName());
                    voteCast.setHeaderText("You voted!");
                    ((Button) voteCast.getDialogPane().lookupButton(ButtonType.OK)).setOnAction(action -> {
                        parentStage.close();
                    });
                    voteCast.show();
                }
            });
            alert.show();
        });
    }

    @Override
    public void manage(Stage parentStage) {
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("VoterMain.fxml"));
            Parent root = loader.load();
            final VoterManager controller = loader.getController();
            controller.parentStage = parentStage;
            parentStage.setScene(new Scene(root));
            parentStage.centerOnScreen();
            parentStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource", e);
        }
    }
}

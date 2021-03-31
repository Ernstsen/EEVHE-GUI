package dk.mmj.eevhe.gui.fetcher;

import dk.mmj.eevhe.entities.Candidate;
import dk.mmj.eevhe.gui.Manager;
import dk.mmj.eevhe.gui.Utilities;
import dk.mmj.eevhe.gui.voter.VoterInitController;
import dk.mmj.eevhe.gui.wrappers.ResultFetcherWrapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

import static dk.mmj.eevhe.gui.Utilities.showDialogueAndWait;

public class FetcherManager implements Manager {

    private final ObservableList<ResultFetcherWrapper.CandidateWithResult> candidates = FXCollections.observableArrayList();

    @FXML
    public TableView<ResultFetcherWrapper.CandidateWithResult> candidateTable;
    @FXML
    public TableColumn<Integer, Integer> candidateIdx;
    @FXML
    public TableColumn<Integer, String> candidateName;
    @FXML
    public TableColumn<Integer, String> candidateDescription;
    @FXML
    public TableColumn<Integer, Integer> candidateVotesInt;
    @FXML
    public TableColumn<Integer, Float> candidateVotesPercentage;
    private Stage parentStage;
    private ResultFetcherWrapper wrapper;

    @FXML
    public void initialize() {
        FXMLLoader loader = new FXMLLoader(VoterInitController.class.getResource("/layout/voter/VoterInit.fxml"));

        try {
            final Parent voterInitParent = loader.load();
            final VoterInitController controller = loader.getController();
            final Stage dialogue = new Stage();
            controller.setOnFinish(tuple -> {
                wrapper = new ResultFetcherWrapper(tuple.getAddress());
                dialogue.close();
            });
            controller.disableId();

            Platform.runLater(() -> {
                showDialogueAndWait(parentStage, voterInitParent, dialogue);

                final Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Please wait");
                alert.setGraphic(new ProgressIndicator());
                alert.setHeaderText("Loading results");
                alert.setContentText("Downloading and verifying election results");
                alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);

                if (wrapper == null) {
                    parentStage.close();
                    return;
                }

                alert.show();

                for (Field declaredField : Candidate.class.getDeclaredFields()) {
                    declaredField.setAccessible(true);
                }

                Arrays.stream(ResultFetcherWrapper.CandidateWithResult.class.getFields()).forEach(f -> f.setAccessible(true));

                final SortedList<ResultFetcherWrapper.CandidateWithResult> sortedCandidates = new SortedList<>(candidates);
                sortedCandidates.comparatorProperty().bind(candidateTable.comparatorProperty());
                candidateTable.setItems(sortedCandidates);

                candidateIdx.setCellValueFactory(new PropertyValueFactory<>("idx"));
                candidateName.setCellValueFactory(new PropertyValueFactory<>("name"));
                candidateDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
                candidateVotesInt.setCellValueFactory(new PropertyValueFactory<>("votes"));
                candidateVotesPercentage.setCellValueFactory(new PropertyValueFactory<>("votesPercentage"));

                new Thread(() -> {
                    final ResultFetcherWrapper.CompleteElectionResult results = wrapper.getResults();
                    if (results == null) {
                        Platform.runLater(() -> {
                            alert.close();
                            parentStage.close();
                            Utilities.handleUnexpectedException("Failed to retrieve election results", "Error");
                        });
                    } else {
                        candidates.addAll(results.getCandidateResults());
                        Platform.runLater(alert::close);
                    }
                }).start();

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void manage(Stage parentStage) {
        try {
            final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layout/fetcher/FetcherMain.fxml"));
            Parent root = fxmlLoader.load();
            final FetcherManager controller = fxmlLoader.getController();
            controller.parentStage = parentStage;
            parentStage.setScene(new Scene(root));
            parentStage.centerOnScreen();
            parentStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource", e);
        }
    }
}

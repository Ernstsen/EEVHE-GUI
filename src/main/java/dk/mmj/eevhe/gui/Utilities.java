package dk.mmj.eevhe.gui;

import dk.mmj.eevhe.client.Voter;
import dk.mmj.eevhe.entities.Candidate;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Field;

public class Utilities {

    /**
     * Displays a dialogue with input to table, waits for finish and refreshes table
     *
     * @param parentStage parent stage to draw dialogue in
     * @param parent      parent for scene (highest order node to be displayed)
     * @param dialogue    stage containing the dialogue
     */
    public static void showDialogueAndWait(Stage parentStage, Parent parent, Stage dialogue) {
        dialogue.initModality(Modality.APPLICATION_MODAL);
        dialogue.initOwner(parentStage);
        dialogue.setScene(new Scene(parent));
        dialogue.centerOnScreen();
        dialogue.showAndWait();
    }

    /**
     * Populates candidate table
     *
     * @param candidateTable             table
     * @param candidates                 observableList of all candidates
     * @param candidateIdxColumn         candidate index column
     * @param candidateNameColumn        candidate name column
     * @param candidateDescriptionColumn candidate description column
     */
    public static void populateCandidateTable(TableView<Candidate> candidateTable,
                                              ObservableList<Candidate> candidates,
                                              TableColumn<Integer, Integer> candidateIdxColumn,
                                              TableColumn<Integer, String> candidateNameColumn,
                                              TableColumn<Integer, String> candidateDescriptionColumn) {
        final SortedList<Candidate> sortedCandidates = new SortedList<>(candidates);
        sortedCandidates.comparatorProperty().bind(candidateTable.comparatorProperty());
        candidateTable.setItems(sortedCandidates);

        for (Field declaredField : Candidate.class.getDeclaredFields()) {
            declaredField.setAccessible(true);
        }
        candidateIdxColumn.setCellValueFactory(new PropertyValueFactory<>("idx"));
        candidateNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        candidateDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    /**
     * @param address address in question
     * @return whether address points to a BulletinBoard
     */
    public static boolean isBulletinBoard(String address) {
        try {
            return new Voter(address, "").checkBulletinBoard();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Handles an unexpected exception
     *
     * @param exception  exception to be handled
     * @param headerText text written in header
     */
    public static void handleUnexpectedException(Exception exception, String headerText) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Unexpected Error");
        alert.setHeaderText(headerText);
        alert.setContentText(exception.getMessage());
        alert.show();
    }

    /**
     * Handles an unexpected exception
     *
     * @param error      exception to be handled
     * @param headerText text written in header
     */
    public static void handleUnexpectedException(String error, String headerText) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Unexpected Error");
        alert.setHeaderText(headerText);
        alert.setContentText(error);
        alert.show();
    }
}

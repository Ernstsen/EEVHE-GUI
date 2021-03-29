package dk.mmj.eevhe.gui.configurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.mmj.eevhe.entities.Candidate;
import dk.mmj.eevhe.gui.Manager;
import dk.mmj.eevhe.gui.wrappers.BuildFailedException;
import dk.mmj.eevhe.gui.wrappers.ConfigurationBuilder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigurerManager implements Manager {

    private final ObservableList<DAInfo> daAddresses = FXCollections.observableArrayList();
    private final ObservableList<Candidate> candidates = FXCollections.observableArrayList();

    @FXML
    public Button doBuild;
    @FXML
    public Label certFileLabel;
    @FXML
    public Button chooseCertFile;
    @FXML
    public Label certKeyFileLabel;
    @FXML
    public Button chooseCertKeyFile;
    @FXML
    public TableView<DAInfo> daTable;
    @FXML
    public Button addDA;
    @FXML
    public TableColumn<Integer, Integer> id;
    @FXML
    public TableColumn<Integer, String> address;
    @FXML
    public TextField days;
    @FXML
    public TextField hours;
    @FXML
    public TextField minutes;
    @FXML
    public Label outputFolderLabel;
    @FXML
    public Button chooseOutputFolder;
    @FXML
    public TableView<Candidate> candidateTable;
    @FXML
    public TableColumn<Integer, Integer> candidateIdx;
    @FXML
    public TableColumn<Integer, String> candidateName;
    @FXML
    public TableColumn<Integer, String> candidateDescription;
    @FXML
    public Button addCandidate;

    private Stage parentStage;
    private ConfigurationBuilder configurationBuilder;

    @FXML
    public void initialize() {
        configurationBuilder = new ConfigurationBuilder();
        chooseCertFile.setOnAction(event -> {
            final File certFile = getFileChooser("Choose certificate file").showOpenDialog(parentStage);
            if (certFile != null) {
                configurationBuilder.setCertFilePath(certFile.getPath());
                certFileLabel.setText(certFile.getName());
            }
        });

        chooseCertKeyFile.setOnAction(event -> {
            final File certKeyFile = getFileChooser("Choose certificate private-key file").showOpenDialog(parentStage);
            if (certKeyFile != null) {
                configurationBuilder.setCertKeyFilePath(certKeyFile.getPath());
                certKeyFileLabel.setText(certKeyFile.getName());
            }
        });

        chooseOutputFolder.setOnAction(event -> {
            final DirectoryChooser outputChooser = new DirectoryChooser();
            outputChooser.setTitle("Choose output destination");
            final File folder = outputChooser.showDialog(parentStage);
            if (folder != null) {
                configurationBuilder.setOutputFolder(folder.getPath());
                outputFolderLabel.setText(folder.getName());
            }

        });

        addDA.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddNewDecryptionAuthority.fxml"));
            try {
                final Parent addDAParent = loader.load();
                final AddNewDecryptionAuthorityController controller = loader.getController();
                final Stage dialogue = new Stage();
                controller.onSave(inf -> {
                    daAddresses.add(inf);
                    dialogue.close();
                });
                controller.onCancel(dialogue::close);

                showDialogueAndWait(addDAParent, dialogue, daTable);
            } catch (IOException e) {
                handleUnexpectedException(e, "Failed to open 'Add DA' dialogue");
            }
        });

        addCandidate.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AddNewCandidate.fxml"));
                final Parent addCandidateParent = loader.load();
                final AddNewCandidateController controller = loader.getController();
                final Stage dialogue = new Stage();
                controller.onSave(candidate -> {
                    candidates.add(candidate);
                    dialogue.close();
                });
                controller.onCancel(dialogue::close);

                controller.setIdx(candidates.stream().mapToInt(Candidate::getIdx).max().orElse(-1) + 1);

                showDialogueAndWait(addCandidateParent, dialogue, candidateTable);
            } catch (IOException e) {
                handleUnexpectedException(e, "Failed to open 'Add Candidate' dialogue");
            }
        });

        SortedList<DAInfo> sortedInfos = new SortedList<>(daAddresses);
        sortedInfos.comparatorProperty().bind(daTable.comparatorProperty());
        daTable.setItems(sortedInfos);

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));

        final SortedList<Candidate> sortedCandidates = new SortedList<>(candidates);
        sortedCandidates.comparatorProperty().bind(candidateTable.comparatorProperty());
        candidateTable.setItems(sortedCandidates);

        for (Field declaredField : Candidate.class.getDeclaredFields()) {
            declaredField.setAccessible(true);
        }
        candidateIdx.setCellValueFactory(new PropertyValueFactory<>("idx"));
        candidateName.setCellValueFactory(new PropertyValueFactory<>("name"));
        candidateDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        doBuild.setOnAction(this::doBuild);
    }

    /**
     * Handles an unexpected exception
     *
     * @param exception  exception to be handled
     * @param headerText text written in header
     */
    private void handleUnexpectedException(IOException exception, String headerText) {
        final Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Unexpected Error");
        alert.setHeaderText(headerText);
        alert.setContentText(exception.getMessage());
        alert.show();
    }

    /**
     * Displays a dialogue with input to table, waits for finish and refreshes table
     *
     * @param parent   parent for scene (highest order node to be displayed)
     * @param dialogue stage containing the dialogue
     * @param table    table to be refreshed
     */
    private void showDialogueAndWait(Parent parent, Stage dialogue, TableView<?> table) {
        dialogue.initModality(Modality.APPLICATION_MODAL);
        dialogue.initOwner(parentStage);
        dialogue.setScene(new Scene(parent));
        dialogue.centerOnScreen();
        dialogue.showAndWait();
        table.refresh();
    }

    /**
     * @param title title for the fileChooser
     * @return a fileChooser for .pem files
     */
    private FileChooser getFileChooser(String title) {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("certificates", "*.pem"));
        return chooser;
    }

    private void doBuild(ActionEvent event) {
        final Alert waitAlert = new Alert(Alert.AlertType.INFORMATION);
        waitAlert.setTitle("Operation in progress");
        waitAlert.setContentText("Computing configuration files");
        waitAlert.setHeaderText("Please wait... ");
        ProgressIndicator progress = new ProgressIndicator();
        waitAlert.setGraphic(progress);
        waitAlert.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
        waitAlert.show();

        new Thread(() -> {
            try {
                configurationBuilder.setDaAddresses(daAddresses.stream().collect(Collectors.toMap(DAInfo::getId, DAInfo::getAddress)));
                configurationBuilder.setElectionDuration(new ConfigurationBuilder.Duration(
                        Integer.parseInt(days.getText()),
                        Integer.parseInt(hours.getText()),
                        Integer.parseInt(minutes.getText())
                ));
                configurationBuilder.setCandidates(candidates);
                configurationBuilder.build();
            } catch (BuildFailedException | NumberFormatException e) {
                Platform.runLater(() -> {
                    final Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Configuration Error");
                    alert.setHeaderText("Failed to produce configuration output");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                });
            } finally {
                Platform.runLater(waitAlert::close);
            }
        }).start();
    }

    @Override
    public void manage(Stage parentStage) {
        this.parentStage = parentStage;

        try {
            Parent root = FXMLLoader.load(getClass().getResource("ConfigureMain.fxml"));
            parentStage.setScene(new Scene(root));
            parentStage.centerOnScreen();
            parentStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource", e);
        }
    }

    public static class DAInfo {
        public final int id;
        public final String address;

        public DAInfo(int id, String address) {
            this.id = id;
            this.address = address;
        }

        public int getId() {
            return id;
        }

        public String getAddress() {
            return address;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DAInfo daInfo = (DAInfo) o;
            return id == daInfo.id &&
                    Objects.equals(address, daInfo.address);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, address);
        }
    }
}

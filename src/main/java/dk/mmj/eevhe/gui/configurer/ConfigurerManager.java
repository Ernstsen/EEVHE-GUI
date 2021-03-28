package dk.mmj.eevhe.gui.configurer;

import dk.mmj.eevhe.gui.Manager;
import dk.mmj.eevhe.gui.wrappers.BuildFailedException;
import dk.mmj.eevhe.gui.wrappers.ConfigurationBuilder;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ConfigurerManager implements Manager {

    private final ObservableList<DAInfo> daAddresses = FXCollections.observableArrayList();

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

    private Stage parentStage;
    private ConfigurationBuilder configurationBuilder;

    @FXML
    public void initialize() {
        configurationBuilder = new ConfigurationBuilder();
        final FileChooser certChooser = getChooser("Choose certificate file");
        chooseCertFile.setOnAction(event -> {
            final File certFile = certChooser.showOpenDialog(parentStage);
            if (certFile != null) {
                configurationBuilder.setCertFilePath(certFile.getPath());
                certFileLabel.setText(certFile.getName());
            }
        });

        final FileChooser certKeyChooser = getChooser("Choose certificate private-key file");
        chooseCertKeyFile.setOnAction(event -> {
            final File certKeyFile = certKeyChooser.showOpenDialog(parentStage);
            if (certKeyFile != null) {
                configurationBuilder.setCertFilePath(certKeyFile.getPath());
                certKeyFileLabel.setText(certKeyFile.getName());
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

                dialogue.initModality(Modality.APPLICATION_MODAL);
                dialogue.initOwner(parentStage);
                dialogue.setScene(new Scene(addDAParent));
                dialogue.showAndWait();
                daTable.refresh();

            } catch (IOException e) {
                final Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Unexpected Error");
                alert.setHeaderText("Failed to open 'Add DA' dialogue");
                alert.setContentText(e.getMessage());
                alert.show();
            }
        });

        SortedList<DAInfo> sortedInfos = new SortedList<>(daAddresses);
        sortedInfos.comparatorProperty().bind(daTable.comparatorProperty());
        daTable.setItems(sortedInfos);

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));

        doBuild.setOnAction(this::doBuild);
    }

    private FileChooser getChooser(String title) {
        final FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("certificates", "*.pem"));
        return chooser;
    }

    private void doBuild(ActionEvent event) {
        try {
            configurationBuilder.build();
        } catch (BuildFailedException e) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Configuration Error");
            alert.setHeaderText("Failed to produce configuration output");
            alert.setContentText(e.getMessage());
            alert.show();
        }
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

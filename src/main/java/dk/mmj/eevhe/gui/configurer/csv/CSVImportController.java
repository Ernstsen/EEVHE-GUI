package dk.mmj.eevhe.gui.configurer.csv;

import dk.mmj.eevhe.gui.Utilities;
import dk.mmj.eevhe.gui.configurer.csv.CSVConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CSVImportController<T> {


    @FXML
    public Button cancel;
    @FXML
    public Button add;
    @FXML
    public Button importFile;
    @FXML
    public TextArea csv;

    private Consumer<List<T>> callback;
    private CSVConfig<T> config;

    private Runnable onCancel;

    @FXML
    public void initialize() {
        add.setOnAction(event -> {
            final String csvText = csv.getText();
            if (csvText == null || csvText.isEmpty()) {
                final Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Creation Error");
                alert.setTitle("Failed to parse infos");
                alert.setContentText("Must supply valid ´;´ separated csv file with id;address");
                alert.show();
                return;
            }

            final List<T> daInfos = Arrays.stream(csvText.split("\n"))
                    .map(s -> s.split(";"))
                    .filter(config::isValid)
                    .map(arr -> config.parse(arr))
                    .collect(Collectors.toList());
            callback.accept(daInfos);
        });

        importFile.setOnAction(event -> {
            final FileChooser fileChooser = Utilities.getFileChooser(
                    "Choose .csv file",
                    new FileChooser.ExtensionFilter("comma-separated file", "*.csv")
            );

            final File file = fileChooser.showOpenDialog(importFile.getParent().getScene().getWindow());

            try {
                final String fileAsString = new String(Files.readAllBytes(file.toPath()));
                csv.setText(fileAsString);
            } catch (IOException e) {
                Utilities.handleUnexpectedException(e, "Failed to read csv file: " + file.getName());
            }

        });

        cancel.setOnAction(event -> onCancel.run());
    }

    public void configure(Consumer<List<T>> callback, CSVConfig<T> csvConfig){
        this.callback = callback;
        config = csvConfig;
    }

    public void onCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }
}

package dk.mmj.eevhe.gui;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Map;

public class GUI extends Application {
    /**
     * GUI acts as singleton, meaning this will not break
     */
    public static GUI INSTANCE;

    private Stage primaryStage;
    private Action action;

    @Override
    public void init() {
        final Parameters parameters = getParameters();
        final Map<String, String> named = parameters.getNamed();

        if (named.containsKey("action")) {
            try {
                action = Action.valueOf(named.get("action").toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        INSTANCE = this;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("EEVHE");
        primaryStage.getIcons().add(new Image("logo.png"));

        if (action == null) {
            Parent root = FXMLLoader.load(getClass().getResource("/layout/Chooser.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.centerOnScreen();
            primaryStage.show();
        } else {
            action.produceManager().manage(primaryStage);
        }
    }

    void setAction(Action action) {
        this.action = action;
        Platform.runLater(() -> action.produceManager().manage(primaryStage));
    }

}

package dk.mmj.eevhe.gui;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Map;

public class GUI extends Application {

    private Stage primaryStage;
    private Action action;

    @Override
    public void init() throws Exception {
        final Parameters parameters = getParameters();
        final Map<String, String> named = parameters.getNamed();

        if (named.containsKey("action")) {
            action = Action.valueOf(named.get("action"));
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Extended Electronic Voting using Homomorphic Encryption");

        Parent root = FXMLLoader.load(getClass().getResource("Chooser.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.centerOnScreen();
        primaryStage.show();

    }
}

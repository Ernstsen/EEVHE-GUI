package dk.mmj.eevhe.gui;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

import static javafx.application.Application.launch;

public class Main {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void main(String[] args) {
        launch(GUI.class, args);
    }
}

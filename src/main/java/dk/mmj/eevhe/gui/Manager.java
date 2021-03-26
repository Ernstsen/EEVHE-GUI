package dk.mmj.eevhe.gui;

import javafx.stage.Stage;

/**
 * Interface signifying that an implementing class manages part of the GUI
 */
public interface Manager {

    /**
     * The manager starts handling what it is designed for.
     * <br>
     * Can only be called once per instance
     *
     * @param parentStage stage containing the content to be managed
     */
    void manage(Stage parentStage);
}

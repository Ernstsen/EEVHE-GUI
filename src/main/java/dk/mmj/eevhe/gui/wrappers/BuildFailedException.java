package dk.mmj.eevhe.gui.wrappers;

/**
 * Exception informing that a build has failed
 */
public class BuildFailedException extends Exception {

    public BuildFailedException(String message) {
        super(message);
    }

    public BuildFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

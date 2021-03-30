package dk.mmj.eevhe.gui.wrappers;

/**
 * Exception thrown when voting fails
 */
public class VoteFailedException extends Exception {

    public VoteFailedException(String message) {
        super(message);
    }

    public VoteFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}

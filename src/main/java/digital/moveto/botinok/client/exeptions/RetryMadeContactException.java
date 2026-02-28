package digital.moveto.botinok.client.exeptions;

/**
 * No free personalized invitations left
 * Exception to be thrown when a retry is needed to make contact with the server.
 */
public class RetryMadeContactException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RetryMadeContactException(String message) {
        super(message);
    }
}

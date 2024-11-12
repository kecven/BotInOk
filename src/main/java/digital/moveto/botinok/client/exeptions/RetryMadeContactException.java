package digital.moveto.botinok.client.exeptions;

/**
 * No free personalized invitations left
 * Exception to be thrown when a retry is needed to make contact with the server.
 */
public class RetryMadeContactException extends RuntimeException {
    public RetryMadeContactException(String message) {
        super(message);
    }
}


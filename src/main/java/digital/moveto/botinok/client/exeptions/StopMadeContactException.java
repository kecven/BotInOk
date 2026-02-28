package digital.moveto.botinok.client.exeptions;

public class StopMadeContactException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public StopMadeContactException(String message) {
        super(message);
    }
}

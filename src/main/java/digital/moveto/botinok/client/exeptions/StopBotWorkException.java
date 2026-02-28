package digital.moveto.botinok.client.exeptions;

public class StopBotWorkException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public StopBotWorkException(String message) {
        super(message);
    }
    public StopBotWorkException(Throwable message) {
        super(message);
    }
}

package digital.moveto.botinok.client.exeptions;

public class StopBotWorkException extends RuntimeException {
    public StopBotWorkException(String message) {
        super(message);
    }
    public StopBotWorkException(Throwable message) {
        super(message);
    }
}


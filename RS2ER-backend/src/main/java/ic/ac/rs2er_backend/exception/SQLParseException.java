package ic.ac.rs2er_backend.exception;

public class SQLParseException extends Exception {
    public SQLParseException() {
        super();
    }

    public SQLParseException(String message) {
        super(message);
    }

    public SQLParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public SQLParseException(Throwable cause) {
        super(cause);
    }
}

package b;

public class QueryStringParsingException extends RuntimeException {
    public QueryStringParsingException(String message) {
        super(message);
    }

    public QueryStringParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}

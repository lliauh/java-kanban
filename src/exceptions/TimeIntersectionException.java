package exceptions;

public class TimeIntersectionException extends RuntimeException {
    public TimeIntersectionException() {
    }

    public TimeIntersectionException(final String message) {
        super(message);
    }
}

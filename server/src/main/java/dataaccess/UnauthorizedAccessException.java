package dataaccess;

/**
 * Indicates that the provided authToken was invalid
 */
public class UnauthorizedAccessException extends Exception {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
    public  UnauthorizedAccessException(String message, Throwable ex) {
        super(message, ex);
    }
}
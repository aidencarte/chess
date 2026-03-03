package dataaccess;

/**
 * Indicates the username was already taken
 */
public class AlreadyTakenException extends Exception{
    public AlreadyTakenException(String message) {
        super(message);
    }
    public  AlreadyTakenException(String message, Throwable ex) {
        super(message, ex);
    }
}

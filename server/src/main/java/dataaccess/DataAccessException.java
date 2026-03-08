package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    private int statusCode = 0;
    public DataAccessException(String message) {
        super(message);
    }
    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
    }

    public DataAccessException(int i, String internalServerError) {
        this.statusCode = i;
    }
    public int getStatusCode()
    {
        return statusCode;
    }
}

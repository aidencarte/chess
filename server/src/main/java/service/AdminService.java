package service;
import dataaccess.*;

public class AdminService extends Service {
    public AdminService(DataAccess dataAccess) {
        super(dataAccess);
    }

    public void clearApplication() throws DataAccessException {
        try {
            dataAccess.clear();
        } catch (DataAccessException ex) {
            throw new DataAccessException(500, "Server error");
        }
    }
}
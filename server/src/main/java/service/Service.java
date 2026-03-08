package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;

public abstract class Service {
    protected final DataAccess dataAccess;

    protected Service(DataAccess dataAccess) {

        this.dataAccess = dataAccess;
    }


    protected AuthData getAuthData(String authToken) throws DataAccessException {

            if (authToken != null) {
                var authData = dataAccess.getAuth(authToken);
                if (authData != null) {
                    return authData;
                }
            }
            throw new DataAccessException(401, "Not authorized");

    }

}
package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;

public class UserService extends Service {
    public UserService(DataAccess dataAccess) {
        super(dataAccess);
    }


    public AuthData register(RegisterRequest registerRequest) throws DataAccessException {
        try {
            UserData newUser = dataAccess.createUser(registerRequest);
            return dataAccess.createAuth(newUser.username());
        } catch (DataAccessException ex) {
            throw new DataAccessException(403, "Unable to register user");
        }
    }
}
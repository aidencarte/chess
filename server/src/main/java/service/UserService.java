package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import org.mindrot.jbcrypt.BCrypt;
public class UserService extends Service {
    public UserService(DataAccess dataAccess) {
        super(dataAccess);
    }


    public AuthData register(RegisterRequest registerRequest) throws DataAccessException {
        try {
            var ePassword = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());
            var eUser = new RegisterRequest(registerRequest.username(),ePassword,
                    registerRequest.email());
            UserData newUser = dataAccess.createUser(eUser);
            return dataAccess.createAuth(newUser.username());
        } catch (DataAccessException ex) {
            throw new DataAccessException(403, "Unable to register user");
        }
    }
}
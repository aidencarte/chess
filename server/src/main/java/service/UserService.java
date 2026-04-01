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

            var ePassword = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());
            var eUser = new RegisterRequest(registerRequest.username(),ePassword,
                    registerRequest.email());
            if(dataAccess.getUser(registerRequest.username())!=null) {
                throw new DataAccessException(403, "Username already taken/duplicate");
            }
            UserData newUser = dataAccess.createUser(eUser);
            return dataAccess.createAuth(newUser.username());

    }
}
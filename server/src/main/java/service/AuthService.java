package service;

import dataaccess.*;
import model.*;

public class AuthService extends Service {
    public AuthService(DataAccess dataAccess) {
        super(dataAccess);
    }

    public LoginResult createAuth(LoginRequest loginRequest) throws DataAccessException {

            UserData loggedInUser = dataAccess.getUser(loginRequest.username());
            if (loggedInUser != null && loggedInUser.password().equals(loginRequest.password())) {
                AuthData user =  dataAccess.createAuth(loggedInUser.username());
                return new LoginResult(user.username(),user.authToken());
            }

            throw new DataAccessException(401, "Invalid username/password");
    }

    public void deleteSession(String authToken) throws DataAccessException {
        try{
            getAuthData(authToken);
            dataAccess.deleteAuth(authToken);
    }
        catch (DataAccessException e) {
            throw new DataAccessException(e.getStatusCode(),e.getMessage());
        }

        }

}
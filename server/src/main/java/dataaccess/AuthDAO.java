package dataaccess;

import model.AuthData;

public interface AuthDAO {
    AuthData getAuth(String authToken)throws DataAccessException;
    void createAuth(AuthData authData);
    void deleteAuth(String authToken);
    void clear();
}
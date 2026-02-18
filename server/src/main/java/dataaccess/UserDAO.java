package dataaccess;

import model.UserData;
public interface UserDAO {
    UserData getUser(String authToken, String username) throws DataAccessException;
    void createUser(UserData userData) throws DataAccessException;
    void clear();
}
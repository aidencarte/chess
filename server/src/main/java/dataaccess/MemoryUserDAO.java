package dataaccess;

import model.*;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    private HashMap<String, UserData> users = new HashMap<>();


    public UserData getUser(String username) throws DataAccessException {
        var result = users.get(username);
        if(result == null) throw new DataAccessException("Could not find user");
        return result;

    }

    public void createUser(RegisterRequest registerRequest) throws DataAccessException, AlreadyTakenException {
        UserData userData = getUser(registerRequest.username());
        if(userData!= null) throw new AlreadyTakenException("Username Already Taken");
        users.put(registerRequest.username(),
                new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
    }

    public void clear() {
        users.clear();
    }
}
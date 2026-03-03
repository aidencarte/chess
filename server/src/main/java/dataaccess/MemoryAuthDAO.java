package dataaccess;

import model.*;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> auths = new HashMap<>();
    public AuthData getAuth(String authToken) throws DataAccessException {
        var result = auths.get(authToken);
        if(result == null) throw new DataAccessException("Could not find auth");
        return result;

    }

    public void createAuth(AuthData authData) {
        auths.put(authData.authToken(),authData);
    }

    public void deleteAuth(String authToken)
    {
        auths.remove(authToken);
    }


    public void clear() {
        auths.clear();
    }
}

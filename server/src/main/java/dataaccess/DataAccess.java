package dataaccess;

import model.*;

import java.util.Collection;
import java.util.UUID;

public interface DataAccess {
    void clear() throws DataAccessException;

    UserData createUser(RegisterRequest registerRequest) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    GameData updateGame(GameData game) throws DataAccessException;

    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String username) throws DataAccessException;
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
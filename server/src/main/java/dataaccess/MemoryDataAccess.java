package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class MemoryDataAccess implements  DataAccess{
    private HashMap<String, AuthData> auths = new HashMap<>();
    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<Integer, GameData> games =  new HashMap<>();
    private int nextGameID = 1;

    public void clear() {
    auths.clear();
    users.clear();
    games.clear();
    }

    public UserData createUser(RegisterRequest registerRequest) throws DataAccessException {
        UserData userData = getUser(registerRequest.username());
        if(userData== null) {
            var newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            users.put(registerRequest.username(),
                    newUser);
            return newUser;
        }
        else{
            throw new DataAccessException(403, "Username Already Taken");
        }
    }

    public UserData getUser(String username)  {
        var result = users.get(username);
        return result;
    }

    public GameData createGame(String gameName) {
        var gameID = nextGameID++;
        var newGame = new GameData(gameID, null, null, gameName, new ChessGame(), GameData.State.UNDECIDED);
        games.put(newGame.gameID(), newGame);
        return newGame;
    }

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public GameData updateGame(GameData game){
        games.put(game.gameID(), game);
        return game;
    }

    public AuthData createAuth(String username) {
        var auth = new AuthData(generateToken(), username);
        auths.put(auth.authToken(), auth);
        return auth;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        var result = auths.get(authToken);
        if(Objects.isNull(result)){
            throw new DataAccessException(401,"Unauthorized");
        }
        return result;
    }

    public void deleteAuth(String username) {
        auths.remove(username);
    }
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}

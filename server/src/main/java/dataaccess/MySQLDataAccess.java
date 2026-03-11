package dataaccess;

import model.*;
import com.google.gson.Gson;
import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import java.util.Collection;
import java.util.List;

public class MySQLDataAccess implements DataAccess{
    public MySQLDataAccess() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public UserData createUser(RegisterRequest registerRequest) throws DataAccessException {
        return null;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public GameData updateGame(GameData game) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData createAuth(String username) {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String username) throws DataAccessException {

    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
                CREATE TABLE IF NOT EXISTS  users (
                  `username` varchar(128) NOT NULL,
                  'password' varchar(128) NOT NULL,
                  'email' varchar(128) NOT NULL,
                  PRIMARY KEY (`username`),
                  UNIQUE KEY 'username_UNIQUE' ('username')
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                
                CREATE TABLE IF NOT EXISTS  auths (
                  `authToken` varchar(128) NOT NULL,
                  'username' varchar(128) NOT NULL,
                  PRIMARY KEY (`authToken`),
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                
                CREATE TABLE IF NOT EXISTS  games (
                  `gameID` int NOT NULL AUTO_INCREMENT,
                  'gameName' varchar(128) DEFAULT NULL,
                  'whitePlayerName' varchar(128) DEFAULT NULL,
                  'blackPlayerName' varchar(128) DEFAULT NULL,
                  'game' longtext NOT NULL,
                  'state' varchar(50) DEFAULT NULL,
                  PRIMARY KEY (`gameID`),
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                """
    };

}

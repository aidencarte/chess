package dataaccess;

import model.*;
import com.google.gson.Gson;
import java.sql.*;


import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.sql.Types.NULL;

public class MySQLDataAccess implements DataAccess{
    public MySQLDataAccess() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        executeCommand("DELETE FROM 'user'");
        executeCommand("DELETE FROM 'auths'");
        executeCommand("DELETE FROM 'games'");
    }

    @Override
    public UserData createUser(RegisterRequest registerRequest) throws DataAccessException {
        if(registerRequest.username() != null)
        {
            UserData newUser = new UserData(registerRequest.username(), registerRequest.password(),
                    registerRequest.email());

        }
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
    public AuthData createAuth(String username) throws DataAccessException {
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

    private void executeCommand(String statement) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Failed to execute command: %s", e.getMessage()));
        }
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException{
        try(var conn = DatabaseManager.getConnection())
        {
            var preparedStatement = conn.prepareStatement(statement);
            for(int i = 0; i < params.length;i++)
            {
                setIndex(preparedStatement,i+1,params[i]);
            }
            preparedStatement.executeUpdate();
            var results = preparedStatement.getGeneratedKeys();
            if(results.next())
            {
                return results.getInt(1);
            }
            return 0;

        }
        catch (Exception e)
        {
            throw new DataAccessException(e.getMessage());
        }
    }
    private void setIndex(java.sql.PreparedStatement preparedStatement, int index, Object param) throws SQLException {
        switch (param) {
            case String s -> preparedStatement.setString(index, s);
            case Integer x -> preparedStatement.setInt(index, x);
            case null -> preparedStatement.setNull(index, NULL);
            default -> throw new SQLException("Unsupported parameter type: " + param.getClass());
        }
    }
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS `authentication` (
              `authToken` varchar(128) NOT NULL,
              `username` varchar(128) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  `game` (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `gameName` varchar(50) DEFAULT NULL,
              `whitePlayerName` varchar(100) DEFAULT NULL,
              `blackPlayerName` varchar(100) DEFAULT NULL,
              `game` longtext NOT NULL,
              `state` varchar(50) DEFAULT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS `user` (
              `username` varchar(128) NOT NULL,
              `password` varchar(128) NOT NULL,
              `email` varchar(128) NOT NULL,
              PRIMARY KEY (`username`),
              UNIQUE KEY `username_UNIQUE` (`username`)
            ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """};

}

package dataaccess;

import chess.ChessGame;
import model.*;
import com.google.gson.Gson;
import java.sql.*;


import java.util.ArrayList;
import java.util.Collection;

import static java.sql.Types.NULL;

public class MySQLDataAccess implements DataAccess{
    public MySQLDataAccess() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        executeCommand("TRUNCATE user");
        executeCommand("TRUNCATE auths");
        executeCommand("TRUNCATE games");
    }

    @Override
    public UserData createUser(RegisterRequest registerRequest) throws DataAccessException {
        if(registerRequest.username() != null)
        {
            UserData newUser = new UserData(registerRequest.username(), registerRequest.password(),
                    registerRequest.email());
            executeUpdate("INSERT into user (username, password, email) VALUES (?, ?, ?)",
                    newUser.username(), newUser.password(), newUser.email());
            return newUser;

        }
        return null;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection())
        {
            String statement = "SELECT password, email FROM user WHERE username=?";
            try(PreparedStatement preparedStatement = conn.prepareStatement(statement))
            {
                preparedStatement.setString(1, username);
                try(ResultSet results = preparedStatement.executeQuery()){
                    if(results.next())
                    {
                        var password = results.getString("password");
                        var email = results.getString("email");
                        return new UserData(username, password, email);
                    }
                }
            }

        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }


        return null;
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        var game = new ChessGame();
        game.myBoard.resetBoard();
        var state = GameData.State.UNDECIDED;
        var id = executeUpdate("INSERT INTO games (gameName, whitePlayerName, blackPlayerName, game, state) VALUES (?, ?, ?, ?, ?)",
                gameName,
                null,
                null,
                game.toString(),
                state.toString());
        if (id != 0) {
            return new GameData(id, null, null, gameName, game, state);
        }
        return null;
    }


    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection())
        {
            try(var preparedStatement = conn.prepareStatement("SELECT gameID, gameName, whitePlayerName, " +
                    "blackPlayerName, game, state FROM games WHERE gameID=?"))
            {
                preparedStatement.setInt(1, gameID);
                try(var results = preparedStatement.executeQuery()){
                    if(results.next())
                    {
                        return pullGameData(results);
                    }
                }
            }

        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }


        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try(var conn = DatabaseManager.getConnection()){
            try(var preparedStatement = conn.prepareStatement("SELECT gameID, gameName," +
                    " whitePlayerName, blackPlayerName, game, state FROM games"))
            {
                try(var resultSet = preparedStatement.executeQuery())
                {
                    while(resultSet.next())
                    {
                        var checkedGameData = pullGameData(resultSet);
                        result.add(checkedGameData);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return result;
    }

    @Override
    public GameData updateGame(GameData game) throws DataAccessException {
        executeUpdate("UPDATE games set gameName=?, whitePlayerName=?, blackPlayerName=?, " +
                "game=?, state=? WHERE gameID=?",
                game.gameName(),
                game.whiteUsername(),
                game.blackUsername(),
                game.toString(),
                game.state().toString(),
                game.gameID());
        return game;
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        var auth = new AuthData(DataAccess.generateToken(), username);
        executeUpdate("INSERT into auths (authtoken, username) VALUES (?, ?)",
                auth.authToken(),auth.username());
        return auth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection())
        {
            try(var preparedStatement = conn.prepareStatement("SELECT username from auths where authToken=?")){
                preparedStatement.setString(1, authToken);
                try(var results = preparedStatement.executeQuery()){
                    if(results.next())
                    {
                        return new AuthData(authToken,  results.getString("username"));
                    }
                }
            }

        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }


        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        executeUpdate("DELETE from auths WHERE authToken=?", authToken);
    }

    private void configureDatabase() throws DataAccessException {
        try{
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
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
            var preparedStatement = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
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
        catch (SQLIntegrityConstraintViolationException e)
        {
            throw new DataAccessException(403, e.getMessage());
        }
        catch(SQLException e)
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
    private GameData pullGameData (ResultSet results) throws SQLException
    {
        var gameString = results.getString("game");
        var gameID = results.getInt("gameID");
        var gameName = results.getString("gameName");
        var whitePlayerName = results.getString("whitePlayerName");
        var blackPlayerName = results.getString("blackPlayerName");
        var game = gameFromString(gameString);
        var state = GameData.State.valueOf(results.getString("state"));
        return new GameData(gameID, whitePlayerName, blackPlayerName, gameName, game, state);
    }

    private ChessGame gameFromString(String gameString)
    {
        return new Gson().fromJson(gameString, ChessGame.class);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS `auths` (
              `authToken` varchar(128) NOT NULL,
              `username` varchar(128) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  `games` (
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
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """};
    public String toString() {
        return String.format("MySQL - %s", DatabaseManager.dbName());
    }


}

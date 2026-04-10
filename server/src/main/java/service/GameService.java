package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;

import java.util.Collection;

public class GameService extends Service {
    public GameService(DataAccess dataAccess) {
        super(dataAccess);
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        getAuthData(authToken);
        try {
            return dataAccess.listGames();
        } catch (DataAccessException ex) {
            throw new DataAccessException(500, "Server Error");
        }
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException {
        getAuthData(authToken);
        try {
            return dataAccess.createGame(gameName);
        } catch (DataAccessException ex) {
            throw new DataAccessException(500, "Server error");
        }
    }

    public GameData joinGame(String authToken, ChessGame.TeamColor color, int gameID) throws DataAccessException {
        AuthData authData = getAuthData(authToken);
        String username = authData.username();
        GameData gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException(400, "Unknown game");
        } else if (color == null) {
                return gameData;
        } else if (gameData.isGameOver()) {
            throw new DataAccessException(403, "Game is over");
        } else {
            if (color == ChessGame.TeamColor.WHITE) {
                if (gameData.whiteUsername() == null || gameData.whiteUsername().equals(username)) {
                    gameData = gameData.setWhite(username);
                    gameData = gameData.setState(GameData.State.UNDECIDED,
                            String.format("%s joined game as white", username));

                    } else {
                    throw new DataAccessException(403, "Color taken");
                    }
                } else if (color == ChessGame.TeamColor.BLACK) {
                    if (gameData.blackUsername() == null || gameData.blackUsername().equals(username)) {
                        gameData = gameData.setBlack(username);
                        gameData = gameData.setState(GameData.State.UNDECIDED,
                                String.format("%s joined game as black", username));
                    } else {
                        throw new DataAccessException(403, "Color taken");
                    }
                }
                dataAccess.updateGame(gameData);
            }
        return gameData;
    }

    public record ConnectionInfo(String username, String role, GameData gameData) {
    }

    public ConnectionInfo connectToGame(String authToken, int gameID) throws Exception {
        AuthData authData = getAuthData(authToken);
        String username = authData.username();
        GameData gameData = getGame(gameID);

        return new ConnectionInfo(username, getRole(username, gameData), gameData);
    }

    public record MoveInfo(String username, GameData gameData) {
    }

    public MoveInfo makeMove(String authToken, int gameID, ChessMove move) throws Exception {
        AuthData authData = getAuthData(authToken);
        String username = authData.username();
        try {
            GameData gameData = getGame(gameID);
            return new MoveInfo(username, updateGame(gameData.makeMove(username, move)));
        } catch (InvalidMoveException ex) {
            throw new DataAccessException(400, "Invalid move");
        }
    }

    public String leaveGame(String authToken, int gameID) throws Exception {
        AuthData authData = getAuthData(authToken);
        String username = authData.username();
        GameData gameData = getGame(gameID);
        if (!gameData.isGameOver()) {
            if (username.equals(gameData.whiteUsername())) {
                gameData = gameData.setWhite(null);
                gameData = gameData.setState(gameData.state(), String.format("%s playing white has resigned",
                        username));
            } else if (username.equals(gameData.blackUsername())) {
                gameData = gameData.setBlack(null);
                gameData = gameData.setState(gameData.state(), String.format("%s playing black has resigned",
                        username));
            }
            updateGame(gameData);
        }
        return username;
    }


    public String resignGame(String authToken, int gameID) throws Exception {
        AuthData authData = getAuthData(authToken);
        String username = authData.username();
        GameData gameData = getGame(gameID);
        if (!gameData.isGameOver()) {
            if (username.equals(gameData.whiteUsername()) && gameData.blackUsername() != null) {
                gameData = gameData.setState(GameData.State.BLACK, String.format("%s playing WHITE resigned!", username));
            } else if (username.equals(gameData.blackUsername()) && gameData.whiteUsername() != null) {
                gameData = gameData.setState(GameData.State.WHITE, String.format("%s playing BLACK resigned!", username));
            } else {
                throw new DataAccessException(400, "Observer cannot resign");
            }
            updateGame(gameData);
        } else {
            throw new DataAccessException(400, "Game is already over");
        }
        return username;
    }


    private GameData getGame(int gameID) throws DataAccessException {
        try {
            GameData gameData = dataAccess.getGame(gameID);
            if (gameData == null) {
                throw new DataAccessException(400, "Unknown game");
            }
            return gameData;
        } catch (DataAccessException ex) {
            throw new DataAccessException(500, "Server error");
        }
    }

    private GameData updateGame(GameData gameData) throws Exception {
        try {
            return dataAccess.updateGame(gameData);
        } catch (DataAccessException ex) {
            throw new DataAccessException(500, ex.getMessage());
        }
    }

    private String getRole(String username, GameData gameData) {
        if (username.equals(gameData.whiteUsername())) {
            return "white";
        } else if (username.equals(gameData.blackUsername())) {
            return "black";
        }
        return "observer";
    }
}


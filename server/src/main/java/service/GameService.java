package service;

import chess.ChessGame;
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
                    } else {
                    throw new DataAccessException(403, "Color taken");
                    }
                } else if (color == ChessGame.TeamColor.BLACK) {
                    if (gameData.blackUsername() == null || gameData.blackUsername().equals(username)) {
                        gameData = gameData.setBlack(username);
                    } else {
                        throw new DataAccessException(403, "Color taken");
                    }
                }
                dataAccess.updateGame(gameData);
            }
        return gameData;
    }

}
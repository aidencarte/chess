package dataAccess;
import chess.model.GameData;

import javax.xml.crypto.Data;

public interface GamesDAO {
    GameData getGame(int gameID) throws DataAccessException;
    void createGame(GameData gameData) throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
    void deleteGames();
}
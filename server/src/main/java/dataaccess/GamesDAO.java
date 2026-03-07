package dataaccess;
import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

public interface GamesDAO {
    HashMap<Integer, GameData> getGames() throws DataAccessException;
    void createGame(GameData gameData);
    void updateGame(GameData gameData) throws DataAccessException;
    void clear();
}
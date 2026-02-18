package dataaccess;
import model.GameData;

public interface GamesDAO {
    GameData getGame(int gameID) throws DataAccessException;
    void createGame(GameData gameData) throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
    void clear();
}
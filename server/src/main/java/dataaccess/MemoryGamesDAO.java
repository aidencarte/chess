package dataaccess;

import model.GameData;

import java.util.HashMap;

public class MemoryGamesDAO implements GamesDAO{
    private HashMap<Integer, GameData> games;
    private Integer nextId = 100;
    @Override
    public HashMap<Integer, GameData> getGames() throws DataAccessException {
        return games;
    }

    @Override
    public void createGame(GameData gameData) {
        games.put(nextId,gameData);
        nextId++;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        Integer tempId = games.get(gameData.)

    }

    @Override
    public void clear() {

    }
}

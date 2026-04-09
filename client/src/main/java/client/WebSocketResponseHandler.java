package client;

import model.GameData;

public interface WebSocketResponseHandler {
    void notify(String message);

    void loadGame(GameData game);
}

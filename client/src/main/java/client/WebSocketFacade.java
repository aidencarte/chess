package client;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import model.GameData;

import model.GameData;

public class WebSocketFacade extends Endpoint {
    Session session;
    WebSocketResponseHandler responseHandler;


    private void sendMessage(String message)throws IOException
    {
        session.getBasicRemote().sendText(message);
    }












    @Override
    public void onOpen(Session session, EndpointConfig config) {

    }

}

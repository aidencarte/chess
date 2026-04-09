package server;


import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import model.GameData;
import service.GameService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadMessage;
import websocket.messages.NotificationMessage;


public class WebsocketServer {

    private final ConnectionManager connections = new ConnectionManager();
    private final GameService gameService;
    public WebsocketServer(Javalin server, GameService gameService) {
        this.gameService = gameService;
        server.ws("/ws", ws -> {
            ws.onConnect(this:: websocketConnect);
            ws.onMessage(this::websocketMessage);
            ws.onClose(this:: websocketClose);
        });
    }

    private void websocketClose(WsCloseContext wsCloseContext) {
    }

    private void websocketMessage(WsMessageContext ctx) {
        try{
            var command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch(command.getCommandType()){
                case LEAVE -> leaveGame(ctx,command);
                case RESIGN -> resignGame(command);
                case MAKE_MOVE -> makeMove(ctx, new Gson().fromJson(ctx.message(), MakeMoveCommand.class));
                case CONNECT -> connectGame(ctx, command);
            }
        }
        catch(Exception ex){
            var error = new ErrorMessage(ex.getMessage());
            ctx.send(error.toString());
        }
    }

    private void websocketConnect(WsMessageContext ctx) {
        ctx.enableAutomaticPings();
        System.out.println("websocket connection established");
    }


}



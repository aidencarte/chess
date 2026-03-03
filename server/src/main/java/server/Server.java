package server;

import Service.UserService;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import io.javalin.http.Handler;
import org.eclipse.jetty.server.Authentication;

public class Server {

    private final UserService userService;
    private final Javalin javalin;
    public Server() {
        this.userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
        this.javalin = Javalin.create(config -> config.staticFiles.add("web"));


        // Register your endpoints and exception handlers here.

    }

    private void createHandlers()
    {
        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(userService);
        javalin.post("/user", userHandler::registerUser);
        javalin.delete("/db", this::clearDb);
        javalin.post("/user", userHandler::registerUser);
        javalin.post("/session", userHandler::loginUser);
        javalin.delete("/session", userHandler::logoutUser);
        javalin.post("/game", gameHandler::createGame);
        javalin.get("/game", gameHandler::listGames);
        javalin.put("/game", gameHandler::joinGame);
    }


    public int run(int desiredPort) {
        createHandlers();
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

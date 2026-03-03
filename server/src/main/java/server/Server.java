package server;

import Service.UserService;
import io.javalin.*;
import io.javalin.http.Handler;
import org.eclipse.jetty.server.Authentication;

public class Server {

    private final UserService userService;
    private final Javalin javalin;
    public Server(UserService userService) {
        this.userService = userService;
        this.javalin = Javalin.create(config -> config.staticFiles.add("web"));


        // Register your endpoints and exception handlers here.

    }

    private void createHandlers()
    {
        UserHandler userHandler = new UserHandler(userService);
        javalin.post("/user", userHandler::registerUser);
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

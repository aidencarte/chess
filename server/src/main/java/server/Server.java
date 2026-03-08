package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.*;
import com.google.gson.Gson;

import java.util.Map;

public class Server {

    private final EndpointHandler endpointHandler;
    private final Javalin javalin;
    public Server() {
        DataAccess dataAccess = new MemoryDataAccess();
        this.endpointHandler = new EndpointHandler(dataAccess);
        this.javalin = Javalin.create(config -> config.staticFiles.add("web"));
        endpointHandler.register(javalin);
        javalin.exception(Exception.class, (e, context) ->
                exceptionHandler(new DataAccessException(200, e.getMessage()), context));
        javalin.exception(DataAccessException.class, this::exceptionHandler);


        // Register your endpoints and exception handlers here.

    }



    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }
    private void exceptionHandler(DataAccessException e, Context context) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        context.status(e.getStatusCode());
        context.json(body);
    }

    public void stop() {
        javalin.stop();
    }
}

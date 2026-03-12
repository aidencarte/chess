package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.*;
import com.google.gson.Gson;

import java.util.Map;

public class Server {

    private EndpointHandler endpointHandler;
    private Javalin javalin;
    public Server() {
        try {
            DataAccess dataAccess = new MySQLDataAccess();
            this.endpointHandler = new EndpointHandler(dataAccess);
            this.javalin = Javalin.create(config -> config.staticFiles.add("web"));
            endpointHandler.register(javalin);
            javalin.exception(DataAccessException.class, this::exceptionHandler);
        } catch (DataAccessException e) {
            System.out.println("Something went wrong " + e.getMessage());
        }


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

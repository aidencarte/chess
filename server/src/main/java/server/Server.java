package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.*;
import com.google.gson.Gson;

import java.util.Map;

public class Server {

    private Javalin javalin;
    public Server() {
        try {
            this.javalin = Javalin.create(config -> config.staticFiles.add("web"));
            DataAccess dataAccess = new MySQLDataAccess();
            EndpointHandler endpointHandler = new EndpointHandler(dataAccess);
            endpointHandler.register(javalin);
            javalin.exception(Exception.class, (e, context) -> exceptionHandler(new DataAccessException(500, e.getMessage()), context));
            javalin.exception(DataAccessException.class, this::exceptionHandler);
        } catch (DataAccessException e) {
            System.out.println("Something went wrong " + e.getMessage());
        }


        // Register your endpoints and exception handlers here.

    }



    public int run(int desiredPort) {
        if(javalin != null) {
            javalin.start(desiredPort);
            return javalin.port();
        }
        return 0;
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

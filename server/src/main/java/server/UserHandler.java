package server;

import dataaccess.AlreadyTakenException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedAccessException;
import model.AuthData;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;
import Service.UserService;
import java.net.http.HttpRequest;
import io.javalin.http.Handler;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

public class UserHandler{

    UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void registerUser(Context context) {
        RegisterRequest registerRequest = gson.fromJson(context.body(), RegisterRequest.class);
        try {
            RegisterResult registerResult = userService.register(registerRequest);
            if (registerResult != null) {
                context.status(200);
                context.contentType("application/json");
                context.result(gson.toJson(registerResult, RegisterResult.class));
            }
        } catch (AlreadyTakenException e) {
            context.status(403);
        } catch (BadRequestException e) {
            context.status(400);
        }
    }

    public void loginUser(@NotNull Context context) {

    }
}


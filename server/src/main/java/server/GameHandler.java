package server;

import Service.UserService;
import com.google.gson.Gson;

public class GameHandler {
    UserService userService;
    private final Gson gson = new Gson();

    public GameHandler(UserService userService) {
        this.userService = userService;
    }

}

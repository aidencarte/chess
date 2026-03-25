package client;

import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;
import model.*;
import ui.ClientState;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;
    private ClientState state = ClientState.LOGGED_OUT;
    private String authToken;
    public Client(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println(WHITE_KNIGHT + " Welcome to CHESS. Sign in to start! " + WHITE_KNIGHT);
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.printf("%s%s\n", RESET_TEXT_COLOR, result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }


    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_BLUE);
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "redraw" -> redraw();
                case "create" -> createGame(params);
                case "quit" -> "quit";
                default -> "Unknown Command";
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws Exception {
        if(state != ClientState.LOGGED_OUT)
        {
            return "You are not logged in";
        }
        if (params.length >= 1) {
            state = ClientState.LOGGED_IN;
            var username = getStringParam("username", params, 0);
            var password = getStringParam("password", params, 1);
            LoginResult loginResult = server.loginUser(new LoginRequest(username, password));
            state = ClientState.LOGGED_IN;
            authToken = loginResult.authToken();

            return String.format("You signed in as %s.", loginResult.username());
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password>");
    }

    public String register(String... params) throws Exception{
        if(state != ClientState.LOGGED_OUT)
        {
            return "Must be logged out to register";
        }
        var username = getStringParam("username", params, 0);
        var password = getStringParam("username", params, 1);
        var email = getStringParam("email", params, 2);

        var registerResult = server.register(new RegisterRequest(username, password, email));
        authToken = registerResult.authToken();
        state = ClientState.LOGGED_IN;
        return String.format("Logged in as %s", username);
    }

    public String logout(String ... params) throws Exception
    {
        if(state == ClientState.LOGGED_OUT)
        {
            return "Unable to logout, you are not logged in";
        }
        if(state != ClientState.LOGGED_IN)
        {
            return "Unable to logout, must not be in game";
        }
        server.logoutUser(authToken);
        state = ClientState.LOGGED_OUT;
        authToken = null;
        return "Logged out";

    }





    public String listPets() throws ResponseException {
        assertSignedIn();
        PetList pets = server.listPets();
        var result = new StringBuilder();
        var gson = new Gson();
        for (Pet pet : pets) {
            result.append(gson.toJson(pet)).append('\n');
        }
        return result.toString();
    }

    public String adoptPet(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            try {
                int id = Integer.parseInt(params[0]);
                Pet pet = getPet(id);
                if (pet != null) {
                    server.deletePet(id);
                    return String.format("%s says %s", pet.name(), pet.sound());
                }
            } catch (NumberFormatException ignored) {
            }
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <pet id>");
    }

    public String adoptAllPets() throws ResponseException {
        assertSignedIn();
        var buffer = new StringBuilder();
        for (Pet pet : server.listPets()) {
            buffer.append(String.format("%s says %s%n", pet.name(), pet.sound()));
        }

        server.deleteAllPets();
        return buffer.toString();
    }

    public String signOut() throws ResponseException {
        assertSignedIn();
        ws.leavePetShop(visitorName);
        state = State.SIGNEDOUT;
        return String.format("%s left the shop", visitorName);
    }

    private Pet getPet(int id) throws ResponseException {
        for (Pet pet : server.listPets()) {
            if (pet.id() == id) {
                return pet;
            }
        }
        return null;
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - signIn <yourname>
                    - quit
                    """;
        }
        return """
                - list
                - adopt <pet id>
                - rescue <name> <CAT|DOG|FROG|FISH>
                - adoptAll
                - signOut
                - quit
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == ClientState.LOGGED_OUT || authToken == null) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }
    private String getStringParam(String paramName, String[] params, int pos) throws Exception
    {
        if(params.length <= pos)
        {
            throw new Exception(String.format("Could not find %s", paramName));
        }
        return params[pos];
    }
}
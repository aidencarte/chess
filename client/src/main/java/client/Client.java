package client;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.*;
import ui.ClientState;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;
    private ClientState state = ClientState.LOGGED_OUT;
    private String authToken = null;
    private String username = null;
    private GameData myGameData = null;
    private GameData[] games = null;

    private ChessGame.TeamColor myTeamColor = ChessGame.TeamColor.WHITE;
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
                case "create" -> createGame(params);
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "redraw" -> redraw();
                case "help" -> help();
                case "quit" -> "quit";
                case "highlight" -> highlight(params);
                default -> "Unknown Command";
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
        public String highlight(String...params) throws Exception{
        var chessPosition = new ChessPosition(params[0]);
        return null;
                }
        public String login(String... params) throws Exception {
        if(state != ClientState.LOGGED_OUT)
        {
            return "You are not logged out";
        }
        if (params.length >= 1) {
            username = getStringParam("username", params, 0);
            var password = getStringParam("password", params, 1);
            LoginResult loginResult = server.loginUser(new LoginRequest(username, password));
            authToken = loginResult.authToken();
            state = ClientState.LOGGED_IN;

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

    public String logout() throws Exception
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





    public String list() throws ResponseException {
        assertSignedIn();
        games = server.listGames(authToken);
        if(games.length == 0)
        {
            return "No games are being played. Type create to be the first!";
        }
        var result = new StringBuilder();
        var localID = 1;
        result.append("Games:\n");
        for (GameData game : games) {
            result.append("_____________________________\n");
            var gameName = game.gameName();
            var blackName = game.blackUsername()!=null ? game.blackUsername() : "Available";
            var whiteName = game.whiteUsername()!=null ? game.whiteUsername() : "Available";
            String curGame = String.format("%d) Game: %s\n White: %s\n Black: %s\n",
                    localID, gameName, whiteName, blackName);
            result.append(curGame);
            localID++;
        }
        return result.toString();
    }

    public String createGame(String ... params) throws Exception
    {
        assertSignedIn();
        var gameName = getStringParam("game name", params, 0);
        server.createGame(gameName, authToken);
        return String.format("Created game: %s", gameName);
    }

    public String join(String ... params) throws Exception
    {
        assertSignedIn();
        if(state != ClientState.LOGGED_IN)
        {
            return "Must not be in game to join another";
        }
        if(games==null)
        {
            return "Try listing games before joining";
        }
        var gameIDInput = getStringParam("gameID", params, 0);
        var gameID = verifyGameIDInput(gameIDInput);
        switch(gameID)
        {
            case -2: return "Invalid game id input";
            case -1: return "Invalid game id";
            default:
        }
        var game = games[gameID - 1];
        if(game == null)
        {
            return "Could not find that game";
        }
        var colorString = getStringParam("team color", params, 1);
        var teamColor = verifyColorString(colorString.toUpperCase());
        if((teamColor == ChessGame.TeamColor.WHITE && game.whiteUsername()!=null) ||
                (teamColor == ChessGame.TeamColor.BLACK && game.blackUsername()!=null))
        {
            return "Could not join game";
        }
        if(teamColor == ChessGame.TeamColor.WHITE)
        {
            game.setWhite(username);
            state = ClientState.WHITE;

        }
        if(teamColor == ChessGame.TeamColor.BLACK)
        {
            game.setBlack(username);
            state = ClientState.BLACK;
        }
        myGameData = server.joinGame(authToken, game.gameID(), teamColor);
        printGame(teamColor, null);
        myTeamColor = teamColor;
        return String.format("Joined %s as %s\n", game.gameName(), teamColor);


    }

    public String observe(String ... params) throws Exception
    {
        assertSignedIn();
        if(state != ClientState.LOGGED_IN)
        {
            throw new Exception("Already in game");
        }
        if(games==null)
        {
            return "Try listing games before observing";
        }
        var inputGameID = getStringParam("gameID", params, 0);
        var gameID = verifyGameIDInput(inputGameID);
        switch(gameID)
        {
            case -2: return "Invalid game id input";
            case -1: return "Invalid game id";
            default:
        }
        myGameData = getGame(games[gameID - 1].gameName());
        state = ClientState.OBSERVING;
        printGame(ChessGame.TeamColor.WHITE, null);
        return String.format("Joined %s as observer", myGameData.gameName());
    }

    private String getGameString(String[] params) throws Exception {
        var gameIDInput = getStringParam("gameID", params, 0);
        var gameID = verifyGameIDInput(gameIDInput);
        switch(gameID)
        {
            case -2:
                return "Invalid game id input";
            case -1:
                return "Invalid game id";
            default:
        }
        var game = getGame(games[gameID - 1].gameName());
        return null;
    }

    public String redraw(String ... params) throws Exception
    {
        assertSignedIn();
        if(state == ClientState.LOGGED_IN)
        {
            throw new Exception("Must be in game to draw board");
        }
        printGame(myTeamColor,null);
        return "";
    }

    private void printGame(ChessGame.TeamColor teamColor, Collection<ChessPosition> highlights)
    {
        var board = myGameData.game().getBoard();
        var outputString = new StringBuilder();
        boolean isWhite = teamColor == ChessGame.TeamColor.WHITE;

        outputString.append(addColumnLabels(isWhite)).append("\n");
        for (int row = 0; row <= 7; row++)
        {
            int rowIndex = isWhite ? 8 - row : row + 1;
            outputString.append(rowIndex).append(" ");
            for (int col = 0; col <=7; col++)
            {
                int colIndex = isWhite ? col + 1 : 8 - col;
                ChessPosition curPos = new ChessPosition(rowIndex, colIndex);
                ChessPiece curPiece = board.getPiece(curPos);
                boolean isLight = (rowIndex + colIndex) % 2 == 1;
                String background = isLight ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREEN;
                String pieceString = " ";
                if(curPiece!=null)
                {
                    pieceString = (curPiece.getTeamColor()== ChessGame.TeamColor.WHITE
                            ? SET_TEXT_COLOR_WHITE : SET_TEXT_COLOR_BLACK) + curPiece;

                }
                outputString.append(background).append(" ").append(pieceString).append(" ").append(RESET_TEXT_COLOR);
            }
            outputString.append(SET_BG_COLOR_BLACK).append(" ").append(rowIndex);
            outputString.append("\n");
        }
        outputString.append(addColumnLabels(isWhite));
        System.out.println(outputString);
    }

    private String addColumnLabels(boolean isWhite) {
        var outputString = new StringBuilder();
        outputString.append("  ");
        for (int i = 0; i <=7; i++)
        {
            char colIndex;
            if(isWhite) {
                colIndex = (char) ('a' + i);
            }
            else {
                colIndex = (char) ('h' - i);
            }
            outputString.append(" ").append(colIndex).append(" ");
        }
        return outputString.toString();
    }

    private ChessGame.TeamColor verifyColorString(String colorString) throws Exception
    {
        if(colorString.equals("WHITE"))
        {
            return ChessGame.TeamColor.WHITE;
        }
        if(colorString.equals("BLACK"))
        {
            return ChessGame.TeamColor.BLACK;
        }
        throw new Exception("Color input invalid");
    }




    private GameData getGame(String gameName) throws ResponseException {
        for (GameData game : server.listGames(authToken)) {
            if (game.gameName().equals(gameName.toLowerCase())) {
                return game;
            }
        }
        return null;
    }

    public String help() {
        switch(state) {
            case LOGGED_OUT:
            return """
                    - login <username> <password>
                    - register <username> <password> <email>
                    - help
                    - quit
                    """;
            case LOGGED_IN:
            return """
                    - logout
                    - create <Game Name>
                    - list
                    - join  <game #> <Color>
                    - quit
                    """;
            case WHITE,BLACK:
                return """
                        -
                        -
                        """;
            case OBSERVING:
                return """
                        -
                        - leave
                        """;
        }
        return "Something went wrong with help";
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
    private int verifyGameIDInput(String gameIDInput){
        var gameID = 0;
        try
        {
            gameID = Integer.parseInt(gameIDInput);
        }
        catch (Exception e)
        {
            return -2;
        }
        if(gameID < 1 || gameID > games.length + 1)
        {
            return -1;
        }
        return gameID;
    }
}
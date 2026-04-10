package client;

import chess.ChessGame;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;
import model.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    static int port;

    private static RegisterRequest existingUser;
    private static RegisterRequest newUser;
    private static LoginRequest existingLogin= new LoginRequest("ExistingUser", "existingUserPassword");
    private static RegisterResult goodUserResult = new RegisterResult("NewUser", "newUserPassword");
    private static String createRequest;
    private String existingAuth;

    @BeforeAll
    public static void init() throws Exception{
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + Integer.toString(port), null);
        existingUser = new RegisterRequest("ExistingUser", "existingUserPassword", "eu@mail.com");
        newUser = new RegisterRequest("NewUser", "newUserPassword", "nu@mail.com");
        createRequest = "testGame";
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    void setup() throws Exception {
        facade.clearDb();
        var registerResult = facade.register(existingUser);
        existingAuth = registerResult.authToken();

    }

    @AfterEach
    void cleanup() throws Exception {
        facade.clearDb();
    }

    @Test
    public void registerPositive() throws ResponseException {
        assertEquals(facade.register(newUser).username(), newUser.username())
        ;
    }

    @Test
    public void registerNegative() throws Exception {
        var loginResult = facade.register(new RegisterRequest("username", "password", "email"));

    }

    @Test
    public void loginPositive() throws Exception{
        assertEquals(existingUser.username(), facade.loginUser(
                new LoginRequest("ExistingUser", "existingUserPassword")).username());
    }

    @Test
    public void loginNegative() {
        assertThrows(Exception.class, () ->
                facade.loginUser(new LoginRequest("username", "bogusPassword")));
    }

@Test
    public void logoutPositive() throws Exception{
        facade.logoutUser(existingAuth);
        assertThrows(Exception.class, () -> facade.createGame("w", existingAuth));
    }
    @Test
    public void logoutNegative() throws Exception {
        facade.logoutUser("nonsense");
        GameData game = facade.createGame("game name", existingAuth);
        assertTrue(game.gameID() != 0);
    }



    @Test
    public void createGame() throws Exception {
        GameData game = facade.createGame("game name", existingAuth);
        assertTrue(game.gameID() != 0);
    }


    @Test
    public void createGameBadAuth() {
        assertThrows(Exception.class, () -> facade.createGame("game name", "nonsense"));
    }


    @Test
    public void listGames() throws Exception {

        var games = facade.listGames(existingAuth);
        assertEquals(0, games.length);

        var gameName = "Example Game";
        facade.createGame(gameName, existingAuth);
        var updatedGames = facade.listGames(existingAuth);
        assertEquals(1, updatedGames.length);
        assertEquals(gameName, updatedGames[0].gameName());
    }


    @Test
    public void listGamesBadAuth() {
        assertThrows(Exception.class, () -> facade.listGames("nonsense"));
    }


    @Test
    public void joinGame() throws Exception {
        var gameName = "Example Game";
        var game = facade.createGame(gameName, existingAuth);
        var joinedGame = facade.joinGame(existingAuth, game.gameID(), ChessGame.TeamColor.WHITE);
        assertEquals(game.gameID(), joinedGame.gameID());
        assertEquals(existingUser.username(), joinedGame.whiteUsername());
    }

    @Test
    public void joinGameNoColor() throws Exception {
        var gameName = "Example Game name";
        var game = facade.createGame(gameName, existingAuth);
        assertThrows(Exception.class, () -> facade.joinGame(existingAuth, game.gameID(), null));
    }

    @Test
    public void joinGameBadAuth() {
        assertThrows(Exception.class, () -> facade.joinGame("nonsense", 3, ChessGame.TeamColor.WHITE));
    }

    @Test
    public void joinGameBadID() throws Exception {
        assertThrows(Exception.class, () -> facade.joinGame(existingAuth, 3, ChessGame.TeamColor.WHITE));
    }



}

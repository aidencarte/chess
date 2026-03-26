package client;

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
    private static RegisterResult goodUserResult = new RegisterResult("NewUser", "newUserPassword");
    private static String createRequest;
    private String existingAuth;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + Integer.toString(port));
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


}

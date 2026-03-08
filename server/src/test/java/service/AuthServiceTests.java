package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.LoginRequest;
import model.RegisterRequest;
import model.UserData;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.jetty.util.log.Log;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import javax.xml.crypto.Data;
import java.util.stream.Stream;

public class AuthServiceTests {

    static Stream<Named<DataAccess>> dataAccessImplementations() {
        return Stream.of(
                Named.of("MemoryDataAccess", new MemoryDataAccess())
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dataAccessImplementations")
    public void login(DataAccess dataAccess) throws Exception {
        var userService = new UserService(dataAccess);
        var registerRequest = new RegisterRequest("aiden", "too many passwords", "default@byu.edu");
        userService.register(registerRequest);
        var loginRequest = new LoginRequest("aiden", "too many passwords");
        var authService = new AuthService(dataAccess);

        assertDoesNotThrow(() -> {
            var authData = authService.createAuth(loginRequest);
            assertNotNull(authData);
            assertFalse((authData.authToken()==null));
        });

    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dataAccessImplementations")
    public void loginBadPassword(DataAccess dataAccess) throws Exception {
        var userService = new UserService(dataAccess);
        var registerRequest = new RegisterRequest("aiden", "too many passwords", "default@byu.edu");
        userService.register(registerRequest);

        var authService = new AuthService(dataAccess);

        assertThrows(DataAccessException.class, () -> {
            var badUser = new LoginRequest("aiden", "");
            var authData = authService.createAuth(badUser);
            assertNotNull(authData);
            assertFalse((authData.authToken()==null));
        });

    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("dataAccessImplementations")
    public void logout(DataAccess dataAccess) throws Exception {
        var userService = new UserService(dataAccess);
        var registerRequest = new RegisterRequest("aiden", "too many passwords", "default@byu.edu");
        var authData = userService.register(registerRequest);

        var authService = new AuthService(dataAccess);

        assertDoesNotThrow(() -> authService.deleteSession(authData.authToken()));

        var gameService = new GameService(dataAccess);
        assertThrows(DataAccessException.class, () -> gameService.listGames(authData.authToken()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dataAccessImplementations")
    public void logoutBadAuthToken(DataAccess dataAccess) {
        var authService = new AuthService(dataAccess);
        assertThrows(DataAccessException.class, () -> authService.deleteSession("bogusToken"));

    }

}
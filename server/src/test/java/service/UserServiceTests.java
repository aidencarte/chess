package service;

import dataaccess.*;
import model.RegisterRequest;
import model.UserData;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class UserServiceTests {

    static Stream<Named<DataAccess>> dataAccessImplementations() {
        return Stream.of(
                Named.of("MemoryDataAccess", new MemoryDataAccess())
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dataAccessImplementations")
    public void registerUser(DataAccess dataAccess) {
        var service = new UserService(dataAccess);
        var registerRequest = new RegisterRequest("juan", "too many secrets", "juan@byu.edu");

        assertDoesNotThrow(() -> {
            var authData = service.register(registerRequest);
            assertNotNull(authData);
            assertFalse((authData.authToken()==null));
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dataAccessImplementations")
    public void registerUserDuplicate(DataAccess dataAccess) {
        var service = new UserService(dataAccess);
        var registerRequest = new RegisterRequest("juan", "too many secrets", "juan@byu.edu");

        assertDoesNotThrow(() -> service.register(registerRequest));
        assertThrows(DataAccessException.class, () -> service.register(registerRequest));
    }
}
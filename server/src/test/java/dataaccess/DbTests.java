package dataaccess;

import model.AuthData;
import model.RegisterRequest;
import model.UserData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Named;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class DbTests {
    protected static DataAccess db;

    @BeforeAll
    static void createDb() throws Exception {
        db = new MySQLDataAccess();
    }

    @AfterAll
    static void deleteDb() throws Exception {
        db.clear();
    }

    @BeforeEach
    public void clearDb() throws Exception {
        db.clear();
    }

    protected UserData randomUser() {
        var name = randomString();
        return new UserData(name, "too many secrets", name + "@byu.edu");
    }

    static Stream<Named<DataAccess>> dataAccessImplementations() {
        return Stream.of(
                Named.of("MemoryDataAccess", new MemoryDataAccess()),
                Named.of("MySqlDataAccess", db)
        );
    }

    // -------------------
    // Positive tests
    // -------------------

    @Test
    void testCreateAndRetrieveUser() throws Exception {
        var user = randomUser();
        db.createUser(new RegisterRequest(user.username(), user.password(), user.email()));

        var retrieved = db.getUser(user.username());
        assertNotNull(retrieved, "User should exist in DB");
        assertEquals(user.username(), retrieved.username());
        assertEquals(user.email(), retrieved.email());
        // Password should match hashed in MySQL; skip equality check if using hashing
    }

    @Test
    void testCreateAndRetrieveAuth() throws Exception {
        var user = randomUser();
        db.createUser(new RegisterRequest(user.username(), user.password(), user.email()));

        var auth = db.createAuth(user.username());
        assertNotNull(auth);
        assertEquals(user.username(), auth.username());

        var retrievedAuth = db.getAuth(auth.authToken());
        assertNotNull(retrievedAuth);
        assertEquals(auth.authToken(), retrievedAuth.authToken());
        assertEquals(auth.username(), retrievedAuth.username());
    }

    @Test
    void testClearDatabase() throws Exception {
        var user = randomUser();
        db.createUser(new RegisterRequest(user.username(), user.password(), user.email()));
        db.clear();

        assertNull(db.getUser(user.username()), "User should be removed after clear");
    }

    // -------------------
    // Negative tests
    // -------------------

    @Test
    void testCreateDuplicateUserFails() {
        var user = randomUser();
        assertDoesNotThrow(() -> db.createUser(new RegisterRequest(user.username(), user.password(), user.email())));

        var ex = assertThrows(DataAccessException.class, () -> db.createUser(new RegisterRequest(user.username(),
                user.password(), user.email())));
        assertTrue(ex.getStatusCode() == 403||ex.getMessage().contains("duplicate"));
    }

    @Test
    void testGetNonexistentUserReturnsNull() throws Exception {
        var retrieved = db.getUser("nonexistent_user_" + randomString());
        assertNull(retrieved, "Should return null for non-existent user");
    }

    @Test
    void testGetNonexistentAuthReturnsNull() throws Exception {
        var auth = db.getAuth("invalid_auth_" + randomString());
        assertNull(auth, "Should return null for non-existent auth token");
    }

    @Test
    void testDeleteAuthToken() throws Exception {
        var user = randomUser();
        db.createUser(new RegisterRequest(user.username(), user.password(), user.email()));
        var auth = db.createAuth(user.username());

        db.deleteAuth(auth.authToken());
        var retrieved = db.getAuth(auth.authToken());
        assertNull(retrieved, "Auth token should be deleted");
    }
    public static String randomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
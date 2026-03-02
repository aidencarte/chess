package dataaccess;
import model.UserData;
import model.RegisterRequest;
public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    void createUser(RegisterRequest registerRequest) throws DataAccessException;
    void clear();
}
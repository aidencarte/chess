package Service;

import dataaccess.*;
import model.*;
import java.util.UUID;

public class AuthService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public AuthService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void createAuth(AuthData authData) {
        authDAO.createAuth(authData);
    }
    public void deleteAuth(AuthData authData)
    {
        authDAO.deleteAuth(authData.authToken());
    }
    public void getAuth(AuthData authData) throws DataAccessException, UnauthorizedAccessException
    {
        try {
            AuthData retrieve = authDAO.getAuth(authData.authToken());
            if(retrieve ==null)
            {
                throw new UnauthorizedAccessException("Unauthorized Access");
            }
        }
        catch (DataAccessException e)
        {
            throw new DataAccessException(e.getMessage());
        }
    }

}




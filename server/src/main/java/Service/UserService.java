package Service;

import dataaccess.*;
import model.*;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(MemoryUserDAO userDAO, AuthDAO authDAO)
    {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(RegisterRequest registerRequest) throws BadRequestException {
        try{
            userDAO.createUser(registerRequest);
        }
        catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }
        String authToken = generateToken();
        AuthData authData = new AuthData(registerRequest.username(), authToken);
        authDAO.createAuth(authData);

        return new RegisterResult(registerRequest.username(), authToken);
    }

    public LoginResult login(LoginRequest loginRequest)throws UnauthorizedAccessException {
        boolean validLogin = false;
        try{
            UserData userData = userDAO.getUser(loginRequest.username());
            if(userData.password().equals(loginRequest.password())) validLogin = true;

        }
        catch (DataAccessException e)
        {
            throw new UnauthorizedAccessException(e.getMessage());
        }
        if(validLogin)
        {
            String authToken = generateToken();
            authDAO.createAuth(new AuthData(authToken, loginRequest.username()));
            return new LoginResult(loginRequest.username(), authToken);
        }
        else {
            throw new UnauthorizedAccessException("Password Does Not Match");
        }
    }
    public void logout(LogoutRequest logoutRequest) {

    }
}

import Service.UserService;
import server.Server;
import dataaccess.*;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService(new MemoryUserDAO(),new MemoryAuthDAO());
        Server server = new Server(userService);
        server.run(8080);


        System.out.println("♕ 240 Chess Server");
    }
}
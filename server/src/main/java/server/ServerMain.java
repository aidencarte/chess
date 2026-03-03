package server;

import Service.UserService;
import chess.*;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;

public class ServerMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        UserService userService = new UserService(new MemoryUserDAO(),new MemoryAuthDAO());
        Server server = new Server(userService);
        int port = 8080;
        server.run(port);
        System.out.println("♕ 240 Chess Server: " + piece);
    }
}

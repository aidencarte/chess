package client;

import chess.*;

public class ClientMain{
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        try {
            new Client("http://localhost:8080").run();
        }
        catch (Exception e)
        {
            System.out.print(e.toString());
        }
        System.out.println("♕ 240 Chess Client: " + piece);
    }
}

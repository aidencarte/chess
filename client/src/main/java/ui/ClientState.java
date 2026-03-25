package ui;
import chess.ChessGame;

public enum ClientState {
    LOGGED_OUT,
    LOGGED_IN,
    WHITE,
    BLACK,
    OBSERVING;

    public Boolean isTurn(ChessGame.TeamColor teamColor){
        return(teamColor.toString().equals(this.toString()));
    }

}

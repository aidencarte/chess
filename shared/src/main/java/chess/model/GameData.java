package chess.model;

import chess.ChessGame;
public record GameData(int gameID, String usernameWhite, String usernameBlack, String gameName, ChessGame game) {};
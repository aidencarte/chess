package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.List;

public class ChessPiece {

    @Override
    public String toString() {
        switch(getPieceType())
        {
            case QUEEN:
                if(getTeamColor()== ChessGame.TeamColor.WHITE)
                    return "Q";
                else return "q";
                break;
            case KING:
                if(getTeamColor()== ChessGame.TeamColor.WHITE)
                    return "K";
                else return "k";
                break;
            case ROOK:
                if(getTeamColor()== ChessGame.TeamColor.WHITE)
                    return "R";
                else return "r";
                break;
            case BISHOP:
                if(getTeamColor()== ChessGame.TeamColor.WHITE)
                    return "B";
                else return "b";
                break;
            case KNIGHT:
                if(getTeamColor()== ChessGame.TeamColor.WHITE)
                    return "N";
                else return "n";
                break;
            case PAWN:
                if(getTeamColor()== ChessGame.TeamColor.WHITE)
                    return "P";
                else return "p";
                break;
            case null, default:
                return " ";

        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN;


    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        PieceMovesCalculator newCalc = new PieceMovesCalculator(myPosition, board.getPiece(myPosition), board);

        return newCalc.getPossibleMoves();
    }
}



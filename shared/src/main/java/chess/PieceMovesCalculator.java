package chess;
import java.util.Collection;
import java.util.Objects;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class PieceMovesCalculator {
    private final ChessPiece piece;
    ChessPosition[] possibleMoves;

    public PieceMovesCalculator(ChessPiece piece) {
        this.piece = piece;
    }
}

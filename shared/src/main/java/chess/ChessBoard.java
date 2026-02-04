package chess;

import java.util.Arrays;
import java.util.Objects;

import static chess.ChessPiece.PieceType;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {


        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessGame.TeamColor currentColor = ChessGame.TeamColor.WHITE;
        for(int i = 1; i<= squares.length; i++)
        {
            if(i ==6) currentColor = ChessGame.TeamColor.BLACK;
            switch(i) {
                case 1,8:
                    for (int j = 1; j <= squares[i-1].length; j++) {
                        switch(j){
                            case 1, 8:
                                addPiece(new ChessPosition(i,j), new ChessPiece(currentColor,PieceType.ROOK));
                                break;
                            case 2, 7:
                                addPiece(new ChessPosition(i,j), new ChessPiece(currentColor,PieceType.KNIGHT));
                                break;
                            case 3, 6:
                                addPiece(new ChessPosition(i,j), new ChessPiece(currentColor,PieceType.BISHOP));
                                break;
                            case 4:
                                addPiece(new ChessPosition(i,j), new ChessPiece(currentColor,PieceType.QUEEN));
                                break;
                            case 5:
                                addPiece(new ChessPosition(i,j), new ChessPiece(currentColor,PieceType.KING));
                                break;
                        }
                    }
                    break;

                case 2,7:
                    for (int j = 1; j <= squares[i-1].length; j++) {
                        addPiece(new ChessPosition(i,j), new ChessPiece(currentColor, PieceType.PAWN));
                    }

                break;
                default:
                    for(int j = 1; j <= squares[i-1].length;j++) squares[i-1][j-1] = null;
                    break;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }

}


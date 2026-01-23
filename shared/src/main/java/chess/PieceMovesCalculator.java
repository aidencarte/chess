package chess;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class PieceMovesCalculator {
    private final ChessPiece.PieceType type;
    private final ChessPosition start;
    private final ChessBoard board;
    private final ChessPiece piece;
    private final ChessGame.TeamColor team;
    private boolean capturedPiece;
    private final int[][] allMoves = {{0,1},{0,-1},//straight 0 1
            {1,1},{1,-1},{-1,-1},{-1,1},//diagonal 2 3 4 5
            {1,0},{-1,0},//sideways 6 7
            {-1,2},{1,2},{2,1},{2,-1},{1,-2},{-1,-2},{-2,-1},{-2,1}};//knight moves 8-15
    HashSet<ChessMove> possibleMoves;

    public PieceMovesCalculator(ChessPosition start, ChessPiece piece, ChessBoard board) {

        this.type = piece.getPieceType();
        this.start = start;
        this.board = board;
        this.piece = piece;
        this.team = piece.getTeamColor();
    }
    private boolean isValidSquare(ChessPosition position)
    {
        boolean sameTeam = false;
        if((position.getRow() >= 1 && position.getRow() <= 8) &&
        (position.getColumn() >= 1 && position.getColumn() <= 8))
        {
            if(capturedPiece)
            {
                capturedPiece = false;
                return false;
            }
            if(board.getPiece(position) != null && board.getPiece(position).getTeamColor() == team) sameTeam = true;
            if(board.getPiece(position) != null && board.getPiece(position).getTeamColor() != team) capturedPiece = true;
            return !sameTeam;
        }
        return false;
    }

    public HashSet<ChessMove> getPossibleMoves() {
        possibleMoves = calcPossibleMoves(getMoveDirections());
        return possibleMoves;
    }

    public HashSet<ChessMove> calcPossibleMoves(ArrayList<int[]> moveDirections)
    {
        var possibleMoves = new HashSet<ChessMove>();
        int curRow = start.getRow();
        int curCol = start.getColumn();
        ChessPosition possiblePosition;

        switch(type)
        {
            case PAWN:

                break;
            case KING:
                for (int[] moveDirection : moveDirections) {
                    possiblePosition = new ChessPosition(curRow + moveDirection[0],
                            curCol + moveDirection[1]);
                    if(isValidSquare(possiblePosition)) possibleMoves.add(new ChessMove(start,possiblePosition,null));
                }
                break;
            case QUEEN:
                for (int[] moveDirection : moveDirections) {
                    possiblePosition = new ChessPosition(curRow + moveDirection[0],
                            curCol + moveDirection[1]);
                    while(isValidSquare(possiblePosition)) {
                        possiblePosition = new ChessPosition(curRow + moveDirection[0],
                                curCol + moveDirection[1]);
                        if(isValidSquare(possiblePosition))possibleMoves.add(new ChessMove(start, possiblePosition, null));
                        curRow += moveDirection[0];
                        curCol += moveDirection[1];
                    }
                    curRow = start.getRow();
                    curCol = start.getColumn();
                }
                break;
            case BISHOP:
                for (int[] moveDirection : moveDirections) {
                    possiblePosition = new ChessPosition(curRow + moveDirection[0],
                            curCol + moveDirection[1]);
                    while(isValidSquare(possiblePosition)) {
                        possiblePosition = new ChessPosition(curRow + moveDirection[0],
                                curCol + moveDirection[1]);
                        if(isValidSquare(possiblePosition))possibleMoves.add(new ChessMove(start, possiblePosition, null));
                        curRow += moveDirection[0];
                        curCol += moveDirection[1];
                    }
                    curRow = start.getRow();
                    curCol = start.getColumn();
                }
                break;
            case KNIGHT:
                for (int[] moveDirection : moveDirections) {
                    capturedPiece = false;
                    possiblePosition = new ChessPosition(curRow + moveDirection[0],
                            curCol + moveDirection[1]);
                    if(isValidSquare(possiblePosition)) possibleMoves.add(new ChessMove(start,possiblePosition,null));
                }
                break;
            case ROOK:
                for (int[] moveDirection : moveDirections) {
                    possiblePosition = new ChessPosition(curRow + moveDirection[0],
                            curCol + moveDirection[1]);
                    while(isValidSquare(possiblePosition)) {
                        possiblePosition = new ChessPosition(curRow + moveDirection[0],
                                curCol + moveDirection[1]);
                        if(isValidSquare(possiblePosition))possibleMoves.add(new ChessMove(start, possiblePosition, null));
                        curRow += moveDirection[0];
                        curCol += moveDirection[1];
                    }
                    curRow = start.getRow();
                    curCol = start.getColumn();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        return possibleMoves;
    }
    public ArrayList<int[]> getMoveDirections() {
        ArrayList<int[]> moveDirections = new ArrayList<int[]>();
        switch(type)
        {
            case PAWN:
                if(team == ChessGame.TeamColor.WHITE) {
                    moveDirections.add(allMoves[0]);
                    moveDirections.add(allMoves[2]);
                    moveDirections.add(allMoves[5]);
                }
                else {
                    moveDirections.add(allMoves[0]);
                    moveDirections.add(allMoves[2]);
                    moveDirections.add(allMoves[5]);
                }
                break;
            case BISHOP:
                moveDirections.add(allMoves[2]);
                moveDirections.add(allMoves[3]);
                moveDirections.add(allMoves[4]);
                moveDirections.add(allMoves[5]);

                break;
            case KNIGHT:
                moveDirections.add(allMoves[8]);
                moveDirections.add(allMoves[9]);
                moveDirections.add(allMoves[10]);
                moveDirections.add(allMoves[11]);
                moveDirections.add(allMoves[12]);
                moveDirections.add(allMoves[13]);
                moveDirections.add(allMoves[14]);
                moveDirections.add(allMoves[15]);
                break;
            case QUEEN:
                moveDirections.add(allMoves[0]);
                moveDirections.add(allMoves[1]);
                moveDirections.add(allMoves[2]);
                moveDirections.add(allMoves[3]);
                moveDirections.add(allMoves[4]);
                moveDirections.add(allMoves[5]);
                moveDirections.add(allMoves[6]);
                moveDirections.add(allMoves[7]);
                break;
            case KING:
                moveDirections.add(allMoves[0]);
                moveDirections.add(allMoves[1]);
                moveDirections.add(allMoves[2]);
                moveDirections.add(allMoves[3]);
                moveDirections.add(allMoves[4]);
                moveDirections.add(allMoves[5]);
                moveDirections.add(allMoves[6]);
                moveDirections.add(allMoves[7]);
                break;
            case ROOK:
                moveDirections.add(allMoves[0]);
                moveDirections.add(allMoves[1]);
                moveDirections.add(allMoves[6]);
                moveDirections.add(allMoves[7]);
                break;
            case null, default:
                break;
        }


        return moveDirections;
    }

}

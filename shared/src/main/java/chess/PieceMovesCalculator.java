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
    private boolean capturedPieceLastTurn;
    private final int[][] allMoves = {{0,1},{0,-1},//sideways 0 1
            {1,1},{1,-1},{-1,-1},{-1,1},//diagonal 2 3 4 5
            {1,0},{-1,0},//straight 6 7
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
        boolean valid = false;
        if((position.getRow() >= 1 && position.getRow() <= 8) &&
        (position.getColumn() >= 1 && position.getColumn() <= 8))
        {
            valid = true;
            if(board.getPiece(position) != null && board.getPiece(position).getTeamColor() == team) valid = false;
            if(board.getPiece(position) != null && board.getPiece(position).getTeamColor() != team) {
                valid = true;
                capturedPieceLastTurn = true;
            }

        }
        return valid;
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
        boolean invalidMove;

        switch(type)
        {
            case PAWN:
                possibleMoves = pawnMoveCalc(moveDirections,curRow,curCol);

                break;
            case KING,KNIGHT:
                possibleMoves = smallMoveCalc(moveDirections,curRow, curCol);
                break;
            case QUEEN, BISHOP, ROOK:
                for (int[] moveDirection : moveDirections) {
                    invalidMove = false;
                    while(!invalidMove)
                    {
                        possiblePosition = new ChessPosition(curRow + moveDirection[0],
                                curCol + moveDirection[1]);
                        if(!capturedPieceLastTurn) {
                            if (isValidSquare(possiblePosition))
                                possibleMoves.add(new ChessMove(start, possiblePosition, null));
                            else invalidMove = true;
                        }
                        else
                        {
                            invalidMove = true;
                            capturedPieceLastTurn = false;
                        }
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

    private HashSet<ChessMove> pawnMoveCalc(ArrayList<int[]> moveDirections, int curRow, int curCol){
        ChessGame.TeamColor teamColor = piece.getTeamColor();
        ChessPosition possiblePosition;
        var possibleMoves = new HashSet<ChessMove>();
        possiblePosition = new ChessPosition(curRow+moveDirections.get(0)[0],curCol+moveDirections.get(0)[1]);
        if(isValidSquare(possiblePosition) && board.getPiece(possiblePosition) ==null) //check for regular move forward
            possibleMoves.add(new ChessMove(start, possiblePosition, null));
        possiblePosition = new ChessPosition(curRow+moveDirections.get(1)[0],curCol+moveDirections.get(1)[1]);
       //check for capture left
        if(isValidSquare(possiblePosition)&&(board.getPiece(possiblePosition)!=null&&board.getPiece(possiblePosition).getTeamColor()!=teamColor)){
            possibleMoves.add(new ChessMove(start, possiblePosition,null));
        }
        //check for capture right
        possiblePosition = new ChessPosition(curRow+moveDirections.get(2)[0],curCol+moveDirections.get(2)[1]);
        if(isValidSquare(possiblePosition)&&(board.getPiece(possiblePosition)!=null&&board.getPiece(possiblePosition).getTeamColor()!=teamColor)){
            possibleMoves.add(new ChessMove(start, possiblePosition,null));
        }
        //check for initial move 2
        possiblePosition = new ChessPosition(curRow+2*moveDirections.get(0)[0],curCol+2*moveDirections.get(0)[1]);
        if(isValidSquare(possiblePosition)&&(teamColor== ChessGame.TeamColor.WHITE && curRow==2)||(teamColor== ChessGame.TeamColor.BLACK &&curRow==7)){
            if(board.getPiece((possiblePosition))==null && board.getPiece(new ChessPosition(curRow+moveDirections.get(0)[0],curCol+moveDirections.get(0)[1]))==null)
                possibleMoves.add(new ChessMove(start, possiblePosition,null));
        }
        if((teamColor== ChessGame.TeamColor.WHITE&& curRow == 7)||(teamColor== ChessGame.TeamColor.BLACK&&curRow==2) )
        {
            var promotionMoves = new HashSet<ChessMove>();
            ChessPosition curPos;
            for(ChessMove currentMove : possibleMoves)
            {
                curPos = currentMove.getEndPosition();
                promotionMoves.add(new ChessMove(start, curPos, ChessPiece.PieceType.BISHOP));
                promotionMoves.add(new ChessMove(start, curPos, ChessPiece.PieceType.KNIGHT));
                promotionMoves.add(new ChessMove(start, curPos, ChessPiece.PieceType.ROOK));
                promotionMoves.add(new ChessMove(start, curPos, ChessPiece.PieceType.QUEEN));
            }
            return promotionMoves;
        }
        return possibleMoves;
    }
    private HashSet<ChessMove> smallMoveCalc(ArrayList<int[]> moveDirections, int curRow, int curCol) {
        ChessPosition possiblePosition;
        var possibleMoves = new HashSet<ChessMove>();
        for (int[] moveDirection : moveDirections) {
            possiblePosition = new ChessPosition(curRow + moveDirection[0],
                    curCol + moveDirection[1]);
            if (isValidSquare(possiblePosition)) possibleMoves.add(new ChessMove(start, possiblePosition, null));
        }
        return possibleMoves;
    }
    public ArrayList<int[]> getMoveDirections() {
        ArrayList<int[]> moveDirections = new ArrayList<int[]>();
        switch(type)
        {
            case PAWN:
                if(team == ChessGame.TeamColor.WHITE) {
                    moveDirections.add(allMoves[6]);
                    moveDirections.add(allMoves[2]);
                    moveDirections.add(allMoves[3]);
                }
                else {
                    moveDirections.add(allMoves[7]);
                    moveDirections.add(allMoves[4]);
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

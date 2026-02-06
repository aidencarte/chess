    package chess;

    import java.util.Collection;
    import java.util.HashSet;
    import java.util.Objects;

    /**
     * For a class that can manage a chess game, making moves on a board
     * <p>
     * Note: You can add to this class, but you may not alter
     * signature of the existing methods.
     */
    public class ChessGame {
        private TeamColor currentTurn;
        private ChessBoard myBoard;


        public ChessGame() {
        this.currentTurn = TeamColor.WHITE;
        this.myBoard = new ChessBoard();
        }

        /**
         * @return Which team's turn it is
         */
        public TeamColor getTeamTurn() {
            return currentTurn;
        }

        /**
         * Set's which teams turn it is
         *
         * @param team the team whose turn it is
         */
        public void setTeamTurn(TeamColor team) {

            currentTurn = team;
        }

        /**
         * Enum identifying the 2 possible teams in a chess game
         */
        public enum TeamColor {
            WHITE,
            BLACK
        }


        /**
         * Gets a valid moves for a piece at the given location
         *
         * @param startPosition the piece to get valid moves for
         * @return Set of valid moves for requested piece, or null if no piece at
         * startPosition
         */
        public Collection<ChessMove> validMoves(ChessPosition startPosition) {
            ChessPiece piece = myBoard.getPiece(startPosition);
            Collection<ChessMove> possibleMoves = piece.pieceMoves(myBoard,startPosition);
            Collection<ChessMove> validMoves = HashSet.newHashSet(possibleMoves.size());
            ChessBoard startBoard = new ChessBoard(myBoard);
            for(ChessMove possibleMove : possibleMoves)
            {
                setBoard(startBoard);
                ChessBoard newBoard = new ChessBoard(myBoard);

                if(myBoard.getPiece(possibleMove.getEndPosition())==null ||
                        (myBoard.getPiece(possibleMove.getEndPosition())!=null &&
                                myBoard.getPiece(possibleMove.getEndPosition()).getTeamColor()!=currentTurn)) {
                    myBoard.addPiece(possibleMove.getEndPosition(), piece);
                    myBoard.addPiece(possibleMove.getStartPosition(), null);
                    if (!isInCheck(currentTurn)) validMoves.add(possibleMove);
                }
            }
            setBoard(startBoard);

            return validMoves;
        }

        /**
         * Makes a move in a chess game
         *
         * @param move chess move to perform
         * @throws InvalidMoveException if move is invalid
         */
        public void makeMove(ChessMove move) throws InvalidMoveException {

            throw new RuntimeException("Not implemented");
        }


        public boolean checkForDefault()
        {

            ChessBoard processingWaste = new ChessBoard(myBoard);
            processingWaste.resetBoard();
            for(int i = 1;i<+8;i++)
            {
                for(int j = 1; j<+8;j++)
                {
                    if(myBoard.getPiece(new ChessPosition(i,j)) != processingWaste.getPiece(new ChessPosition(i,j))) return false;
                }
            }
            return  true;
        }

        /**
         * Determines if the given team is in check
         *
         * @param teamColor which team to check for check
         * @return True if the specified team is in check
         */


        public boolean isInCheck(TeamColor teamColor) {
            if(checkForDefault())return false;
            ChessPiece currentPiece;
            Collection<ChessMove> currentMoves;
            ChessPosition currentPosition, kingPosition = findKingPosition(teamColor);

           for(int i = 1;i<=8;i++)
           {
            for(int j = 1;j<= 8;j++)
            {
                currentPosition = new ChessPosition(i,j);
                currentPiece = myBoard.getPiece(currentPosition);
                if(currentPiece != null && currentPiece.getTeamColor()!=teamColor)
                {
                    currentMoves = currentPiece.pieceMoves(myBoard, currentPosition);
                    for(ChessMove chessMove : currentMoves)
                    {   //for some reason position comparing does not work here
                        if(kingPosition.getRow() == chessMove.getEndPosition().getRow() &&
                                kingPosition.getColumn() == chessMove.getEndPosition().getColumn()) return true;
                    }
                }
            }
           }
            return false;
        }


        private ChessPosition findKingPosition(TeamColor teamColor)
        {
            ChessPosition currentPosition, kingPosition = null;
            for(int i = 1;i<=8;i++) {
                for (int j = 1; j <= 8; j++) {
                    currentPosition = new ChessPosition(i,j);
                    if(myBoard.getPiece(currentPosition)!=null && myBoard.getPiece(currentPosition).getPieceType()== ChessPiece.PieceType.KING &&
                            myBoard.getPiece(currentPosition).getTeamColor()==teamColor) kingPosition = currentPosition;
                }
            }
            return kingPosition;
        }

        /**
         * Determines if the given team is in checkmate
         *
         * @param teamColor which team to check for checkmate
         * @return True if the specified team is in checkmate
         */
        public boolean isInCheckmate(TeamColor teamColor) {
            return isInCheck(teamColor) && doesNotHaveMoves(teamColor);
        }
        public boolean doesNotHaveMoves(TeamColor teamColor)
        {
            ChessPosition currentPosition;
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    currentPosition = new ChessPosition(i, j);
                    if (myBoard.getPiece(currentPosition) != null &&
                            (myBoard.getPiece(currentPosition).getTeamColor() == teamColor)) {
                        if (!validMoves(currentPosition).isEmpty()) return false;
                    }
                }
            }
            return true;
        }

        /**
         * Determines if the given team is in stalemate, which here is defined as having
         * no valid moves while not in check.
         *
         * @param teamColor which team to check for stalemate
         * @return True if the specified team is in stalemate, otherwise false
         */
        public boolean isInStalemate(TeamColor teamColor) {
            if(isInCheck(teamColor)) return false;
            return doesNotHaveMoves(teamColor);

        }

        /**
         * Sets this game's chessboard with a given board
         *
         * @param board the new board to use
         */
        public void setBoard(ChessBoard board) {

            for(int i =0;i<8;i++)
            {
                for(int j = 0; j<8;j++)
                {
                    myBoard.squares[i][j] = board.squares[i][j];
                }
            }
        }

        /**
         * Gets the current chessboard
         *
         * @return the chessboard
         */
        public ChessBoard getBoard() {

            return myBoard;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ChessGame chessGame = (ChessGame) o;
            return currentTurn == chessGame.currentTurn && Objects.equals(myBoard, chessGame.myBoard);
        }

        @Override
        public int hashCode() {
            return Objects.hash(currentTurn, myBoard);
        }
    }

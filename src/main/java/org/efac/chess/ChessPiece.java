package org.efac.chess;

import org.efac.chess.BoardLocation;
import org.efac.chess.Chessboard;

public abstract class ChessPiece {
    BoardLocation location;
    Chessboard board;
    
    public BoardLocation getLocation() { return location; }
    public Chessboard getChessboard() { return board; }

    public ChessPiece(BoardLocation location, Chessboard board) {
        if (location == null) {
            throw new NullPointerException("location cannot be null");
        }

        if (board == null) {
            throw new NullPointerException("board cannot be null");
        }

        board.checkIfInBounds(location);
        
        this.location = location;
        this.board = board;
    }

    public ChessPiece(int xLocation, int yLocation, Chessboard board) {
        this.location = board.createLocation(xLocation, yLocation);
        this.board = board;
    }

    public abstract BoardLocation[] getPossibleMoves();
}

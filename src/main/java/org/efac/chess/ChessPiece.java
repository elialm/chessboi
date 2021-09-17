package org.efac.chess;

import org.efac.chess.BoardLocation;

public abstract class ChessPiece {
    BoardLocation location;
    
    public BoardLocation getLocation() { return location; }

    public ChessPiece(BoardLocation location) {
        this.location = location;
    }

    public abstract BoardLocation[] getPossibleMoves();
}

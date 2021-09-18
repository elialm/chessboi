package org.efac.chess;

// import org.efac.chess.BoardLocation;
import java.util.ArrayList;

public abstract class ChessPiece {
    public abstract ArrayList<BoardLocation> getPossibleMoves(BoardLocation location);
}

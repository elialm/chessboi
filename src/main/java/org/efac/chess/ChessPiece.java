package org.efac.chess;

import org.efac.chess.BoardLocation;
import org.efac.chess.Chessboard;

public abstract class ChessPiece {
    public abstract BoardLocation[] getPossibleMoves(BoardLocation location);
}

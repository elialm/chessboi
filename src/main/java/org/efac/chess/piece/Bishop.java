package org.efac.chess.piece;

import org.efac.chess.ChessPiece;
import org.efac.chess.BoardLocation;
import org.efac.chess.Chessboard;

public class Bishop extends ChessPiece {
    public Bishop(BoardLocation location, Chessboard board) {
        super(location, board);
    }

    public Bishop(int xLocation, int yLocation, Chessboard board) {
        super(xLocation, yLocation, board);
    }

    public BoardLocation[] getPossibleMoves() {
        // TODO: implement
        return null;
    }
}

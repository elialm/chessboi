package org.efac.chess.piece;

import org.efac.chess.ChessPiece;

import java.util.ArrayList;

import org.efac.chess.BoardLocation;
import org.efac.chess.Chessboard;

public class Bishop extends ChessPiece {
    /**
     *      p  -  -  -  p
     *      -  p  -  p  -
     *      -  -  X  -  - 
     *      -  p  -  p  -
     *      p  -  -  -  p
     */
    public BoardLocation[] getPossibleMoves(BoardLocation location) {
        ArrayList<BoardLocation> possibleMoves = new ArrayList<BoardLocation>();

        for (BoardLocation nextLocation = location.getRelativeLocation(1, 1); nextLocation != null; nextLocation = nextLocation.getRelativeLocation(1, 1)) {
            System.out.println(nextLocation);
        }

        return null;
    }
}

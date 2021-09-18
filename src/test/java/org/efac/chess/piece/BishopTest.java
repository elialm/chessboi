package org.efac.chess.piece;

import org.efac.chess.Chessboard;
import org.efac.chess.ChessPiece.Color;
import org.efac.chess.BoardLocation;
import org.efac.chess.piece.Bishop;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class BishopTest {
    
    @Test
    void testGettingPossibleMoves() {
        Chessboard board = new Chessboard(8, 8);

        board.getLocation(4, 4).setPiece(new Bishop(Color.WHITE));
        ArrayList<BoardLocation> possibleLocations = board.getLocation(4, 4).getPossibleMoves();

        return;
    }
}

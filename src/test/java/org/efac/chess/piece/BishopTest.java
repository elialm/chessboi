package org.efac.chess.piece;

import org.efac.chess.Chessboard;
import org.efac.chess.BoardLocation;
import org.efac.chess.piece.Bishop;
import org.junit.jupiter.api.Test;

public class BishopTest {
    
    @Test
    void demo() {
        Chessboard board = new Chessboard(8, 8);

        Bishop bishop = new Bishop();
        board.getLocation(4, 4).setPiece(bishop);
        board.getLocation(4, 4).getPossibleMoves();
    }
}

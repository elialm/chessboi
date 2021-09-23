package org.efac.chess;

import java.util.Arrays;
import java.util.ArrayList;

import org.efac.chess.ChessPiece.Color;
import org.efac.chess.piece.Bishop;
import org.efac.chess.piece.Queen;

import org.junit.jupiter.api.Test;

public class DominationSolverTest {
    
    @Test
    public void testSolver() {
        ChessPiece pieces[] = {
            new Queen(Color.WHITE),
            new Bishop(Color.WHITE),
            new Bishop(Color.WHITE),
            new Bishop(Color.WHITE)
        };
        DominationSolver solver = new DominationSolver(5, 5, Arrays.asList(pieces));

        // ArrayList<Chessboard> solutions = solver.solve();

        return;
    }
}

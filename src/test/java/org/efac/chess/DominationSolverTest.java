package org.efac.chess;

import java.util.Arrays;

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

        for (Chessboard solution : solver.getThreadedSolutions()) {
            System.out.println(solution);
        }

        return;
    }
}

package org.efac.chess;

import java.util.ArrayList;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;

public class DominationSolver {
    private ArrayList<Chessboard> chessboards;
    private int chessboardCombinations;

    public DominationSolver(int boardWidth, int boardHeight, ArrayList<ChessPiece> pieces) {
        chessboardCombinations = calculateNumberOfBoardCombinations(boardWidth, boardHeight, pieces);
        chessboards = new ArrayList<>(chessboardCombinations);
    }

    private static int factorial(int n) {
        if (n < 0) {
            return 0;
        }

        final Function<Integer, Integer> inner_factorial = new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer m) {
                return n == 1 ? 1 : m * apply(m);
            }
        };
        
        return n == 1 ? 1 : n * inner_factorial.apply(n);
    }

    private static int countPiecesOfType(ArrayList<ChessPiece> pieces, ChessPiece.Type type) {
        return Iterators.size(FluentIterable.from(pieces).filter(piece -> piece.getType() == type).iterator());
    }

    private static int calculateNumberOfBoardCombinations(int boardWidth, int boardHeight, ArrayList<ChessPiece> pieces) {
        int cellCount = boardWidth * boardHeight;
        int numberOfQueens = countPiecesOfType(pieces, ChessPiece.Type.QUEEN);
        int numberOfBishops = countPiecesOfType(pieces, ChessPiece.Type.BISHOP);

        return factorial(cellCount) / (factorial(numberOfQueens) * factorial(numberOfBishops) * factorial(cellCount - numberOfQueens - numberOfBishops));
    }
}

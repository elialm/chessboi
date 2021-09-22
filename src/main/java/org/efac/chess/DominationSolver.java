package org.efac.chess;

/**
 *  MIT License

    Copyright (c) 2021 Elijah Almeida Coimbra

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.

 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;
import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class DominationSolver {
    private final int boardWidth;
    private final int boardHeight;
    private final ArrayList<Chessboard> chessboards;
    private final int chessboardCombinations;

    public DominationSolver(int boardWidth, int boardHeight, ArrayList<ChessPiece> pieces) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        chessboardCombinations = calculateNumberOfBoardCombinations(pieces);
        chessboards = new ArrayList<>(chessboardCombinations);
    }

    private int calculateNumberOfBoardCombinations(ArrayList<ChessPiece> pieces) {
        final int cellCount = boardWidth * boardHeight;
        final int numberOfQueens = countPiecesOfType(pieces, ChessPiece.Type.QUEEN);
        final int numberOfBishops = countPiecesOfType(pieces, ChessPiece.Type.BISHOP);

        return factorial(cellCount) / (factorial(numberOfQueens) * factorial(numberOfBishops) * factorial(cellCount - numberOfQueens - numberOfBishops));
    }

    private ArrayList<ImmutableList<Point>> generateBoardLocationCombinations(int numberOfPieces) {
        if (numberOfPieces == 0) {
            return new ArrayList<>();
        }
        
        final int cellCount = boardWidth * boardHeight;
        final int numberOfCombinations = factorial(cellCount) / (factorial(numberOfPieces) * factorial(cellCount - numberOfPieces));
        ArrayList<ImmutableList<Point>> boardLocations = new ArrayList<>(numberOfCombinations);

        ArrayList<Integer> currentCombination = Lists.newArrayList(range(0, numberOfPieces));
        ArrayList<Integer> lastCombination = Lists.newArrayList(range(cellCount - numberOfPieces, cellCount));

        while (!lastCombination.containsAll(currentCombination)) {
            boardLocations.add(
                FluentIterable.from(currentCombination)
                                .transform(index -> Point.fromIndex(index, boardWidth, boardHeight))
                                .toList()
            );
            
            for (int i : reversed(range(0, numberOfPieces))) {
                if (currentCombination.get(i) < lastCombination.get(i)) {
                    for (int j : range(i, numberOfPieces)) {
                        currentCombination.set(j, currentCombination.get(j) + 1);
                    }

                    break;
                } else if (currentCombination.get(i) == lastCombination.get(i)) {
                    for (int j : reversed(range(0, i))) {
                        if (currentCombination.get(j) != lastCombination.get(j)) {
                            currentCombination.set(i, currentCombination.get(j) + (i - j));
                            break;
                        }
                    }
                }
            }
        }

        return boardLocations;
    }

    private static Iterable<Integer> range(int startInclusive, int endExclusive) {
        return new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return IntStream.range(startInclusive, endExclusive).iterator();
            }
        };
    }

    private static<T> Iterable<T> reversed(Iterable<T> iterable) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                ArrayList<T> collected = Lists.newArrayList(iterable);
                Collections.reverse(collected);

                return collected.iterator();
            }
        };
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

    
}

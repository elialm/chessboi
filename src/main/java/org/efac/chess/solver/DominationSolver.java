package org.efac.chess.solver;

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

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import org.efac.chess.BoardLocationCombinationComputer;
import org.efac.chess.ChessPiece;
import org.efac.chess.Chessboard;
import org.efac.chess.iter.BoardLocationCombinationIterator;
import org.efac.chess.iter.DominationSolutionIterator;
import org.efac.chess.iter.ThreadedSolutionIterator;
import org.efac.chess.iter.ThreadedSolutionIterable;
import org.efac.func.ReduceFunction;
import org.efac.func.PyIterators;
import org.efac.common.Utility;

public class DominationSolver {
    private final int boardWidth;
    private final int boardHeight;
    private final ArrayList<ChessPiece> pieces;
    private final BigInteger chessboardCombinations;

    public BigInteger getNumberOfChessboardCombinations() {
        return chessboardCombinations;
    }

    public DominationSolver(int boardWidth, int boardHeight, List<ChessPiece> pieces) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        chessboardCombinations = calculateNumberOfBoardCombinations(pieces);
        this.pieces = new ArrayList<>(pieces);
    }

    public Iterable<Chessboard> getSolutions() {
        return new Iterable<Chessboard>() {
            @Override
            public Iterator<Chessboard> iterator() {
                return Iterables.concat(getSolutionIterators(0)).iterator();
            }
        };
    }

    public ThreadedSolutionIterable getThreadedSolutions() {
        return new ThreadedSolutionIterable(
                FluentIterable.from(getSolutionIterators(ThreadedSolutionIterator.PREFERRED_THREAD_COUNT))
                        .transform(iterable -> (DominationSolutionIterator) iterable.iterator()).toList());
    }

    public List<Iterable<Chessboard>> getSolutionIterators(int minIterables) {
        List<List<ChessPiece>> pieceCombinations = generatePieceCombinations(pieces);
        BoardLocationCombinationIterator boardLocationCombinations = new BoardLocationCombinationIterator(boardWidth, boardHeight, pieces.size());
        BoardLocationCombinationComputer boardLocationComputer = new BoardLocationCombinationComputer(boardWidth, boardHeight, pieces.size());
        List<Iterable<Chessboard>> iterables = new ArrayList<>();

        // Separate the solutions into at least minIterables iterables
        // If the number is too big, cap it at boardLocationCombinations.size()
        // If the number is zero, make it at least 1, taking the entire thing
        if (minIterables > boardLocationCombinations.getNumberOfCombinations()) {
            minIterables = boardLocationCombinations.getNumberOfCombinations();
        } else if (minIterables == 0) {
            minIterables = 1;
        }

        int iterableSize = (boardLocationCombinations.getNumberOfCombinations() / minIterables);
        if (boardLocationCombinations.getNumberOfCombinations() % minIterables != 0) {
            iterableSize += 1;
        }

        for (int i = 0; i < boardLocationCombinations.getNumberOfCombinations(); i += iterableSize) {
            BoardLocationCombinationIterator copy = new BoardLocationCombinationIterator(boardWidth, boardHeight, boardLocationComputer.computeAtIndex(i), i);
            
            if (i + iterableSize >= boardLocationCombinations.getNumberOfCombinations()) {
                copy.setLimit(boardLocationCombinations.getNumberOfCombinations());
                innerGetSolutionsIterable(iterables, pieceCombinations, copy, boardLocationComputer);
            } else {
                copy.setLimit(i + iterableSize);
                innerGetSolutionsIterable(iterables, pieceCombinations, copy, boardLocationComputer);
            }
        }

        return iterables;
    }

    // This is an inner method, only to be called by getSolutionIterables()
    private void innerGetSolutionsIterable(List<Iterable<Chessboard>> iterables,
            List<List<ChessPiece>> pieceCombinations,
            BoardLocationCombinationIterator boardLocationCombinations,
            BoardLocationCombinationComputer boardLocationComputer) {
        try {
            // Try to multiply number of piece and location combinations
            // If it is or exceeds Integer.MAX_VALUE, then the rest of the
            // logic will not work, so we split the location combinations
            // into 2 separate iterators.
            int numberOfBoardCombinations = Math.multiplyExact(pieceCombinations.size(),
            boardLocationCombinations.getNumberOfCombinations());
            if (numberOfBoardCombinations == Integer.MAX_VALUE) {
                throw new ArithmeticException();
            }

            iterables.add(new Iterable<Chessboard>() {
                @Override
                public Iterator<Chessboard> iterator() {
                    return new DominationSolutionIterator(boardWidth, boardHeight, pieceCombinations,
                            boardLocationCombinations);
                }
            });
        } catch (ArithmeticException e) {
            int halfBoardLocationCombinationsIndex = boardLocationCombinations.getNumberOfCombinations() / 2;

            BoardLocationCombinationIterator lowerPart = new BoardLocationCombinationIterator(boardWidth, boardHeight, pieces.size());
            lowerPart.setLimit(halfBoardLocationCombinationsIndex);
            BoardLocationCombinationIterator upperPart = new BoardLocationCombinationIterator(boardWidth, boardHeight, boardLocationComputer.computeAtIndex(halfBoardLocationCombinationsIndex), halfBoardLocationCombinationsIndex);

            innerGetSolutionsIterable(iterables, pieceCombinations, lowerPart, boardLocationComputer);
            innerGetSolutionsIterable(iterables, pieceCombinations, upperPart, boardLocationComputer);
        }
    }

    private BigInteger calculateNumberOfBoardCombinations(List<ChessPiece> pieces) {
        final int cellCount = boardWidth * boardHeight;
        final int numberOfQueens = countPiecesOfType(pieces, ChessPiece.Type.QUEEN);
        final int numberOfBishops = countPiecesOfType(pieces, ChessPiece.Type.BISHOP);

        return Utility.factorial(cellCount)
                .divide(Utility.factorial(numberOfQueens).multiply(Utility.factorial(numberOfBishops))
                        .multiply(Utility.factorial(cellCount - numberOfQueens - numberOfBishops)));
    }

    private ArrayList<FluentIterable<Integer>> generateBoardLocationCombinations(int numberOfPieces) {
        if (numberOfPieces == 0) {
            return new ArrayList<>();
        }

        final int cellCount = boardWidth * boardHeight;
        final int numberOfCombinations = Utility.factorial(cellCount)
                .divide(Utility.factorial(numberOfPieces).multiply(Utility.factorial(cellCount - numberOfPieces)))
                .intValue();

        final ArrayList<FluentIterable<Integer>> boardLocations = new ArrayList<>(numberOfCombinations);

        final ArrayList<Integer> currentCombination = Lists.newArrayList(PyIterators.range(0, numberOfPieces));
        final ArrayList<Integer> lastCombination = Lists
                .newArrayList(PyIterators.range(cellCount - numberOfPieces, cellCount));
        boardLocations.add(FluentIterable.from((ArrayList<Integer>) currentCombination.clone()));

        while (!lastCombination.containsAll(currentCombination)) {
            for (int i = numberOfPieces - 1; i >= 0; i--) {
                if (currentCombination.get(i) < lastCombination.get(i)) {
                    for (int j = i; j < numberOfPieces; j++) {
                        currentCombination.set(j, currentCombination.get(j) + 1);
                    }

                    break;
                } else if (currentCombination.get(i) == lastCombination.get(i)) {
                    for (int j = i - 1; j >= 0; j--) {
                        if (currentCombination.get(j) != lastCombination.get(j)) {
                            currentCombination.set(i, currentCombination.get(j) + (i - j));
                            break;
                        }
                    }
                }
            }

            boardLocations.add(FluentIterable.from((ArrayList<Integer>) currentCombination.clone()));
        }

        return boardLocations;
    }

    private int calculateNumberOfChessPieceCombinations(ArrayList<ChessPiece> pieces) {
        ArrayList<Integer> countPerPiece = new ArrayList<>(ChessPiece.Type.values().length);
        FluentIterable.from(pieces).transform(piece -> piece.getType()).toSet().forEach(type -> {
            countPerPiece.add(FluentIterable.from(pieces).filter(piece -> piece.getType() == type).size());
        });

        return Utility.factorial(pieces.size()).divide(PyIterators.reduce(countPerPiece, (acc, current) -> {
            return acc.multiply(Utility.factorial(current));
        }, new BigInteger("1"))).intValue();
    }

    private List<List<ChessPiece>> generatePieceCombinations(ArrayList<ChessPiece> pieces) {
        if (pieces.isEmpty()) {
            return new ArrayList<>();
        }

        final List<List<ChessPiece>> pieceCombinations = new ArrayList<>(
                calculateNumberOfChessPieceCombinations(pieces));

        final ReduceFunction<ArrayList<ChessPiece>, ArrayList<ChessPiece>> inner_generator = new ReduceFunction<ArrayList<ChessPiece>, ArrayList<ChessPiece>>() {
            public ArrayList<ChessPiece> apply(ArrayList<ChessPiece> current, ArrayList<ChessPiece> left) {
                ImmutableSet<ChessPiece.Type> chessPieceTypes = FluentIterable.from(left)
                        .transform(piece -> piece.getType()).toSet();

                if (chessPieceTypes.size() == 1) {
                    current.addAll(left);
                    pieceCombinations.add(ImmutableList.copyOf(current));
                } else if (chessPieceTypes.size() > 1) {
                    for (ChessPiece.Type type : chessPieceTypes) {
                        ChessPiece pieceOfType = FluentIterable.from(left).filter(piece -> piece.getType() == type)
                                .first().get();

                        ArrayList<ChessPiece> copyOfLeft = Lists.newArrayList(left);
                        copyOfLeft.remove(pieceOfType);

                        ArrayList<ChessPiece> copyOfCurrent = Lists.newArrayList(current);
                        copyOfCurrent.add(pieceOfType);

                        apply(copyOfCurrent, copyOfLeft);
                    }
                }

                return current;
            }
        };

        inner_generator.apply(new ArrayList<>(), pieces);
        return pieceCombinations;
    }

    private static int countPiecesOfType(List<ChessPiece> pieces, ChessPiece.Type type) {
        return Iterators.size(FluentIterable.from(pieces).filter(piece -> piece.getType() == type).iterator());
    }
}

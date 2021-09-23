package org.efac.chess;

import java.math.BigInteger;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableSet;

import org.efac.func.ReduceFunction;
import org.efac.chess.iter.DominationSolutionIterator;
import org.efac.func.PyIterators;

public class DominationSolver {
    private final int boardWidth;
    private final int boardHeight;
    private final ArrayList<ChessPiece> pieces;
    private final int chessboardCombinations;
    private Chessboard solutionChessboard;
    private boolean ranSolver;

    public int getNumberOfChessboardCombinations() { return chessboardCombinations; }

    public DominationSolver(int boardWidth, int boardHeight, List<ChessPiece> pieces) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        chessboardCombinations = calculateNumberOfBoardCombinations(pieces);
        this.pieces = new ArrayList<>(pieces);
        solutionChessboard = null;
        ranSolver = false;
    }

    public Iterable<Chessboard> getSolutions() {
        return new Iterable<Chessboard>(){
            @Override
            public Iterator<Chessboard> iterator() {
                return new DominationSolutionIterator(boardWidth, boardHeight, generatePieceCombinations(pieces), generateBoardLocationCombinations(pieces.size()));
            }
        };
    }

    private int calculateNumberOfBoardCombinations(List<ChessPiece> pieces) {
        final int cellCount = boardWidth * boardHeight;
        final int numberOfQueens = countPiecesOfType(pieces, ChessPiece.Type.QUEEN);
        final int numberOfBishops = countPiecesOfType(pieces, ChessPiece.Type.BISHOP);

        return factorial(cellCount).divide(factorial(numberOfQueens).multiply(factorial(numberOfBishops)).multiply(factorial(cellCount - numberOfQueens - numberOfBishops))).intValue();
    }

    private ArrayList<ImmutableList<Point>> generateBoardLocationCombinations(int numberOfPieces) {
        if (numberOfPieces == 0) {
            return new ArrayList<>();
        }
        
        final int cellCount = boardWidth * boardHeight;
        final int numberOfCombinations = factorial(cellCount).divide(factorial(numberOfPieces).multiply(factorial(cellCount - numberOfPieces))).intValue();
        final ArrayList<ImmutableList<Point>> boardLocations = new ArrayList<>(numberOfCombinations);

        final ArrayList<Integer> currentCombination = Lists.newArrayList(PyIterators.range(0, numberOfPieces));
        final ArrayList<Integer> lastCombination = Lists.newArrayList(PyIterators.range(cellCount - numberOfPieces, cellCount));

        while (!lastCombination.containsAll(currentCombination)) {
            boardLocations.add(
                FluentIterable.from(currentCombination)
                                .transform(index -> Point.fromIndex(index, boardWidth, boardHeight))
                                .toList()
            );
            
            for (int i : PyIterators.reversed(PyIterators.range(0, numberOfPieces))) {
                if (currentCombination.get(i) < lastCombination.get(i)) {
                    for (int j : PyIterators.range(i, numberOfPieces)) {
                        currentCombination.set(j, currentCombination.get(j) + 1);
                    }

                    break;
                } else if (currentCombination.get(i) == lastCombination.get(i)) {
                    for (int j : PyIterators.reversed(PyIterators.range(0, i))) {
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

    private int calculateNumberOfChessPieceCombinations(ArrayList<ChessPiece> pieces) {
        ArrayList<Integer> countPerPiece = new ArrayList<>(ChessPiece.Type.values().length);
        FluentIterable.from(pieces)
                        .transform(piece -> piece.getType())
                        .toSet()
                        .forEach(type -> {
                            countPerPiece.add(
                                FluentIterable.from(pieces)
                                                .filter(piece -> piece.getType() == type)
                                                .size()
                            );
                        });

        return factorial(pieces.size()).divide(PyIterators.reduce(countPerPiece, (acc, current) -> acc.multiply(factorial(current)), new BigInteger("1"))).intValue();
    }

    private ArrayList<ImmutableList<ChessPiece>> generatePieceCombinations(ArrayList<ChessPiece> pieces) {
        if (pieces.isEmpty()) {
            return new ArrayList<>();
        }
        
        final ArrayList<ImmutableList<ChessPiece>> pieceCombinations = new ArrayList<>(calculateNumberOfChessPieceCombinations(pieces));

        final ReduceFunction<ArrayList<ChessPiece>, ArrayList<ChessPiece>> inner_generator = new ReduceFunction<ArrayList<ChessPiece>,ArrayList<ChessPiece>>(){
            public ArrayList<ChessPiece> apply(ArrayList<ChessPiece> current, ArrayList<ChessPiece> left) {
                ImmutableSet<ChessPiece.Type> chessPieceTypes = FluentIterable.from(left)
                                                                                .transform(piece -> piece.getType())
                                                                                .toSet();

                if (chessPieceTypes.size() == 1) {
                    current.addAll(left);
                    pieceCombinations.add(ImmutableList.copyOf(current));
                } else if (chessPieceTypes.size() > 1) {
                    for (ChessPiece.Type type : chessPieceTypes) {
                        ChessPiece pieceOfType = FluentIterable.from(left)
                                                                .filter(piece -> piece.getType() == type)
                                                                .first()
                                                                .get();

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

    private Chessboard createChessboard(Map<Point, ChessPiece> boardState) {
        Chessboard chessboard = new Chessboard(boardWidth, boardHeight);
    
        for (Map.Entry<Point, ChessPiece> entry : boardState.entrySet()) {
            BoardLocation location = chessboard.getLocation(entry.getKey());
            location.setPiece(entry.getValue());
        }

        return chessboard;
    }

    private static BigInteger factorial(int n) {
        if (n == 0) {
            return new BigInteger("1");
        }

        final Function<Integer, BigInteger> inner_factorial = new Function<Integer, BigInteger>() {
            @Override
            public BigInteger apply(Integer m) {
                return m == 1 ? new BigInteger("1") : apply(m - 1).multiply(new BigInteger(m.toString()));
            }
        };
        
        return inner_factorial.apply(n);
    }

    private static int countPiecesOfType(List<ChessPiece> pieces, ChessPiece.Type type) {
        return Iterators.size(FluentIterable.from(pieces).filter(piece -> piece.getType() == type).iterator());
    }
}

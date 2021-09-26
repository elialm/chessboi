package org.efac.chess.iter;

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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Map;
import java.util.List;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import org.efac.chess.ChessPiece;
import org.efac.chess.Chessboard;
import org.efac.chess.Point;
import org.efac.chess.BoardLocation;
import org.efac.func.PyIterators;
import org.efac.func.ProgressUpdate;

public class DominationSolutionIterator implements Iterator<Chessboard> {
    private final int boardWidth;
    private final int boardHeight;
    private final List<ImmutableList<ChessPiece>> pieceCombinations;
    private final List<FluentIterable<Integer>> boardLocationCombinationIterables;
    private ProgressUpdate progressUpdateCallback;
    private Chessboard nextSolution;
    private boolean exhaustedIterator;
    private int currentBoardCombination;
    private int previousLocationCombinationIndex;
    private ImmutableList<Point> cachedLocationCombination;

    public void setProgressUpdateCallback(ProgressUpdate callback) { progressUpdateCallback = callback; }

    public DominationSolutionIterator(int boardWidth, int boardHeight, List<ImmutableList<ChessPiece>> pieceCombinations, List<FluentIterable<Integer>> boardLocationCombinations) {
        this(boardWidth, boardHeight, pieceCombinations, boardLocationCombinations, null);
    }

    public DominationSolutionIterator(int boardWidth, int boardHeight, List<ImmutableList<ChessPiece>> pieceCombinations, List<FluentIterable<Integer>> boardLocationCombinations, ProgressUpdate progressUpdate) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.pieceCombinations = pieceCombinations;
        this.boardLocationCombinationIterables = boardLocationCombinations;
        this.progressUpdateCallback = progressUpdate;
        nextSolution = null;
        exhaustedIterator = false;
        currentBoardCombination = 0;
        previousLocationCombinationIndex = -1;
        cachedLocationCombination = null;
    }
    
    @Override
    public boolean hasNext() {
        if (exhaustedIterator) {
            return false;
        }
        
        if (nextSolution != null) {
            return true;
        }

        return (nextSolution = findNext()) == null ? false : true;
    }
      
    @Override
    public Chessboard next() {
        if (exhaustedIterator) {
            throw new NoSuchElementException("Iterator has been exhausted");
        }
        
        Chessboard next = nextSolution != null ? nextSolution : findNext();
        nextSolution = null;
        if (next == null) {
            throw new NoSuchElementException("Iterator has been exhausted");
        }

        return next;
    }
      
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private Chessboard findNext() {
        Chessboard chessboardSolution = null;
        
        locationCombinationsLoop:
        for (int currentLocationCombinationIndex = currentBoardCombination / pieceCombinations.size(); currentLocationCombinationIndex < boardLocationCombinationIterables.size(); currentLocationCombinationIndex++) {
            ImmutableList<Point> currentLocationCombination = getBoardLocationCombination(currentLocationCombinationIndex);
            
            if (progressUpdateCallback != null) {
                progressUpdateCallback.updateProgress(currentBoardCombination);
            }

            for (int pieceCombinationIndex = currentBoardCombination % pieceCombinations.size(); pieceCombinationIndex < pieceCombinations.size(); currentBoardCombination++, pieceCombinationIndex++) {
                ImmutableList<ChessPiece> currentPieceCombination = pieceCombinations.get(pieceCombinationIndex);
                Chessboard chessboard = createChessboard(PyIterators.zip(currentLocationCombination, currentPieceCombination));
            
                if (chessboard.isDominated()) {
                    chessboardSolution = chessboard;
                    currentBoardCombination++;

                    if (progressUpdateCallback != null) {
                        progressUpdateCallback.updateProgress(currentBoardCombination);
                    }
                    
                    break locationCombinationsLoop;
                }
            }
        }

        if (chessboardSolution == null) {
            exhaustedIterator = true;
        }

        return chessboardSolution;
    }

    private Chessboard createChessboard(Map<Point, ChessPiece> boardState) {
        Chessboard chessboard = new Chessboard(boardWidth, boardHeight);
    
        for (Map.Entry<Point, ChessPiece> entry : boardState.entrySet()) {
            BoardLocation location = chessboard.getLocation(entry.getKey());
            location.setPiece(entry.getValue());
        }

        return chessboard;
    }

    private ImmutableList<Point> getBoardLocationCombination(int index) {
        if (index == previousLocationCombinationIndex) {
            return cachedLocationCombination;
        }

        previousLocationCombinationIndex = index;
        return cachedLocationCombination = boardLocationCombinationIterables.get(index)
                                                                            .transform(i -> Point.fromIndex(i, boardWidth, boardHeight))
                                                                            .toList();
    }
}

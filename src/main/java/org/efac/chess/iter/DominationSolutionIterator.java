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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.List;

import org.efac.chess.ChessPiece;
import org.efac.chess.Chessboard;
import org.efac.chess.BoardLocation;
import org.efac.func.ProgressUpdate;

public class DominationSolutionIterator implements Iterator<Chessboard> {
    private final int boardWidth;
    private final int boardHeight;
    private final List<List<ChessPiece>> pieceCombinations;
    private final Iterator<List<Integer>> boardLocationCombinationIterator;
    private List<List<Integer>> boardLocationCombinations;
    private ProgressUpdate progressUpdateCallback;
    private Chessboard nextSolution;
    private boolean exhaustedIterator;
    private int currentBoardCombination;
    private volatile boolean cancelIteration;

    public void setProgressUpdateCallback(ProgressUpdate callback) { progressUpdateCallback = callback; }

    public DominationSolutionIterator(int boardWidth, int boardHeight, List<List<ChessPiece>> pieceCombinations, Iterator<List<Integer>> boardLocationCombinations) {
        this(boardWidth, boardHeight, pieceCombinations, boardLocationCombinations, null);
    }

    public DominationSolutionIterator(int boardWidth, int boardHeight, List<List<ChessPiece>> pieceCombinations, Iterator<List<Integer>> boardLocationCombinations, ProgressUpdate progressUpdate) {
        if (!boardLocationCombinations.hasNext()) {
            throw new InvalidParameterException("Given board location combinations iterator has already been exhausted");
        }
        
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.pieceCombinations = pieceCombinations;
        this.boardLocationCombinationIterator = boardLocationCombinations;
        boardLocationCombinations = null;
        this.progressUpdateCallback = progressUpdate;
        nextSolution = null;
        exhaustedIterator = false;
        currentBoardCombination = 0;
        cancelIteration = false;
    }
    
    @Override
    public boolean hasNext() {
        if (exhaustedIterator) {
            return false;
        }
        
        if (nextSolution != null) {
            return true;
        }

        if (boardLocationCombinations == null) {
            boardLocationCombinations = collectLocationCombinations(boardLocationCombinationIterator);
        }

        return (nextSolution = findNext()) != null;
    }
      
    @Override
    public Chessboard next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Iterator has been exhausted");
        }
        
        Chessboard next = nextSolution;
        nextSolution = null;
        return next;
    }
      
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void cancel() {
        cancelIteration = true;
        exhaustedIterator = true;
    }

    private Chessboard findNext() {
        Chessboard chessboardSolution = null;
        
        locationCombinationsLoop:
        for (int currentLocationCombinationIndex = currentBoardCombination / pieceCombinations.size(); currentLocationCombinationIndex < boardLocationCombinations.size() && !cancelIteration; currentLocationCombinationIndex++) {
            List<Integer> currentLocationCombination = boardLocationCombinations.get(currentLocationCombinationIndex);
            
            callProgressCallback();

            for (int pieceCombinationIndex = currentBoardCombination % pieceCombinations.size(); pieceCombinationIndex < pieceCombinations.size() && !cancelIteration; currentBoardCombination++, pieceCombinationIndex++) {
                List<ChessPiece> currentPieceCombination = pieceCombinations.get(pieceCombinationIndex);
                Chessboard chessboard = createChessboard(currentLocationCombination, currentPieceCombination);
            
                if (chessboard.isDominated()) {
                    chessboardSolution = chessboard;
                    currentBoardCombination++;

                    callProgressCallback();
                    break locationCombinationsLoop;
                }
            }
        }

        if (chessboardSolution == null) {
            exhaustedIterator = true;
        }

        return chessboardSolution;
    }

    private Chessboard createChessboard(List<Integer> boardLocationCombination, List<ChessPiece> pieceCombination) {
        if (pieceCombination.size() != boardLocationCombination.size()) {
            throw new InvalidParameterException("Size mismatch between number of piece and board location combinations");
        }

        Chessboard chessboard = new Chessboard(boardWidth, boardHeight);
    
        for (int i = 0; i < pieceCombination.size(); i++) {
            BoardLocation location = chessboard.getLocation(boardLocationCombination.get(i));
            location.setPiece(pieceCombination.get(i));
        }

        return chessboard;
    }

    private void callProgressCallback() {
        if (progressUpdateCallback != null) {
            progressUpdateCallback.updateProgress(currentBoardCombination);
        }
    }

    private List<List<Integer>> collectLocationCombinations(Iterator<List<Integer>> boardLocationCombinations) {
        ArrayList<List<Integer>> collectedLocationCombinations = new ArrayList<>();
        while (boardLocationCombinations.hasNext()) {
            collectedLocationCombinations.add(boardLocationCombinations.next());
        }

        return collectedLocationCombinations;
    }
}

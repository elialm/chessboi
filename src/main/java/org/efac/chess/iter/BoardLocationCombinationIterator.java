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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;

import com.google.common.collect.Lists;

import org.efac.func.PyIterators;
import org.efac.common.Utility;

public class BoardLocationCombinationIterator implements Iterator<List<Integer>> {
    final private int cellCount;
    final private int numberOfPieces;
    final private int numberOfCombinations;
    private ArrayList<Integer> next;
    private ArrayList<Integer> current;
    private ArrayList<Integer> last;
    private int combinationsGenerated;
    private int limit;

    public BoardLocationCombinationIterator(int boardWidth, int boardHeight, int numberOfPieces) {
        this(boardWidth, boardHeight, Lists.newArrayList(PyIterators.range(0, numberOfPieces)), 0);
    }

    public BoardLocationCombinationIterator(int boardWidth, int boardHeight, List<Integer> initial, int initialIndex) {
        cellCount = boardWidth * boardHeight;
        this.numberOfPieces = initial.size();
        numberOfCombinations = calculateNumberOfCombinations();

        next = null;
        current = Lists.newArrayList(initial);
        last = Lists.newArrayList(PyIterators.range(cellCount - numberOfPieces, cellCount));

        combinationsGenerated = initialIndex;
        limit = Integer.MAX_VALUE;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getNumberOfCombinations() {
        return Math.min(numberOfCombinations, limit);
    }

    @Override
    public boolean hasNext() {
        if (next != null) {
            return true;
        }

        if (combinationsGenerated >= limit) {
            return false;
        }

        next = findNext();
        return next != null;
    }

    @Override
    public List<Integer> next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Iterator is exhausted");
        }

        List<Integer> nextCopy = Lists.newArrayList(next);
        next = null;
        return nextCopy;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private ArrayList<Integer> findNext() {
        if (last.containsAll(current)) {
            return null;
        }

        for (int i = numberOfPieces - 1; i >= 0; i--) {
            if (current.get(i) < last.get(i)) {
                for (int j = i; j < numberOfPieces; j++) {
                    current.set(j, current.get(j) + 1);
                }

                break;
            } else if (current.get(i) == last.get(i)) {
                for (int j = i - 1; j >= 0; j--) {
                    if (current.get(j) != last.get(j)) {
                        current.set(i, current.get(j) + (i - j));
                        break;
                    }
                }
            }
        }

        combinationsGenerated++;
        return (ArrayList<Integer>) current.clone();
    }

    private int calculateNumberOfCombinations() {
        return Utility.factorial(cellCount)
                .divide(Utility.factorial(numberOfPieces).multiply(Utility.factorial(cellCount - numberOfPieces)))
                .intValue();
    }
}

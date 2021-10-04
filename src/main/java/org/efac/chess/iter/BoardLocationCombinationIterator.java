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

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.efac.func.PyIterators;

public class BoardLocationCombinationIterator implements Iterator<FluentIterable<Integer>> {
    final private int cellCount;
    final private int numberOfPieces;
    private ArrayList<Integer> next;
    private ArrayList<Integer> current;
    private ArrayList<Integer> last;

    public BoardLocationCombinationIterator(int boardWidth, int boardHeight, int numberOfPieces) {
        cellCount = boardWidth * boardHeight;
        this.numberOfPieces = numberOfPieces;

        last = Lists.newArrayList(PyIterators.range(cellCount - numberOfPieces, cellCount));
        current = Lists.newArrayList(PyIterators.range(0, numberOfPieces));
        next = null;
    }

    public BoardLocationCombinationIterator(int boardWidth, int boardHeight, List<Integer> initial) {
        cellCount = boardWidth * boardHeight;
        this.numberOfPieces = initial.size();

        last = Lists.newArrayList(PyIterators.range(cellCount - numberOfPieces, cellCount));
        current = Lists.newArrayList(initial);
        next = null;
    }

    @Override
    public boolean hasNext() {
        if (next != null) {
            return true;
        }

        next = findNext();
        return next != null;
    }

    @Override
    public FluentIterable<Integer> next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Iterator is exhausted");
        }

        FluentIterable<Integer> nextCopy = FluentIterable.from(next);
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

        return (ArrayList<Integer>)current.clone();
    }
}

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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import org.efac.common.Utility;

public class BoardLocationCombinationComputer {
    final private int cellCount;
    final private int numberOfPieces;
    final private long numberOfCombinations;
    final private int upperSliceLength;
    final private ArrayList<ArrayList<Integer>> upperSlices;
    final private ArrayList<Integer> baseBounds;

    public BoardLocationCombinationComputer(int boardWidth, int boardHeight, int numberOfPieces) {
        cellCount = boardWidth * boardHeight;
        this.numberOfPieces = numberOfPieces;
        numberOfCombinations = Utility.factorial(cellCount)
                .divide(Utility.factorial(numberOfPieces).multiply(Utility.factorial(cellCount - numberOfPieces)))
                .longValue();

        upperSliceLength = cellCount + 1 - numberOfPieces;
        upperSlices = computeSlices(upperSliceLength, numberOfPieces + 1);
        baseBounds = computeBounds(upperSlices.get(0), 0);
        upperSlices.remove(0);
    }

    public ArrayList<Integer> computeAtIndex(long requestedIndex) {
        if (requestedIndex >= numberOfCombinations) {
            throw new IndexOutOfBoundsException("Index exceeds possible combinations");
        }

        if (requestedIndex < 0) {
            throw new IndexOutOfBoundsException("Index must be a positive integer");
        }

        ArrayList<Integer> requestedCombination = new ArrayList<>(numberOfPieces);
        ArrayList<Integer> currentBounds = baseBounds;
        int startingPoint = 0;
        int previousPoint = 0;

        for (int depth = 0; depth < (upperSlices.size() + 1); depth++) {
            if (depth != 0) {
                int lastValueAdded = requestedCombination.get(requestedCombination.size() - 1);
                currentBounds = computeBounds(upperSlices.get(depth - 1).subList(0, upperSliceLength - lastValueAdded + depth - 1), currentBounds.get(startingPoint - previousPoint - 1));
            }

            for (int i = 1; i < currentBounds.size(); i++) {
                if (currentBounds.get(i) > requestedIndex) {
                    requestedCombination.add(i - 1 + startingPoint);
                    previousPoint = startingPoint;
                    startingPoint++;
                    break;
                }
            }
        }

        return requestedCombination;
    }

    // Computes bounds between increments using a diagonal slice of Pascal's triangle
    private ArrayList<Integer> computeBounds(List<Integer> slice, int base) {
        ArrayList<Integer> bounds = new ArrayList<>(slice.size() + 1);
        bounds.add(base);

        for (int i = 0; i < slice.size(); i++) {
            bounds.add(bounds.get(i) + slice.get(i));
        }

        return bounds;
    }

    // Computes diagonal slices off of Pascal's triangle with the given length and depth
    private ArrayList<ArrayList<Integer>> computeSlices(int length, int depth) {
        if (depth < 1) {
            throw new InvalidParameterException("Depth must be 1 or higher");
        }
        
        ArrayList<ArrayList<Integer>> slices = new ArrayList<>(depth);
        ArrayList<Integer> lastLevel = Lists.newArrayList(Collections.nCopies(length, 1));
        slices.add(lastLevel);

        if (depth == 1) {
            return slices;
        }

        ArrayList<Integer> currentLevel = new ArrayList<>(length);
        currentLevel.add(1);

        for (int i = 0; i < (depth - 1); i++) {
            if (i != 0) {
                lastLevel = currentLevel;
                currentLevel = new ArrayList<>(length);
                currentLevel.add(1);
            }

            for (int j = 1; j < lastLevel.size(); j++) {
                currentLevel.add(currentLevel.get(j - 1) + lastLevel.get(j));
            }

            slices.add(currentLevel);
        }

        return slices;
    }
}

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

import java.math.BigInteger;
import java.util.List;
import java.util.ArrayList;

import org.efac.chess.Chessboard;
import org.efac.func.PyIterators;

public class ThreadedSolutionIterable implements Iterable<Chessboard> {
    private List<DominationSolutionIterator> solutionIterables;
    private ArrayList<Integer> workerProgresses;
    
    public ThreadedSolutionIterable(List<DominationSolutionIterator> solutionIterables) {
        this.solutionIterables = solutionIterables;
        workerProgresses = new ArrayList<>(solutionIterables.size());

        for (int i = 0; i < solutionIterables.size(); i++) {
            final int threadIndex = i;
            
            workerProgresses.add(0);
            solutionIterables.get(threadIndex).setProgressUpdateCallback(progress -> {
                iteratorUpdateCallback(threadIndex, progress);
            });
        }
    } 
    
    @Override
    public ThreadedSolutionIterator iterator() {
        return new ThreadedSolutionIterator(solutionIterables);
    }

    public BigInteger getCurrentProgress() {
        return PyIterators.reduce(workerProgresses, (acc, progress) -> {
            return acc.add(new BigInteger(progress.toString()));
        }, new BigInteger("0"));
    }

    private void iteratorUpdateCallback(int threadIndex, int progress) {
        workerProgresses.set(threadIndex, progress);
    }
}

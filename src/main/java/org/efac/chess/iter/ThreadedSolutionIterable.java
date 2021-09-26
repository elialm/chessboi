package org.efac.chess.iter;

import java.math.BigInteger;
import java.util.Iterator;
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
    public Iterator<Chessboard> iterator() {
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

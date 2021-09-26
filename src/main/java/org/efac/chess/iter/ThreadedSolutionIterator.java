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

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.efac.chess.Chessboard;

import oshi.SystemInfo;

public class ThreadedSolutionIterator implements Iterator<Chessboard> {
    public final static int PREFERRED_THREAD_COUNT = new SystemInfo().getHardware().getProcessor().getPhysicalProcessorCount();
    private final static int queueSize = 256;

    private final List<Iterable<Chessboard>> solutionIterables;
    private final ThreadPoolExecutor executor;
    private final ArrayBlockingQueue<Chessboard> solutionQueue;
    private final ArrayList<Future<Exception>> futures;
    private final AtomicInteger runningThreads;
    private Chessboard nextSolution;
    
    public ThreadedSolutionIterator(List<Iterable<Chessboard>> iterables) {
        solutionIterables = iterables;
        executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(iterables.size());
        solutionQueue = new ArrayBlockingQueue<>(queueSize);
        futures = new ArrayList<>(iterables.size());
        runningThreads = new AtomicInteger(iterables.size());
        nextSolution = null;

        for (int i = 0; i < solutionIterables.size(); i++) {
            Iterable<Chessboard> solutionIterable = solutionIterables.get(i);

            Future<Exception> future = executor.submit(() -> {
                try {
                    for (Chessboard solution : solutionIterable) {
                        solutionQueue.put(solution);
                    }
                } catch (NullPointerException ex) {
                    runningThreads.decrementAndGet();
                    return ex;
                }

                    runningThreads.decrementAndGet();
                    return null;
            });

            futures.add(future);
        }
    }

    @Override
    public boolean hasNext() {
        if (nextSolution != null) {
            return true;
        }

        try {
            do {
                nextSolution = solutionQueue.poll(200, TimeUnit.MILLISECONDS);
            } while (nextSolution == null && runningThreads.get() != 0);

        } catch (InterruptedException ex) {
            cleanup();
            return false;
        }

        cleanup();
        return nextSolution != null;
    }

    @Override
    public Chessboard next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Iterator is exhausted");   
        }

        Chessboard temp = nextSolution;
        nextSolution = null;
        return temp;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void cleanup() {
        if (runningThreads.get() == 0) {
            executor.shutdown();
        }
    }
}

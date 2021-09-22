package org.efac.func;

import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.Iterator;
import java.util.Collections;

import com.google.common.collect.Lists;

public class PyIterators {
    public static<F, T> T reduce(Iterable<F> iterable, ReduceFunction<F, T> func, T initial) {
        T current = initial;
        for (F f : iterable) {
            current = func.apply(current, f);
        }

        return current;
    }

    public static Iterable<Integer> range(int startInclusive, int endExclusive) {
        return new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return IntStream.range(startInclusive, endExclusive).iterator();
            }
        };
    }

    public static<T> Iterable<T> reversed(Iterable<T> iterable) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                ArrayList<T> collected = Lists.newArrayList(iterable);
                Collections.reverse(collected);

                return collected.iterator();
            }
        };
    }
}

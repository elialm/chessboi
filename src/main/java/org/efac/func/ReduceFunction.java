package org.efac.func;

public interface ReduceFunction<F, T> {
    public T apply(T t, F f); 
}

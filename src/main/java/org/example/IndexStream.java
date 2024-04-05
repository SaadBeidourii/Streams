package org.example;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

public class IndexStream<T> implements BasicStream<T> {

    private final int min;
    private final int max;
    private final Function<Integer, Optional<T>> f;

    public IndexStream(Function<Integer,Optional<T>> f,int min, int max) {
        this.f = f;
        this.min = min;
        this.max = max;
    }

    @Override
    public void forEach(Consumer<T> action) {
        AtomicInteger i = new AtomicInteger(min);
        while (i.get() < max) {
            f.apply(i.get()).ifPresent(action);
            i.incrementAndGet();
        }
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        return Optional.empty();
    }

    @Override
    public BasicStream<T> filter(Predicate<T> predicate) {
        return null;
    }

    @Override
    public BasicStream<T> limit(long maxSize) {
        return null;
    }

    @Override
    public <R> BasicStream<R> map(Function<T, R> mapper) {
        return null;
    }
}

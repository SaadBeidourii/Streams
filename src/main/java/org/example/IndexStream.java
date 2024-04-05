package org.example;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

public class IndexStream<T> implements BasicStream<T> {

    protected final int min;
    protected final int max;
    protected final Function<Integer, Optional<T>> valueSupplier;

    public IndexStream(Function<Integer,Optional<T>> valueSupplier, int min, int max) {
        this.valueSupplier = valueSupplier;
        this.min = min;
        this.max = max;
    }

    @Override
    public void forEach(Consumer<T> action) {
        forEachRecursive(action, min);
    }

    private void forEachRecursive(Consumer<T> action, int index) {
        if (index <= max) {
            Optional<T> element = valueSupplier.apply(index);
            element.ifPresent(action);
            forEachRecursive(action, index + 1);
        }
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        return reduceRecursive(accumulator, min);
    }

    private Optional<T> reduceRecursive(BinaryOperator<T> accumulator, int index) {
        if (index > max) {
            return Optional.empty(); // Terminate recursion when index exceeds max
        }
        Optional<T> currentValue = valueSupplier.apply(index);
        return currentValue.map(value ->
                reduceRecursive(accumulator, index + 1).map(nextValue ->
                        accumulator.apply(value, nextValue)
                ).orElse(value)
        );
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

    public T findAny(Predicate<T> predicate) {
        AtomicInteger i = new AtomicInteger(min);
        while (i.get() < max) {
            Optional<T> o = valueSupplier.apply(i.get());
            if (o.isPresent()) {
                T x = o.get();
                if (predicate.test(x)) {
                    return x;
                }
            }
            i.incrementAndGet();
        }
        return null;
    }




}

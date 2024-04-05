package org.example;

import java.util.Optional;
import java.util.function.*;

public class IndexStream<T> implements BasicStream<T> {

    @Override
    public void forEach(Consumer<T> action) {

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

package org.example;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class IndexStreamParallel<T> extends IndexStream<T> {


    public IndexStreamParallel(Function<Integer, Optional<T>> f, int min, int max) {
        super(f, min, max);
    }

    @Override
    public void forEach(Consumer<T> action) {
        IntStream.range(min, max).parallel().forEach(index -> {
            Optional<T> o = valueSupplier.apply(index);
            o.ifPresent(action::accept);
        });
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        return IntStream.range(min, max).parallel().mapToObj(index -> valueSupplier.apply(index).orElse(null))
                .filter(x -> x != null)
                .reduce(accumulator);
    }

    @Override
    public BasicStream<T> filter(Predicate<T> predicate) {
        return new IndexStreamParallel<>(i -> valueSupplier.apply(i).filter(predicate), min, max);
    }

    @Override
    public BasicStream<T> limit(long maxSize) {
        return new IndexStreamParallel<>(valueSupplier, min, min + (int) maxSize);
    }

    @Override
    public <R> BasicStream<R> map(Function<T, R> mapper) {
        return new IndexStreamParallel<>(i -> valueSupplier.apply(i).map(mapper), min, max);
    }

    public T findAny(Predicate<T> predicate) {
        AtomicReference<T> result = new AtomicReference<>();
        IntStream.range(min, max).parallel()
                .mapToObj(i -> valueSupplier.apply(i))
                .forEach(optional -> optional.ifPresent(value -> {
                    if (predicate.test(value) && result.get() == null) {
                        result.set(value);
                    }
                }));
        return result.get();
    }



}

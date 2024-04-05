package org.example;

import java.util.Optional;
import java.util.function.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SupplierStream<T> implements BasicStream<T>{
    final Supplier<Optional<T>> supplier;

    public SupplierStream(Supplier<Optional<T>> supplier) {
        this.supplier = supplier;
    }

    @Override
    public BasicStream<T> filter(Predicate<T> predicate) {
        return new SupplierStream<>(() -> getNext(predicate));
    }
    public Optional<T> getNext(Predicate<T> predicate){
        Optional<T> o = supplier.get();
        return o.flatMap((x)->predicate.test(x)?Optional.of(x):getNext(predicate));
    }
    @Override
    public BasicStream<T> limit(long maxSize) {
        AtomicInteger c = new AtomicInteger(0);
        return new SupplierStream<T>(() ->
                supplier.get().filter(x -> c.getAndIncrement() < maxSize));
    }

    @Override
    public <R> BasicStream<R> map(Function<T, R> mapper) {
        return new SupplierStream<>(() -> supplier.get().map(mapper));
    }

    @Override
    public void forEach(Consumer<T> action) {
        supplier.get().ifPresent((x) -> { action.accept(x); forEach(action); });
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        return supplier.get().map((x) -> 
            reduce(accumulator).map((a) -> accumulator.apply(x, a)).orElse(x));
    }


}

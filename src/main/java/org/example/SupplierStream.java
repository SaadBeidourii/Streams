package org.example;

import java.util.Optional;
import java.util.function.*;

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
        final long[] remainingSize = {maxSize}; // Create effectively final array to hold remaining size
        return new SupplierStream<>(() -> {
            Optional<T> nextElement = supplier.get();
            if (nextElement.isPresent() && remainingSize[0] > 0) {
                remainingSize[0]--; // Decrement remaining size
                return nextElement;
            } else {
                return Optional.empty();
            }
        });
    }

    @Override
    public <R> BasicStream<R> map(Function<T, R> mapper) {
        return new SupplierStream<>(() -> supplier.get().map(mapper));
    }

    @Override
    public void forEach(Consumer<T> action) {
        supplier.get().ifPresent((x) -> { action.accept(x); forEach(action); });

        // Autre possibilité, moins "fonctionnelle"
        // Optional<T> o = s.get();
        // while (o.isPresent()) {
        //   action.accept(x);
        //   o = s.get();
        // }
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        Optional<T> r = supplier.get();
        if(r.isEmpty()) return r;
        T acc = r.get();
        Optional<T> o = supplier.get();
        while (o.isPresent()) {
            acc = accumulator.apply(acc, o.get());
            o=supplier.get();
        }
        return Optional.of(acc);
    }


}

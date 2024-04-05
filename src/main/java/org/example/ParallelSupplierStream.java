package org.example;

import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.function.*;

public class ParallelSupplierStream<T> extends SupplierStream<T> {
    private static final ForkJoinPool globalPool = new ForkJoinPool();

    public ParallelSupplierStream(Supplier<Optional<T>> supplier) {
        super(supplier);
    }

    private void forEachRecur(Consumer<T> action){
        supplier.get().ifPresent(presentValue -> {
            action.accept(presentValue);
            forEachRecur(action);
        });
    }

    @Override
    public void forEach(Consumer<T> action) {
        globalPool.submit(() -> forEachRecur(action)).join();
    }

    @Override
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        return globalPool.submit(() -> {
            Optional<T> result = supplier.get();
            if (result.isEmpty()) {
                return Optional.empty();
            } else {
                T acc = result.get();
                while ((result = supplier.get()).isPresent()) {
                    acc = accumulator.apply(acc, result.get());
                }
                return Optional.of(acc);
            }
        }).join().map(o -> (T) o);
    }
}

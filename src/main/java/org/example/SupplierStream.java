package org.example;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class SupplierStream<T> implements BasicStream<T>{
    final Supplier<Optional<T>> supplier;

    public SupplierStream(Supplier<Optional<T>> supplier) {
        this.supplier = supplier;
    }

    @Override
    public SupplierStream<T> filter(Predicate<T> predicate) {
        return new SupplierStream<>(() -> getNext(predicate));
    }
    public Optional<T> getNext(Predicate<T> predicate){
        Optional<T> o = supplier.get();
        return o.flatMap((x)->predicate.test(x)?Optional.of(x):getNext(predicate));
    }
    @Override
    public SupplierStream<T> limit(long maxSize) {
        AtomicInteger c = new AtomicInteger(0);
        return new SupplierStream<T>(() ->
                supplier.get().filter(x -> c.getAndIncrement() < maxSize));
    }

    @Override
    public <R> SupplierStream<R> map(Function<T, R> mapper) {
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

    public static <T> SupplierStream<T> iterate(T seed, UnaryOperator<T> f) {
        AtomicReference<T> next = new AtomicReference<>(seed);
        return new SupplierStream<>(() -> {
            next.set(f.apply(next.get()));
            return Optional.of(next.get());
        });
    }

    public static <T> SupplierStream<T> concat(SupplierStream<T> s1, SupplierStream<T> s2) {
        return new SupplierStream<>(() -> s1.supplier.get().or(s2.supplier));
    }





    private static class ReduceTask<T> extends RecursiveTask<Optional<T>> {
        private final SupplierStream<T> stream;
        private final BinaryOperator<T> accumulator;

        public ReduceTask(SupplierStream<T> stream, BinaryOperator<T> accumulator) {
            this.stream = stream;
            this.accumulator = accumulator;
        }

        @Override
        protected Optional<T> compute() {
            return stream.supplier.get().flatMap(element -> {
                ReduceTask<T> nextTask = new ReduceTask<>(stream, accumulator);
                nextTask.fork();
                return Optional.of(accumulator.apply(element, nextTask.join().orElse(element)));
            });
        }

    }

    private static class ForEachTask<T> extends RecursiveTask<Void> {
        private final SupplierStream<T> stream;
        private final Consumer<T> action;

        public ForEachTask(SupplierStream<T> stream, Consumer<T> action) {
            this.stream = stream;
            this.action = action;
        }

        @Override
        protected Void compute() {
            stream.supplier.get().ifPresent(element -> {
                action.accept(element);
                ForEachTask<T> nextTask = new ForEachTask<>(stream, action);
                nextTask.fork();
                nextTask.join();
            });
            return null;
        }
    }

    public SupplierStream<T> parallel() {
        return new ParallelSupplierStream<>(supplier);
    }

}

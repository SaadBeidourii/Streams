import org.example.BasicStream;
import org.example.SupplierStream;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class SupplierStreamTest {

    static Supplier<Optional<Integer>> generator(int n) {
        return new Supplier<>() {
            int i = 0;
            @Override
            public Optional<Integer> get() {
                return (i < n) ? Optional.of(i++) : Optional.empty();
            }
        };
    }

    List<Integer> lacc;

    @Test
    public void testForEach() {
        SupplierStream<Integer> stream = new SupplierStream<>(generator(6));
        lacc = new ArrayList<>();
        stream.forEach(x -> lacc.add(x));
        List<Integer> excepted = List.of(0, 1, 2, 3, 4, 5);
        assertEquals(excepted, lacc);

        stream = new SupplierStream<>(Optional::empty);
        lacc = new ArrayList<>();
        stream.forEach(x -> lacc.add(x));
        assertTrue(lacc.isEmpty());
    }

    @Test
    public void testReduce() {
        SupplierStream<Integer> stream = new SupplierStream<>(generator(6));
        assertEquals(15, stream.reduce(Integer::sum).orElse(-1).intValue());
        assertEquals(15, stream.reduce(Integer::sum).orElse(-1).intValue());
        stream = new SupplierStream<>(Optional::empty);
        assertFalse(stream.reduce(Integer::sum).isPresent());
    }

    @Test
    public void testFilter() {
        SupplierStream<Integer> stream = new SupplierStream<>(generator(7));
        Predicate<Integer> p = x -> x > 10;
        BasicStream<Integer> filtered = stream.filter(p);
        assertFalse(filtered.reduce(Integer::sum).isPresent());

        stream = new SupplierStream<>(generator(7));
        p = x -> x % 2 == 0;
        filtered = stream.filter(p);
        assertEquals(12, filtered.reduce(Integer::sum).orElse(-1).intValue());
    }

    @Test
    public void testLimit() {
        assertEquals(6, new SupplierStream<>(generator(7)).
                limit(4).reduce(Integer::sum).orElse(-1).intValue());
        assertEquals(6, new SupplierStream<>(generator(4)).
                limit(10).reduce(Integer::sum).orElse(-1).intValue());
        assertEquals(45, new SupplierStream<>(generator(Integer.MAX_VALUE)).
                limit(10).reduce(Integer::sum).orElse(-1).intValue());
        assertEquals(-1, new SupplierStream<>(generator(42)).
                limit(0).reduce(Integer::sum).orElse(-1).intValue());
        assertEquals(-1, new SupplierStream<Integer>(Optional::empty).
                limit(4).reduce(Integer::sum).orElse(-1).intValue());
    }

    @Test
    public void testMap() {
        SupplierStream<Integer> stream = new SupplierStream<>(generator(10));
        BasicStream<Integer> mapped = stream.map(x -> x * 10);
        assertEquals(450, mapped.reduce(Integer::sum).orElse(-1).intValue());

        stream = new SupplierStream<>(generator(7));
        mapped = stream.map(x -> 1);
        assertEquals(7, mapped.reduce(Integer::sum).orElse(-1).intValue());

        stream = new SupplierStream<>(Optional::empty);
        mapped = stream.map(x -> 1);
        assertFalse(mapped.reduce(Integer::sum).isPresent());
    }

    @Test
    public void testConcat() {
        SupplierStream<Integer> stream1 = new SupplierStream<>(generator(3));
        SupplierStream<Integer> stream2 = new SupplierStream<>(generator(3));
        BasicStream<Integer> concat2 = SupplierStream.concat(stream1, stream2);

        assertEquals(6, concat2.reduce(Integer::sum).orElse(-1).intValue());
    }

    @Test
    public void testIterate() {
        SupplierStream<Integer> stream = new SupplierStream<>(generator(10));
        BasicStream<Integer> iteratedEasy = stream.iterate(1, x -> x * 2).limit(4);
         assertEquals(30, iteratedEasy.reduce(Integer::sum).orElse(-1).intValue());

        BasicStream<Integer> iterated = stream.iterate(1, x -> x * 2).limit(10);
        assertEquals(2046, iterated.reduce(Integer::sum).orElse(-1).intValue());


    }



}

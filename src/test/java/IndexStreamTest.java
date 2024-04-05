import org.example.BasicStream;
import org.example.IndexStream;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IndexStreamTest {

    @Test
    void testForEachAndReduce() {
        Function<Integer, Optional<String>> valueSupplier = index -> Optional.of("Element" + index);

        IndexStream<String> indexStream = new IndexStream<>(valueSupplier, 0, 5);

        // Test forEach
        StringBuilder resultBuilder = new StringBuilder();
        Consumer<String> action = resultBuilder::append;
        indexStream.forEach(action);
        assertEquals("Element0Element1Element2Element3Element4Element5", resultBuilder.toString());

        // Test reduce
        String reducedResult = indexStream.reduce(String::concat).orElse("");
        assertEquals("Element0Element1Element2Element3Element4Element5", reducedResult);
    }


    @Test
    public void testFindAny() {
        final int min = 0;
        final int max = 10;
        final Function f = x -> Optional.of(x);
        IndexStream<Integer> stream = new IndexStream<>(f, min, max);
        assertEquals(5, stream.findAny(x -> x == 5).intValue());
    }

    @Test
    void testLimit() {
        // Create an IndexStream with a value supplier that returns optional elements from 0 to 9
        IndexStream<Integer> indexStream = new IndexStream<>(index -> Optional.of(index), 0, 9);

        // Limit the stream to 5 elements
        BasicStream<Integer> limitedStream = indexStream.limit(5);

        // Collect the elements and verify
        StringBuilder result = new StringBuilder();
        Consumer<Integer> action = result::append;
        limitedStream.forEach(action);
        assertEquals("01234", result.toString());
    }

    @Test
    void testMap() {
        // Create an IndexStream with a value supplier that returns optional elements from 0 to 9
        IndexStream<Integer> indexStream = new IndexStream<>(index -> Optional.of(index), 0, 8);

        // Map the stream to its square
        BasicStream<Integer> mappedStream = indexStream.map(x -> x * x);

        // Collect the elements and verify
        StringBuilder result = new StringBuilder();
        Consumer<Integer> action = result::append;
        mappedStream.forEach(action);
        assertEquals("01491625364964", result.toString());
    }
}

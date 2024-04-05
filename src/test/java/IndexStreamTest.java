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
}

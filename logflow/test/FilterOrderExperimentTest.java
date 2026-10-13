import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilterOrderExperimentTest {

    @Test
    void filterOrderChangesWorkButNotResult() {
        Path input = Path.of("data", "access-small.log");

        AtomicInteger firstStatusChecks = new AtomicInteger();
        AtomicInteger firstPathChecks = new AtomicInteger();
        List<String> firstOutput = new ArrayList<>();

        PipelineBuilder.from(new FileLineSource(input))
                .via(new ParserStage())
                .via(new PredicateFilterStage(log -> {
                    firstStatusChecks.incrementAndGet();
                    return log.status() >= 400;
                }))
                .via(new PredicateFilterStage(log -> {
                    firstPathChecks.incrementAndGet();
                    return log.path().startsWith("/page/1");
                }))
                .via(new ProjectionStage("status", "path"))
                .to(firstOutput::add)
                .run();

        AtomicInteger secondStatusChecks = new AtomicInteger();
        AtomicInteger secondPathChecks = new AtomicInteger();
        List<String> secondOutput = new ArrayList<>();

        PipelineBuilder.from(new FileLineSource(input))
                .via(new ParserStage())
                .via(new PredicateFilterStage(log -> {
                    secondPathChecks.incrementAndGet();
                    return log.path().startsWith("/page/1");
                }))
                .via(new PredicateFilterStage(log -> {
                    secondStatusChecks.incrementAndGet();
                    return log.status() >= 400;
                }))
                .via(new ProjectionStage("status", "path"))
                .to(secondOutput::add)
                .run();

        assertEquals(firstOutput, secondOutput);
        assertEquals(11, firstOutput.size());

        int firstChecks = firstStatusChecks.get()
                + firstPathChecks.get();

        int secondChecks = secondStatusChecks.get()
                + secondPathChecks.get();

        assertEquals(220, firstChecks);
        assertEquals(311, secondChecks);
        assertTrue(firstChecks < secondChecks);
    }
}
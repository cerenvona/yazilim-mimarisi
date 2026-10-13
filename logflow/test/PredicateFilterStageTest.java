import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PredicateFilterStageTest {

    @Test
    void filtersByMinimumStatus() {
        PredicateFilterStage stage =
                new PredicateFilterStage(
                        log -> log.status() >= 400
                );

        List<LogRecord> output = new ArrayList<>();

        stage.process(log(200, "/api/users", "12:00:00Z"),
                output::add);
        stage.process(log(404, "/home", "12:00:01Z"),
                output::add);

        assertEquals(1, output.size());
        assertEquals(404, output.get(0).status());
    }

    @Test
    void filtersByPathPrefix() {
        PredicateFilterStage stage =
                new PredicateFilterStage(
                        log -> log.path().startsWith("/api")
                );

        List<LogRecord> output = new ArrayList<>();

        stage.process(log(200, "/home", "12:00:00Z"),
                output::add);
        stage.process(log(200, "/api/users", "12:00:01Z"),
                output::add);

        assertEquals(1, output.size());
        assertEquals("/api/users", output.get(0).path());
    }

    @Test
    void filtersByTimestampRange() {
        Instant start =
                Instant.parse("2026-10-06T12:00:00Z");
        Instant end =
                Instant.parse("2026-10-06T13:00:00Z");

        PredicateFilterStage stage =
                new PredicateFilterStage(log ->
                        !log.timestamp().isBefore(start)
                        && log.timestamp().isBefore(end)
                );

        List<LogRecord> output = new ArrayList<>();

        stage.process(log(200, "/a", "11:59:59Z"),
                output::add);
        stage.process(log(200, "/b", "12:30:00Z"),
                output::add);
        stage.process(log(200, "/c", "13:00:00Z"),
                output::add);

        assertEquals(1, output.size());
        assertEquals("/b", output.get(0).path());
    }

    private LogRecord log(
            int status,
            String path,
            String time
    ) {
        return new LogRecord(
                Instant.parse("2026-10-06T" + time),
                "192.0.2.1",
                "GET",
                path,
                status,
                100,
                "",
                Map.of(),
                "raw"
        );
    }
}
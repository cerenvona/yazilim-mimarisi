import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectionStageTest {

    @Test
    void keepsOnlySelectedFieldsInGivenOrder() {
        ProjectionStage stage =
                new ProjectionStage(
                        "clientIp", "status", "path"
                );

        List<String> output = new ArrayList<>();
        stage.process(sampleLog(), output::add);

        assertEquals(1, output.size());
        assertEquals(
                "clientIp=192.0.2.5,status=404,path=/api/users",
                output.get(0)
        );

        assertFalse(output.get(0).contains("userAgent"));
        assertFalse(output.get(0).contains("raw"));
    }

    @Test
    void rejectsUnknownField() {
        ProjectionStage stage =
                new ProjectionStage("unknownField");

        assertThrows(
                IllegalArgumentException.class,
                () -> stage.process(sampleLog(), value -> {
                })
        );
    }

    private LogRecord sampleLog() {
        return new LogRecord(
                Instant.parse("2026-10-06T15:00:00Z"),
                "192.0.2.5",
                "GET",
                "/api/users",
                404,
                250,
                "Private Browser Information",
                Map.of(),
                "original raw log line"
        );
    }
}
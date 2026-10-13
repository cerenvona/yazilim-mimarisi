import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PipelineIntegrationTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void runsThreeStagePipelineAndWritesExpectedFile()
            throws IOException {

        Path input = Path.of(
                "test",
                "fixtures",
                "pipeline-fixture.log"
        );

        Path output = temporaryDirectory
                .resolve("filtered.log");

        ParserStage parser = new ParserStage();

        PipelineBuilder.from(new FileLineSource(input))
                .via(parser)
                .via(new PredicateFilterStage(log ->
                        log.status() >= 400
                        && log.path().startsWith("/api")
                ))
                .via(new ProjectionStage("status", "path"))
                .to(new FileSink(output))
                .run();

        assertEquals(0L, parser.getInvalidCount());

        assertEquals(
                List.of(
                        "status=404,path=/api/users",
                        "status=500,path=/api/orders"
                ),
                Files.readAllLines(output)
        );
    }
}
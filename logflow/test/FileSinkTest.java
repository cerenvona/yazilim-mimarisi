import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileSinkTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void opensWritesAndClosesOutputFile()
            throws IOException {

        Path output = temporaryDirectory
                .resolve("results")
                .resolve("filtered.log");

        FileSink sink = new FileSink(output);

        sink.open();
        sink.consume("status=404,path=/api/users");
        sink.consume("status=500,path=/api/orders");
        sink.close();

        assertTrue(Files.exists(output));

        assertEquals(
                List.of(
                        "status=404,path=/api/users",
                        "status=500,path=/api/orders"
                ),
                Files.readAllLines(output)
        );
    }
}
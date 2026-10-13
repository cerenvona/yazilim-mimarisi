import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSink implements Sink<String> {

    private final Path outputPath;
    private BufferedWriter writer;

    public FileSink(Path outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public void open() {
        try {
            Path parent = outputPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            writer = Files.newBufferedWriter(
                    outputPath,
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Cikti dosyasi acilamadi: " + outputPath, e
            );
        }
    }

    @Override
    public void consume(String line) {
        try {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Cikti dosyasina yazilamadi", e
            );
        }
    }

    @Override
    public void close() {
        if (writer == null) {
            return;
        }

        try {
            writer.close();
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Cikti dosyasi kapatilamadi", e
            );
        }
    }
}
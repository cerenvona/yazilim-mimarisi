import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectionStage implements Stage<LogRecord, String> {

    private final List<String> fields;

    public ProjectionStage(String... fields) {
        if (fields.length == 0) {
            throw new IllegalArgumentException(
                    "En az bir alan secilmelidir"
            );
        }
        this.fields = List.copyOf(Arrays.asList(fields));
    }

    @Override
    public void process(LogRecord record, Emitter<String> out) {
        String result = fields.stream()
                .map(field -> field + "=" + valueOf(record, field))
                .collect(Collectors.joining(","));

        out.emit(result);
    }

    private Object valueOf(LogRecord record, String field) {
        return switch (field) {
            case "timestamp" -> record.timestamp();
            case "clientIp" -> record.clientIp();
            case "method" -> record.method();
            case "path" -> record.path();
            case "status" -> record.status();
            case "bytes" -> record.bytes();
            case "userAgent" -> record.userAgent();
            case "attributes" -> record.attributes();
            case "raw" -> record.raw();
            default -> throw new IllegalArgumentException(
                    "Bilinmeyen alan: " + field
            );
        };
    }
}
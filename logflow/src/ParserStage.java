import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserStage implements Stage<String, LogRecord> {
    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^\\s*(\\S+)\\s+\\S+\\s+\\S+\\s+"
        + "\\[([^\\]]+)\\]\\s+"
        + "\"(\\S+)\\s+(\\S+)\\s+HTTP/\\d+(?:\\.\\d+)?\"\\s+"
        + "(\\d{3})\\s+(\\d+|-)"
        + "(?:\\s+\"[^\"]*\"\\s+\"([^\"]*)\")?\\s*$"
    );

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern(
            "dd/MMM/uuuu:HH:mm:ss xx", Locale.ENGLISH
        ).withResolverStyle(ResolverStyle.STRICT);

    private long invalidCount;

    @Override
    public void process(String input, Emitter<LogRecord> out) {
        if (input == null || input.isBlank()) {
            invalidCount++;
            return;
        }

        Matcher matcher = LOG_PATTERN.matcher(input);

        if (!matcher.matches()) {
            invalidCount++;
            return;
        }

        LogRecord record;

        try {
            Instant timestamp = OffsetDateTime.parse(
                matcher.group(2), DATE_FORMAT
            ).toInstant();

            int status = Integer.parseInt(matcher.group(5));

            if (status < 100 || status > 599) {
                invalidCount++;
                return;
            }

            String bytesText = matcher.group(6);
            long bytes = bytesText.equals("-")
                ? 0L
                : Long.parseLong(bytesText);

            String userAgent = matcher.group(7);

            if (userAgent == null) {
                userAgent = "";
            }

            record = new LogRecord(
                timestamp,
                matcher.group(1),
                matcher.group(3),
                matcher.group(4),
                status,
                bytes,
                userAgent,
                Map.of(),
                input
            );

        } catch (DateTimeParseException | NumberFormatException e) {
            invalidCount++;
            return;
        }

        out.emit(record);
    }

    public long getInvalidCount() {
        return invalidCount;
    }
}
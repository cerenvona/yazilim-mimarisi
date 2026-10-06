import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserStageTest {

    private static final String VALID =
            "192.0.2.1 - - [06/Oct/2026:15:00:00 +0000] "
            + "\"GET /index.html HTTP/1.1\" 200 1234";

    private LogRecord parseValid(String line) {
        ParserStage parser = new ParserStage();
        List<LogRecord> records = new ArrayList<>();

        parser.process(line, records::add);

        assertEquals(1, records.size());
        assertEquals(0L, parser.getInvalidCount());

        return records.get(0);
    }

    private void assertInvalid(String line) {
        ParserStage parser = new ParserStage();
        List<LogRecord> records = new ArrayList<>();

        parser.process(line, records::add);

        assertTrue(records.isEmpty());
        assertEquals(1L, parser.getInvalidCount());
    }

    @Test
    void validLineProducesCorrectRecord() {
        LogRecord record = parseValid(VALID);

        assertEquals(
                Instant.parse("2026-10-06T15:00:00Z"),
                record.timestamp()
        );
        assertEquals("192.0.2.1", record.clientIp());
        assertEquals("GET", record.method());
        assertEquals("/index.html", record.path());
        assertEquals(200, record.status());
        assertEquals(1234L, record.bytes());
        assertEquals("", record.userAgent());
        assertTrue(record.attributes().isEmpty());
        assertEquals(VALID, record.raw());
    }

    @Test
    void missingFieldIsSkipped() {
        assertInvalid(VALID.replace(" 200 1234", " 200"));
    }

    @Test
    void invalidTimestampIsSkipped() {
        assertInvalid(
                VALID.replace("06/Oct/2026", "32/Oct/2026")
        );
    }

    @Test
    void invalidStatusTextIsSkipped() {
        assertInvalid(VALID.replace(" 200 ", " abc "));
    }

    @Test
    void blankLineIsSkipped() {
        assertInvalid("   ");
    }

    @Test
    void extraWhitespaceIsAccepted() {
        String line =
                "  192.0.2.1   -   -   "
                + "[06/Oct/2026:15:00:00 +0000]   "
                + "\"GET   /index.html   HTTP/1.1\"   "
                + "200   1234  ";

        LogRecord record = parseValid(line);

        assertEquals("/index.html", record.path());
        assertEquals(200, record.status());
        assertEquals(line, record.raw());
    }

    @Test
    void quotedUserAgentPreservesSpaces() {
        String line = VALID
                + " \"https://example.com/\" "
                + "\"Mozilla/5.0 Test Browser\"";

        LogRecord record = parseValid(line);

        assertEquals(
                "Mozilla/5.0 Test Browser",
                record.userAgent()
        );
    }

    @Test
    void queryStringIsPreserved() {
        String line = VALID.replace(
                "/index.html",
                "/search?q=java&page=2"
        );

        LogRecord record = parseValid(line);

        assertEquals(
                "/search?q=java&page=2",
                record.path()
        );
    }

    @Test
    void nullLineIsSkipped() {
        assertInvalid(null);
    }

    @Test
    void dashByteCountBecomesZero() {
        LogRecord record = parseValid(
                VALID.replace(" 1234", " -")
        );

        assertEquals(0L, record.bytes());
    }

    @Test
    void outOfRangeStatusIsSkipped() {
        assertInvalid(VALID.replace(" 200 ", " 099 "));
        assertInvalid(VALID.replace(" 200 ", " 600 "));
    }

    @Test
    void overflowingByteCountIsSkipped() {
        assertInvalid(
                VALID.replace(
                        " 1234",
                        " 999999999999999999999999"
                )
        );
    }

    @Test
    void invalidLinesAreCountedAndProcessingContinues() {
        ParserStage parser = new ParserStage();
        List<LogRecord> records = new ArrayList<>();

        parser.process("bozuk satir", records::add);
        parser.process(VALID, records::add);
        parser.process("", records::add);

        assertEquals(2L, parser.getInvalidCount());
        assertEquals(1, records.size());
        assertEquals("/index.html", records.get(0).path());
    }
}
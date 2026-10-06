public class ConsoleSink implements Sink<LogRecord> {
    @Override
    public void consume(LogRecord record) {
        System.out.printf(
            "Zaman=%s | IP=%s | Metot=%s | Yol=%s"
            + " | Durum=%d | Boyut=%d | Tarayici=%s%n",
            record.timestamp(),
            record.clientIp(),
            record.method(),
            record.path(),
            record.status(),
            record.bytes(),
            record.userAgent()
        );
    }
}
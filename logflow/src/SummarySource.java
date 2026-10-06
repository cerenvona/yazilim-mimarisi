import java.util.function.LongSupplier;

public class SummarySource<T> implements Source<T> {
    private final Source<T> source;
    private final LongSupplier invalidCount;

    public SummarySource(
            Source<T> source,
            LongSupplier invalidCount) {
        this.source = source;
        this.invalidCount = invalidCount;
    }

    @Override
    public void produce(Emitter<T> out) {
        source.produce(out);

        System.out.println(
            "Hatali satir sayisi: " + invalidCount.getAsLong()
        );
    }
}
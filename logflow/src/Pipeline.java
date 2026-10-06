import java.util.ArrayList;
import java.util.List;

public class Pipeline<T> {
    private final Source<T> source;
    private final List<Stage<?, ?>> stages;
    private Sink<T> sink;

    private Pipeline(Source<T> source, List<Stage<?, ?>> stages) {
        this.source = source;
        this.stages = stages;
    }

    public static <T> Pipeline<T> from(Source<T> source) {
        return new Pipeline<>(source, new ArrayList<>());
    }

    public <R> Pipeline<R> then(Stage<T, R> stage) {
        List<Stage<?, ?>> nextStages = new ArrayList<>(stages);
        nextStages.add(stage);

        Source<R> nextSource = out -> source.produce(input -> {
            try {
                stage.process(input, out);
            } catch (StageException e) {
                throw new IllegalStateException(
                    "Pipeline asamasinda hata olustu", e
                );
            }
        });

        return new Pipeline<>(nextSource, nextStages);
    }

    public Pipeline<T> to(Sink<T> sink) {
        this.sink = sink;
        return this;
    }

    public void run() {
        if (sink == null) {
            throw new IllegalStateException(
                "Pipeline icin bir sink belirlenmeli"
            );
        }

        source.produce(item -> sink.consume(item));
    }
}
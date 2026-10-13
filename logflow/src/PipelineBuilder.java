public class PipelineBuilder<T> {

    private final Pipeline<T> pipeline;

    private PipelineBuilder(Pipeline<T> pipeline) {
        this.pipeline = pipeline;
    }

    public static <T> PipelineBuilder<T> from(
            Source<T> source
    ) {
        return new PipelineBuilder<>(
                Pipeline.from(source)
        );
    }

    public <R> PipelineBuilder<R> via(
            Stage<T, R> stage
    ) {
        return new PipelineBuilder<>(
                pipeline.then(stage)
        );
    }

    public Pipeline<T> to(Sink<T> sink) {
        return pipeline.to(sink);
    }
}
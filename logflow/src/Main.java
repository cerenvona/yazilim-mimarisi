public class Main {

    public static void main(String[] args) {
        try {
            CommandLineOptions options =
                    CommandLineOptions.parse(args);

            ParserStage parser = new ParserStage();

            Source<String> source = new SummarySource<>(
                    new FileLineSource(options.input()),
                    parser::getInvalidCount
            );

            PipelineBuilder<LogRecord> builder =
                    PipelineBuilder.from(source)
                            .via(parser);

            if (options.minStatus() != null) {
                int minimum = options.minStatus();

                builder = builder.via(
                        new PredicateFilterStage(
                                record -> record.status() >= minimum
                        )
                );
            }

            if (options.pathPrefix() != null) {
                String prefix = options.pathPrefix();

                builder = builder.via(
                        new PredicateFilterStage(
                                record -> record.path()
                                        .startsWith(prefix)
                        )
                );
            }

            builder
                    .via(new ProjectionStage(options.fields()))
                    .to(new FileSink(options.output()))
                    .run();

            System.out.println(
                    "Cikti dosyasi: " + options.output()
            );

        } catch (IllegalArgumentException e) {
            System.err.println("Hata: " + e.getMessage());
            printUsage();
        }
    }

    private static void printUsage() {
        System.err.println(
                "Kullanim: ./logflow <girdi> <cikti> "
                + "[--min-status sayi] "
                + "[--path-prefix yol] "
                + "[--fields alan1,alan2]"
        );
    }
}
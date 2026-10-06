import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println(
                "Kullanim: java -cp out Main data/access-small.log"
            );
            return;
        }

        Path filePath = Path.of(args[0]);

        ParserStage parser = new ParserStage();

        Source<String> source = new SummarySource<>(
            new FileLineSource(filePath),
            parser::getInvalidCount
        );

        Pipeline.from(source)
                .then(parser)
                .to(new ConsoleSink())
                .run();
    }
}
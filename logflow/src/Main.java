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

        Pipeline.from(new FileLineSource(filePath))
                .to(new ConsoleSink())
                .run();
    }
}
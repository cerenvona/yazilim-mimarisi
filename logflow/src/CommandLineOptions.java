import java.nio.file.Path;
import java.util.Arrays;

public record CommandLineOptions(
        Path input,
        Path output,
        Integer minStatus,
        String pathPrefix,
        String[] fields
) {

    public static CommandLineOptions parse(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException(
                    "Girdi ve cikti dosyasi belirtilmelidir"
            );
        }

        Integer minStatus = null;
        String pathPrefix = null;
        String[] fields = {
                "timestamp", "clientIp", "status", "path"
        };

        for (int i = 2; i < args.length; i++) {
            switch (args[i]) {
                case "--min-status" ->
                        minStatus = Integer.parseInt(
                                nextValue(args, ++i, "--min-status")
                        );

                case "--path-prefix" ->
                        pathPrefix = nextValue(
                                args, ++i, "--path-prefix"
                        );

                case "--fields" ->
                        fields = Arrays.stream(
                                        nextValue(
                                                args, ++i, "--fields"
                                        ).split(",")
                                )
                                .map(String::trim)
                                .filter(value -> !value.isEmpty())
                                .toArray(String[]::new);

                default -> throw new IllegalArgumentException(
                        "Bilinmeyen secenek: " + args[i]
                );
            }
        }

        return new CommandLineOptions(
                Path.of(args[0]),
                Path.of(args[1]),
                minStatus,
                pathPrefix,
                fields
        );
    }

    private static String nextValue(
            String[] args,
            int index,
            String option
    ) {
        if (index >= args.length) {
            throw new IllegalArgumentException(
                    option + " icin deger eksik"
            );
        }

        return args[index];
    }
}
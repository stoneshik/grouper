package grouper.io.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class InputFromFileHandler implements InputHandler {
    private final Path pathToFile;

    public InputFromFileHandler(Path pathToFile) {
        this.pathToFile = pathToFile;
    }

    @Override
    public Optional<BufferedReader> input() throws IOException {
        if (!Files.exists(pathToFile)) {
            System.err.println("File not found: " + pathToFile);
            return Optional.empty();
        }
        BufferedReader reader = Files.newBufferedReader(pathToFile);
        return Optional.of(reader);
    }
}

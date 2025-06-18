package grouper.io.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

public interface InputHandler {
    Optional<BufferedReader> input() throws IOException;
}

package grouper.io.output;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class OutputToFileHandler implements OutputHandler {
    private final Path pathToFile;

    public OutputToFileHandler(Path pathToFile) {
        this.pathToFile = pathToFile;
    }

    @Override
    public void output(List<Set<String>> resultGroups) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(pathToFile)) {
            writer.write("Количество групп с более чем одним элементом: " + resultGroups.size());
            writer.newLine();
            writer.newLine();

            int groupNumber = 1;
            for (Set<String> group : resultGroups) {
                writer.write("Группа " + groupNumber++);
                writer.newLine();
                for (String row : group) {
                    writer.write(row);
                    writer.newLine();
                }
                writer.newLine();
            }
        }
    }
}

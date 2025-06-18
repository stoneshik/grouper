package grouper.io.output;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface OutputHandler {
    void output(List<Set<String>> resultGroups) throws IOException;
}

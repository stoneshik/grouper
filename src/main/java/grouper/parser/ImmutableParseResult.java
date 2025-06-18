package grouper.parser;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;


public record ImmutableParseResult(
    List<String[]> rows,
    Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds
) {
    public ImmutableParseResult(
        List<String[]> rows,
        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds
    ) {
        this.rows = Collections.unmodifiableList(rows);
        this.columnValueToRowIds = Collections.unmodifiableMap(columnValueToRowIds);
    }
}

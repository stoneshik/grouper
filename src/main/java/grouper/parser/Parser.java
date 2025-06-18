package grouper.parser;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public abstract class Parser {
    public abstract ImmutableParseResult parse() throws IOException;

    protected void parseLineAndUpdateRowsAndColumnValueToRowIds(
        List<String[]> rows,
        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds,
        String line,
        int rowIndex
    ) {
        if (isInvalidLine(line)) {
            return;
        }
        String[] tokens = parseLineAndUpdateRows(rows, line);
        parseLineAndUpdateColumnValueToRowIds(
            tokens,
            columnValueToRowIds,
            rowIndex
        );
    }

    private boolean isInvalidLine(String line) {
        // строка некорректна, если содержит несбалансированные кавычки
        long quoteCount = line.chars().filter(ch -> ch == '"').count();
        return quoteCount % 2 != 0;
    }

    private String[] parseLineAndUpdateRows(List<String[]> rows, String line) {
        String[] tokens = Arrays.stream(line.split(";", -1))
            .map(String::trim)
            .toArray(String[]::new);
        rows.add(tokens);
        return tokens;
    }

    private void parseLineAndUpdateColumnValueToRowIds(
        String[] tokens,
        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds,
        int rowIndex
    ) {
        for (int i = 0; i < tokens.length; i++) {
            String value = tokens[i];
            if (value.isEmpty()) {
                continue;
            }
            value = value.intern();
            columnValueToRowIds
                .computeIfAbsent(i, k -> new HashMap<>())
                .computeIfAbsent(value, k -> new HashSet<>())
                .add(rowIndex);
        }
    }
}

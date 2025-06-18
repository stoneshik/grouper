package grouper.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParserFromBufferedReader extends Parser {
    private final BufferedReader bufferedReader;

    public ParserFromBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    @Override
    public ImmutableParseResult parse() throws IOException {
        List<String[]> rows = new ArrayList<>();
        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds = new HashMap<>();
        String line;
        int rowIndex = 0;
        try (bufferedReader) {
            while ((line = bufferedReader.readLine()) != null) {
                parseLineAndUpdateRowsAndColumnValueToRowIds(
                    rows,
                    columnValueToRowIds,
                    line,
                    rowIndex
                );
                rowIndex++;
            }
        }
        return new ImmutableParseResult(rows, columnValueToRowIds);
    }
}

package grouper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import grouper.dsu.ColumnBasedRowGrouper;
import grouper.io.input.InputFromFileHandler;
import grouper.io.output.OutputHandler;
import grouper.io.output.OutputToFileHandler;
import grouper.parser.ImmutableParseResult;
import grouper.parser.Parser;
import grouper.parser.ParserFromBufferedReader;


public class Main {
    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        if (args.length != 1) {
            System.err.println("Запуск командой: java -jar grouper.jar <path-to-file>");
            return;
        }
        groupStrings(args);
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("Время выполнения: " + elapsed + " мс");
    }

    private static void groupStrings(String[] args) throws IOException {
        // Чтение данных
        Path path = Paths.get(args[0]);
        List<String[]> rows;
        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds;
        InputFromFileHandler inputHandler = new InputFromFileHandler(path);

        try (BufferedReader reader = inputHandler.input().orElseThrow()) {
            Parser parser = new ParserFromBufferedReader(reader);
            ImmutableParseResult parseResult = parser.parse();
            rows = parseResult.rows();
            columnValueToRowIds = parseResult.columnValueToRowIds();
        }

        // Группировка строк
        List<Set<String>> resultGroups = ColumnBasedRowGrouper.groupRows(rows, columnValueToRowIds);

        // Вывод
        Path pathToFile = Paths.get("output.txt");
        OutputHandler outputHandler = new OutputToFileHandler(pathToFile);
        outputHandler.output(resultGroups);

        System.out.println("Количество групп с более чем одним элементом: " + resultGroups.size());
    }
}

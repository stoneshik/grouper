package grouper;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import grouper.parser.ImmutableParseResult;
import grouper.parser.Parser;
import grouper.parser.ParserFromBufferedReader;

class ParserFromBufferedReaderTest {
    @Test
    @DisplayName("Парсинг пустой строки")
    void testParseEmptyReader() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(""));
        Parser parser = new ParserFromBufferedReader(reader);
        ImmutableParseResult parseResult = parser.parse();

        assertTrue(parseResult.rows().isEmpty());
        assertTrue(parseResult.columnValueToRowIds().isEmpty());
    }

    @Test
    @DisplayName("Парсинг одной строки")
    void testParseSingleLine() throws IOException {
        String testLine = "\"A1\"";
        BufferedReader reader = new BufferedReader(new StringReader(testLine));
        Parser parser = new ParserFromBufferedReader(reader);
        ImmutableParseResult parseResult = parser.parse();

        List<String[]> rows = parseResult.rows();
        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds = parseResult.columnValueToRowIds();

        assertEquals(1, rows.size());
        assertEquals(1, rows.get(0).length);
        assertEquals(testLine, rows.get(0)[0]);

        assertEquals(1, columnValueToRowIds.size());
        assertEquals(1, columnValueToRowIds.get(0).size());
        assertEquals(1, columnValueToRowIds.get(0).get(testLine).size());
        assertTrue(columnValueToRowIds.get(0).get(testLine).contains(0));
    }

    @Test
    @DisplayName("Парсинг 3 строк")
    void testParseMultipleLines() throws IOException {
        String input = "\"A1\"\n\"A2\"\n\"A3\"";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        Parser parser = new ParserFromBufferedReader(reader);
        ImmutableParseResult parseResult = parser.parse();

        List<String[]> rows = parseResult.rows();
        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds = parseResult.columnValueToRowIds();

        assertEquals(3, rows.size());
        assertArrayEquals(new String[]{"\"A1\""}, rows.get(0));
        assertArrayEquals(new String[]{"\"A2\""}, rows.get(1));
        assertArrayEquals(new String[]{"\"A3\""}, rows.get(2));

        assertEquals(1, columnValueToRowIds.size());
        assertEquals(3, columnValueToRowIds.get(0).size());
        assertIterableEquals(new HashSet<>(List.of(0)), columnValueToRowIds.get(0).get("\"A1\""));
        assertIterableEquals(new HashSet<>(List.of(1)), columnValueToRowIds.get(0).get("\"A2\""));
        assertIterableEquals(new HashSet<>(List.of(2)), columnValueToRowIds.get(0).get("\"A3\""));
    }

    @Test
    @DisplayName("Парсинг с закрытым BufferedReader")
    void testParseWithClosedReader() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader("\"A1\""));
        reader.close();
        Parser parser = new ParserFromBufferedReader(reader);

        assertThrows(IOException.class, parser::parse);
    }

    @Test
    @DisplayName("Парсинг 3 строк с пробелами")
    void testParseWithWhitespaceLines() throws IOException {
        String input = "  \"A1\"  \n\t\t\"A2\"\t\n   \"A3\"   ";
        BufferedReader reader = new BufferedReader(new StringReader(input));

        Parser parser = new ParserFromBufferedReader(reader);
        ImmutableParseResult parseResult = parser.parse();

        List<String[]> rows = parseResult.rows();
        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds = parseResult.columnValueToRowIds();

        assertEquals(3, rows.size());
        assertArrayEquals(new String[]{"\"A1\""}, rows.get(0));
        assertArrayEquals(new String[]{"\"A2\""}, rows.get(1));
        assertArrayEquals(new String[]{"\"A3\""}, rows.get(2));

        assertEquals(1, columnValueToRowIds.size());
        assertEquals(3, columnValueToRowIds.get(0).size());
        assertIterableEquals(new HashSet<>(List.of(0)), columnValueToRowIds.get(0).get("\"A1\""));
        assertIterableEquals(new HashSet<>(List.of(1)), columnValueToRowIds.get(0).get("\"A2\""));
        assertIterableEquals(new HashSet<>(List.of(2)), columnValueToRowIds.get(0).get("\"A3\""));
    }

    @Test
    @DisplayName("Парсинг где BufferedReader равен null")
    void testParseWithNullReader() {
        Parser parser = new ParserFromBufferedReader(null);
        assertThrows(NullPointerException.class, parser::parse);
    }

    @Test
    @DisplayName("Парсинг некорректных строк")
    void testParseIncorrectLines() throws IOException {
        String input = "\"8383\"200000741652251\"\n\"79855053897\"83100000580443402\";\"200000133000191\"";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        Parser parser = new ParserFromBufferedReader(reader);
        ImmutableParseResult parseResult = parser.parse();

        assertTrue(parseResult.rows().isEmpty());
        assertTrue(parseResult.columnValueToRowIds().isEmpty());
    }

    @Test
    @DisplayName("Парсинг 3 строк с несколькими значениями в каждой строке")
    void testParseMultipleLinesWithMultipleValues() throws IOException {
        String input = "\"A1\";\"B1\";\"C1\"\n\"A2\";\"B2\";\"C2\"\n\"A3\";\"B3\"";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        Parser parser = new ParserFromBufferedReader(reader);
        ImmutableParseResult parseResult = parser.parse();

        List<String[]> rows = parseResult.rows();
        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds = parseResult.columnValueToRowIds();

        assertEquals(3, rows.size());
        assertArrayEquals(new String[]{"\"A1\"", "\"B1\"", "\"C1\""}, rows.get(0));
        assertArrayEquals(new String[]{"\"A2\"", "\"B2\"", "\"C2\""}, rows.get(1));
        assertArrayEquals(new String[]{"\"A3\"", "\"B3\""}, rows.get(2));

        assertEquals(3, columnValueToRowIds.size());
        assertEquals(3, columnValueToRowIds.get(0).size());
        assertIterableEquals(new HashSet<>(List.of(0)), columnValueToRowIds.get(0).get("\"A1\""));
        assertIterableEquals(new HashSet<>(List.of(1)), columnValueToRowIds.get(0).get("\"A2\""));
        assertIterableEquals(new HashSet<>(List.of(2)), columnValueToRowIds.get(0).get("\"A3\""));
        assertEquals(3, columnValueToRowIds.get(1).size());
        assertIterableEquals(new HashSet<>(List.of(0)), columnValueToRowIds.get(1).get("\"B1\""));
        assertIterableEquals(new HashSet<>(List.of(1)), columnValueToRowIds.get(1).get("\"B2\""));
        assertIterableEquals(new HashSet<>(List.of(2)), columnValueToRowIds.get(1).get("\"B3\""));
        assertEquals(2, columnValueToRowIds.get(2).size());
        assertIterableEquals(new HashSet<>(List.of(0)), columnValueToRowIds.get(2).get("\"C1\""));
        assertIterableEquals(new HashSet<>(List.of(1)), columnValueToRowIds.get(2).get("\"C2\""));
    }

    @Test
    @DisplayName("Парсинг 3 строк с несколькими значениями в каждой строке, которые образуют несколько групп")
    void testParseMultipleLinesWithMultipleValuesAndGroups() throws IOException {
        String input = "\"111\";\"123\";\"222\"\n\"200\";\"123\";\"100\"\n\"300\";\"\";\"100\"";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        Parser parser = new ParserFromBufferedReader(reader);
        ImmutableParseResult parseResult = parser.parse();

        List<String[]> rows = parseResult.rows();
        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds = parseResult.columnValueToRowIds();

        assertEquals(3, rows.size());
        assertArrayEquals(new String[]{"\"111\"", "\"123\"", "\"222\""}, rows.get(0));
        assertArrayEquals(new String[]{"\"200\"", "\"123\"", "\"100\""}, rows.get(1));
        assertArrayEquals(new String[]{"\"300\"", "\"\"", "\"100\""}, rows.get(2));

        assertEquals(3, columnValueToRowIds.size());
        assertEquals(3, columnValueToRowIds.get(0).size());
        assertIterableEquals(new HashSet<>(List.of(0)), columnValueToRowIds.get(0).get("\"111\""));
        assertIterableEquals(new HashSet<>(List.of(1)), columnValueToRowIds.get(0).get("\"200\""));
        assertIterableEquals(new HashSet<>(List.of(2)), columnValueToRowIds.get(0).get("\"300\""));
        assertEquals(2, columnValueToRowIds.get(1).size());
        assertIterableEquals(new HashSet<>(List.of(0, 1)), columnValueToRowIds.get(1).get("\"123\""));
        assertIterableEquals(new HashSet<>(List.of(2)), columnValueToRowIds.get(1).get("\"\""));
        assertEquals(2, columnValueToRowIds.get(2).size());
        assertIterableEquals(new HashSet<>(List.of(0)), columnValueToRowIds.get(2).get("\"222\""));
        assertIterableEquals(new HashSet<>(List.of(1, 2)), columnValueToRowIds.get(2).get("\"100\""));
    }
}

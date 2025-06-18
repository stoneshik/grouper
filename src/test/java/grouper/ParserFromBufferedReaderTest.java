package grouper;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import grouper.parser.ImmutableParseResult;
import grouper.parser.Parser;
import grouper.parser.ParserFromBufferedReader;

class ParserFromBufferedReaderTest {
    /* Тесты на общую работоспособность парсера без специфики задания */
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
        final String testLine = "test line";
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
        String input = "line1\nline2\nline3";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        Parser parser = new ParserFromBufferedReader(reader);
        ImmutableParseResult parseResult = parser.parse();

        List<String[]> rows = parseResult.rows();
        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds = parseResult.columnValueToRowIds();

        assertEquals(3, rows.size());
        assertEquals(1, rows.get(0).length);
        assertEquals("line1", rows.get(0)[0]);
        assertEquals(1, rows.get(1).length);
        assertEquals("line2", rows.get(1)[0]);
        assertEquals(1, rows.get(2).length);
        assertEquals("line3", rows.get(2)[0]);

        assertEquals(1, columnValueToRowIds.size());
        assertEquals(3, columnValueToRowIds.get(0).size());
        assertEquals(1, columnValueToRowIds.get(0).get("line1").size());
        assertTrue(columnValueToRowIds.get(0).get("line1").contains(0));
        assertEquals(1, columnValueToRowIds.get(0).get("line2").size());
        assertTrue(columnValueToRowIds.get(0).get("line2").contains(1));
        assertEquals(1, columnValueToRowIds.get(0).get("line3").size());
        assertTrue(columnValueToRowIds.get(0).get("line3").contains(2));
    }

    @Test
    @DisplayName("Парсинг с закрытым BufferedReader")
    void testParseWithClosedReader() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader("test"));
        reader.close();
        Parser parser = new ParserFromBufferedReader(reader);

        assertThrows(IOException.class, parser::parse);
    }

    @Test
    @DisplayName("Парсинг 3 строк с пробелами")
    void testParseWithWhitespaceLines() throws IOException {
        String input = "  line1  \n\t\tline2\t\n   line3   ";
        BufferedReader reader = new BufferedReader(new StringReader(input));

        Parser parser = new ParserFromBufferedReader(reader);
        ImmutableParseResult parseResult = parser.parse();

        List<String[]> rows = parseResult.rows();
        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds = parseResult.columnValueToRowIds();

        assertEquals(3, rows.size());
        assertEquals(1, rows.get(0).length);
        assertEquals("line1", rows.get(0)[0]);
        assertEquals(1, rows.get(1).length);
        assertEquals("line2", rows.get(1)[0]);
        assertEquals(1, rows.get(2).length);
        assertEquals("line3", rows.get(2)[0]);

        assertEquals(1, columnValueToRowIds.size());
        assertEquals(3, columnValueToRowIds.get(0).size());
        assertEquals(1, columnValueToRowIds.get(0).get("line1").size());
        assertTrue(columnValueToRowIds.get(0).get("line1").contains(0));
        assertEquals(1, columnValueToRowIds.get(0).get("line2").size());
        assertTrue(columnValueToRowIds.get(0).get("line2").contains(1));
        assertEquals(1, columnValueToRowIds.get(0).get("line3").size());
        assertTrue(columnValueToRowIds.get(0).get("line3").contains(2));
    }

    @Test
    @DisplayName("Парсинг где BufferedReader равен null")
    void testParseWithNullReader() {
        Parser parser = new ParserFromBufferedReader(null);
        assertThrows(NullPointerException.class, parser::parse);
    }

    /* Тесты проверяющие работоспособность парсера с учетом специфики задания */
}

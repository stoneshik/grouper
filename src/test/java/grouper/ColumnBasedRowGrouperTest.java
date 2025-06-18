package grouper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import grouper.dsu.ColumnBasedRowGrouper;

class ColumnBasedRowGrouperTest {
    @Test
    @DisplayName("Должен возвращаться пустой список когда не переданы строки")
    void testGroupRowsWithEmptyInput() {
        List<String[]> rows = new ArrayList<>();
        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds = new HashMap<>();

        List<Set<String>> result = ColumnBasedRowGrouper.groupRows(rows, columnValueToRowIds);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Должен возвращаться пустой список, когда все группы имеют один элемент")
    void testGroupRowsWithOnlySingleElementGroups() {
        List<String[]> rows = Arrays.asList(
            new String[]{"A", "1"},
            new String[]{"B", "2"},
            new String[]{"C", "3"}
        );

        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds = new HashMap<>();
        // Каждая строка имеет уникальные значения, поэтому группировка не происходит
        Map<String, Set<Integer>> column0 = new HashMap<>();
        column0.put("A", Set.of(0));
        column0.put("B", Set.of(1));
        column0.put("C", Set.of(2));
        columnValueToRowIds.put(0, column0);

        List<Set<String>> result = ColumnBasedRowGrouper.groupRows(rows, columnValueToRowIds);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Должны группироваться строки с одинаковыми значениями в один столбец")
    void testGroupRowsWithSingleColumnGrouping() {
        List<String[]> rows = Arrays.asList(
            new String[]{"A", "1"},
            new String[]{"A", "2"},
            new String[]{"B", "3"}
        );

        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds = new HashMap<>();
        Map<String, Set<Integer>> column0 = new HashMap<>();
        column0.put("A", Set.of(0, 1)); // Строки 0 и 1 имеют одинаковое значение "A"
        column0.put("B", Set.of(2));
        columnValueToRowIds.put(0, column0);

        List<Set<String>> result = ColumnBasedRowGrouper.groupRows(rows, columnValueToRowIds);

        assertEquals(1, result.size());
        Set<String> group = result.get(0);
        assertEquals(2, group.size());
        assertTrue(group.contains("A;1"));
        assertTrue(group.contains("A;2"));
    }

    @Test
    @DisplayName("Должны группироваться строки с одинаковыми значениями в несколько столбцов")
    void testGroupRowsWithMultipleColumnGrouping() {
        List<String[]> rows = Arrays.asList(
            new String[]{"A", "X"},
            new String[]{"B", "X"},
            new String[]{"C", "Y"},
            new String[]{"A", "Z"}
        );

        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds = new HashMap<>();

        // Группировка столбца 0
        Map<String, Set<Integer>> column0 = new HashMap<>();
        column0.put("A", Set.of(0, 3)); // Строки 0 и 3 имеют "A"
        column0.put("B", Set.of(1));
        column0.put("C", Set.of(2));
        columnValueToRowIds.put(0, column0);

        // Группировка столбца 1
        Map<String, Set<Integer>> column1 = new HashMap<>();
        column1.put("X", Set.of(0, 1)); // Строки 0 и 1 имеют "X"
        column1.put("Y", Set.of(2));
        column1.put("Z", Set.of(3));
        columnValueToRowIds.put(1, column1);

        List<Set<String>> result = ColumnBasedRowGrouper.groupRows(rows, columnValueToRowIds);

        assertEquals(1, result.size());
        Set<String> group = result.get(0);
        assertEquals(3, group.size()); // Строки 0, 1, 3 должны быть связаны
        assertTrue(group.contains("A;X"));
        assertTrue(group.contains("B;X"));
        assertTrue(group.contains("A;Z"));
    }

    @Test
    @DisplayName("Должны сортироваться группы по размеру в порядке убывания")
    void testGroupsSortedBySize() {
        List<String[]> rows = Arrays.asList(
            new String[]{"A", "1"}, // Группа 1 (размер 2)
            new String[]{"A", "2"}, // Группа 1
            new String[]{"B", "3"}, // Группа 2 (размер 3)
            new String[]{"B", "4"}, // Группа 2
            new String[]{"B", "5"}  // Группа 2
        );

        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds = new HashMap<>();
        Map<String, Set<Integer>> column0 = new HashMap<>();
        column0.put("A", Set.of(0, 1)); // Группа размером 2
        column0.put("B", Set.of(2, 3, 4)); // Группа размером 3
        columnValueToRowIds.put(0, column0);

        List<Set<String>> result = ColumnBasedRowGrouper.groupRows(rows, columnValueToRowIds);

        assertEquals(2, result.size());
        // Первая группа должна быть больше (размер 3)
        assertEquals(3, result.get(0).size());
        // Вторая группа должна быть меньше (размер 2)
        assertEquals(2, result.get(1).size());
    }

    @Test
    @DisplayName("Должна обрабатываться сложная транзитивная группировка")
    void testTransitiveGrouping() {
        List<String[]> rows = Arrays.asList(
            new String[]{"A", "X"},
            new String[]{"A", "Y"},
            new String[]{"B", "Y"},
            new String[]{"B", "Z"},
            new String[]{"C", "Z"}
        );

        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds = new HashMap<>();

        // Группировка столбца 0: A связывает 0,1, а B связывает 2,3.
        Map<String, Set<Integer>> column0 = new HashMap<>();
        column0.put("A", Set.of(0, 1));
        column0.put("B", Set.of(2, 3));
        column0.put("C", Set.of(4));
        columnValueToRowIds.put(0, column0);

        // Группировка столбца 1: Y связывает 1,2, а Z связывает 3,4.
        Map<String, Set<Integer>> column1 = new HashMap<>();
        column1.put("X", Set.of(0));
        column1.put("Y", Set.of(1, 2));
        column1.put("Z", Set.of(3, 4));
        columnValueToRowIds.put(1, column1);

        List<Set<String>> result = ColumnBasedRowGrouper.groupRows(rows, columnValueToRowIds);

        // Все строки должны быть транзитивно связаны: 0-1-2-3
        assertEquals(1, result.size());
        Set<String> group = result.get(0);
        assertEquals(5, group.size());
        assertTrue(group.contains("A;X"));
        assertTrue(group.contains("A;Y"));
        assertTrue(group.contains("B;Y"));
        assertTrue(group.contains("B;Z"));
        assertTrue(group.contains("C;Z"));
    }

    @Test
    @DisplayName("Должны обрабатываться пустые сопоставления столбцов")
    void testEmptyColumnMappings() {
        List<String[]> rows = Arrays.asList(
            new String[]{"A", "1"},
            new String[]{"B", "2"}
        );

        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds = new HashMap<>();

        List<Set<String>> result = ColumnBasedRowGrouper.groupRows(rows, columnValueToRowIds);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Должен сохраняться порядок вставки в группах с использованием LinkedHashSet")
    void testGroupPreservesOrder() {
        List<String[]> rows = Arrays.asList(
            new String[]{"A", "1"},
            new String[]{"A", "2"},
            new String[]{"A", "3"}
        );

        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds = new HashMap<>();
        Map<String, Set<Integer>> column0 = new HashMap<>();
        column0.put("A", Set.of(0, 1, 2));
        columnValueToRowIds.put(0, column0);

        List<Set<String>> result = ColumnBasedRowGrouper.groupRows(rows, columnValueToRowIds);

        assertEquals(1, result.size());
        Set<String> group = result.get(0);
        assertTrue(group instanceof LinkedHashSet);
        assertEquals(3, group.size());
    }
}

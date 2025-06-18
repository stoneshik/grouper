package grouper.dsu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ColumnBasedRowGrouper {
    private ColumnBasedRowGrouper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static List<Set<String>> groupRows(
        List<String[]> rows,
        Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds
    ) {
        DSU dsu = buildDSU(rows.size(), columnValueToRowIds);
        Map<Integer, Set<String>> rawGroups = collectGroups(rows, dsu);
        return filterAndSortGroups(rawGroups);
    }

    private static DSU buildDSU(int totalRows, Map<Integer, Map<String, Set<Integer>>> columnValueToRowIds) {
        DSU dsu = new DSU(totalRows);
        for (Map<String, Set<Integer>> valueToRows : columnValueToRowIds.values()) {
            for (Set<Integer> group : valueToRows.values()) {
                Integer[] ids = group.toArray(new Integer[0]);
                for (int i = 1; i < ids.length; i++) {
                    dsu.union(ids[0], ids[i]);
                }
            }
        }
        return dsu;
    }

    private static Map<Integer, Set<String>> collectGroups(List<String[]> rows, DSU dsu) {
        Map<Integer, Set<String>> groups = new HashMap<>();
        for (int i = 0; i < rows.size(); i++) {
            int root = dsu.find(i);
            groups
                .computeIfAbsent(root, k -> new LinkedHashSet<>())
                .add(String.join(";", rows.get(i)));
        }
        return groups;
    }

    private static List<Set<String>> filterAndSortGroups(Map<Integer, Set<String>> groups) {
        List<Set<String>> resultGroups = new ArrayList<>(groups.values());
        resultGroups.removeIf(group -> group.size() <= 1);
        resultGroups.sort((a, b) -> Integer.compare(b.size(), a.size()));
        return resultGroups;
    }
}

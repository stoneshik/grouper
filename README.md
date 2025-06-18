# Тестовое задание на java-разработчика от PeacockTeam

Текст выполняемого задания - https://github.com/PeacockTeam/new-job/blob/master/lng-java.md


## Запуск

Программа собрана в fat-jar grouper.jar

Команда запуска:

```
java -Xmx1G -jar grouper.jar lng.txt
```

### Результат запуска

Количество групп с более чем одним элементом: 471<br>
Время выполнения: 5193 мс

Полный вывод приведен в файле output.txt

Также написаны юнит тесты для логики парсинга вводимых данных и определения групп для строк

На данный момент не получилось добиться оптимизации по памяти, затрачиваемая память превышает 1 Гб.

При запуске ```java -Xmx1G -XX:+HeapDumpOnOutOfMemoryError -jar grouper.jar lng.txt```

Получаем:

```
java.lang.OutOfMemoryError: Java heap space
Dumping heap to java_pid173757.hprof ...
Heap dump file created [1765521821 bytes in 2,113 secs]
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
        at java.base/java.util.HashSet.<init>(HashSet.java:107)
        at grouper.parser.Parser.lambda$parseLineAndUpdateColumnValueToRowIds$3(Parser.java:59)
        at grouper.parser.Parser$$Lambda/0x000072b6880018c0.apply(Unknown Source)
        at java.base/java.util.HashMap.computeIfAbsent(HashMap.java:1228)
        at grouper.parser.Parser.parseLineAndUpdateColumnValueToRowIds(Parser.java:59)
        at grouper.parser.Parser.parseLineAndUpdateRowsAndColumnValueToRowIds(Parser.java:25)
        at grouper.parser.ParserFromBufferedReader.parse(ParserFromBufferedReader.java:26)
        at grouper.Main.groupStrings(Main.java:41)
        at grouper.Main.main(Main.java:27)
```

То есть размер затрачиваемой памяти примерно равен 1,64 Гб

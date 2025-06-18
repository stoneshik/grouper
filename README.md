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
Dumping heap to java_pid152340.hprof ...
Heap dump file created [1765245287 bytes in 4,173 secs]
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
        at java.base/java.lang.String.chars(String.java:4315)
        at grouper.parser.Parser.isInvalidLine(Parser.java:34)
        at grouper.parser.Parser.parseLineAndUpdateRowsAndColumnValueToRowIds(Parser.java:21)
        at grouper.parser.ParserFromBufferedReader.parse(ParserFromBufferedReader.java:26)
        at grouper.Main.groupStrings(Main.java:41)
        at grouper.Main.main(Main.java:27)
```

То есть размер затрачиваемой памяти примерно равен 1,64 Гб

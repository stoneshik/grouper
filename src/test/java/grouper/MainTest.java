package grouper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MainTest {
    @Test
    void testSum() {
        int a = 2;
        int b = 3;
        int result = a + b;
        assertEquals(5, result, "Сумма должна быть 5");
    }

    @Test
    void testNotNull() {
        String text = "hello";
        assertNotNull(text);
    }
}
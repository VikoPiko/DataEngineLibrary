package annotation;

import com.viko.validation.NotNullStrategy;
import com.viko.validation.RangeStrategy;
import com.viko.validation.RegexStrategy;
import org.junit.jupiter.api.Test;
import parser.MockClass;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnnotaionTest {
    @Test
    void shouldFailNotNullWhenValueIsNull() throws Exception {
        NotNullStrategy strategy = new NotNullStrategy();
        MockClass mock = new MockClass(); // name = null

        Field field = MockClass.class.getDeclaredField("name");

        Optional<String> result = strategy.validate(mock, field);

        assertTrue(result.isPresent());
        assertEquals("name is required", result.get());
    }

    @Test
    void shouldPassNotNullWhenValuePresent() throws Exception {
        NotNullStrategy strategy = new NotNullStrategy();
        MockClass mock = new MockClass();
        mock.name = "John";

        Field field = MockClass.class.getDeclaredField("name");

        Optional<String> result = strategy.validate(mock, field);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFailRegexWhenInvalid() throws Exception {
        RegexStrategy strategy = new RegexStrategy();
        MockClass mock = new MockClass();
        mock.email = "invalid";

        Field field = MockClass.class.getDeclaredField("email");

        Optional<String> result = strategy.validate(mock, field);

        assertTrue(result.isPresent());
        assertEquals("invalid email", result.get());
    }

    @Test
    void shouldPassRegexWhenValid() throws Exception {
        RegexStrategy strategy = new RegexStrategy();
        MockClass mock = new MockClass();
        mock.email = "john@mail.com";

        Field field = MockClass.class.getDeclaredField("email");

        assertTrue(strategy.validate(mock, field).isEmpty());
    }

    @Test
    void shouldFailRangeWhenOutOfBounds() throws Exception {
        RangeStrategy strategy = new RangeStrategy();
        MockClass mock = new MockClass();
        mock.age = 10;

        Field field = MockClass.class.getDeclaredField("age");

        Optional<String> result = strategy.validate(mock, field);

        assertTrue(result.isPresent());
        assertEquals("Out of range....", result.get());
    }
}

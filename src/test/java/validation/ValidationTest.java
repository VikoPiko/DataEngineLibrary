package validation;

import com.viko.annotations.NotNull;
import com.viko.parser.GenericParser;
import com.viko.validation.NotNullStrategy;
import com.viko.validation.Validator;
import org.junit.jupiter.api.Test;
import parser.MockClass;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationTest {

    @Test
    void shouldFailWithoutFileSource() {
        class NoFileSource {}

        GenericParser parser = new GenericParser();

        assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse("malformed.txt", NoFileSource.class,  GenericParser.ParsingMode.STRICT)
        );
    }

    @Test
    void shouldDetectValidationErrors() {
        MockClass mockClass = new MockClass();

        mockClass.email = "Invalidddd";
        mockClass.age = 11;

        Validator validator = new Validator();

        Map<MockClass, Set<String>> errors = validator.validate(List.of(mockClass));

        assertEquals(3, errors.get(mockClass).size());
    }
}

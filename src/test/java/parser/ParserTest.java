package parser;

import com.viko.exception.ParsingException;
import com.viko.parser.GenericParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {

    @Test
    public void shouldParseFile() throws Exception {
        Path file = Files.createTempFile("mock-data", "txt");
        Files.writeString(file, "Data,data@mail.com,25");

        GenericParser parser = new GenericParser();

        List<MockClass> result = parser.parse(file.toString(), MockClass.class);

        assertEquals(1, result.size());
        assertEquals("Data", result.get(0).name);
    }

    @Test
    void shouldParseLine() throws Exception {
        GenericParser parser = new GenericParser();

        MockClass mockClass = parser.parseLine("Data,data@mail.com,25", MockClass.class,",");

        assertEquals("Data", mockClass.name);
        assertEquals("data@mail.com", mockClass.email);
        assertEquals(25, mockClass.age);
    }

    @Test
    void shouldThrowParsingExceptionOnInvalidNumber() throws Exception {
        Path file = Files.createTempFile("customers", ".txt");
        Files.writeString(file, "John,john@mail.com,NOT_A_NUMBER");

        GenericParser parser = new GenericParser();

        assertThrows(
                ParsingException.class,
                () -> parser.parse(file.toString(), MockClass.class)
        );
    }
}

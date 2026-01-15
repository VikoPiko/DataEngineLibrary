package com.viko.parser;

import com.viko.annotations.Column;
import com.viko.annotations.FileSource;
import com.viko.exception.GlobalErrorHandler;
import com.viko.exception.ParsingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class GenericParser {

    public enum ParsingMode {
        STRICT,
        LENIENT
    }
    /**
     * Func parse: Parses through a given file, based on passed class type.
     * @param filePath Specifies the path to the file
     * @param clazz Reference to the class to be parsed.
     * @return Returns results from successfully parsed entries which satisfy defined rules.
     * @param <T> Generic type allowing for work with any class/type.
     * @throws IOException Exception when an error occurs during reading or writing to a file.
     * @throws ReflectiveOperationException Exception when Reflection Api encounters an issue.
     * Double check for RetentionPolicy. To use the Reflection Api RUNTIME is recommended.
     */
    public <T> List<T> parse(String filePath, Class<T> clazz, ParsingMode mode)
            throws IOException, ReflectiveOperationException {

        FileSource fileSource = clazz.getAnnotation(FileSource.class);
        if (fileSource == null) {
            throw new IllegalArgumentException("Missing @FileSource annotation");
        }

        String delimiter = fileSource.delimiter();
        String[] commentPrefixes = fileSource.commentPrefixes();
        List<T> result = new ArrayList<>();

        //Uses Stream (Buffer would also work here) to optimize code during runtime.
        //Parses the file line by line, instead of loading it all into memory with Files.readAllLines()
        //Which can really thank the load of the application and its resources leading to unexpected/unwanted behavior
//        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
//            lines.filter(line -> line != null && !line.trim().isEmpty())
//                .forEach(line -> {
//                try{
//                    T obj = parseLine(line.trim(), clazz, delimiter);
//                    result.add(obj);
//                } catch (ReflectiveOperationException e) {
//                    throw new ParsingException("Failed parsing line: "+ line, e);
//                }
//            });
//        }
        //Changed to bufferReader for more flexibility, retaining speed and control.
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            //split into lines
            String line;
            //start from line 0
            int lineNumber = 0;
            //while valid lines ->
            while ((line = reader.readLine()) != null) {
                //added line numbering counter
                lineNumber++;
                //skip \n empty lines
                if (line.isBlank()) continue;
                //skip comments
                if(isComment(line, commentPrefixes)) continue;
                try {
                    result.add(parseLine(line.trim(), clazz, delimiter));
                } catch (ReflectiveOperationException | ParsingException e) {
                    //log where error occurred

                    GlobalErrorHandler.log("Parsing failed at line " + lineNumber + ": " + line, e);
                    if(mode == ParsingMode.STRICT) {
                        throw new ParsingException("Parsing failed at line " + lineNumber + ": " + line, e);
                    }
                }
            }
        }
        return result;
    }
    /**
     * isComment is a function that checks if the line begins with one of the following characters
     * if it does, it skips the entire line.
     * @param line --> Current line being iterated
     * @return boolean value (true or false) acting as a flag to show if current line is a comment
     */
    private boolean isComment(String line, String[] prefixes) {
        String trimmed = line.trim();
        for (String prefix : prefixes) {
            if (trimmed.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Func convert: Takes in a value as an input and a Class type and returns the value in the correct type format.
     * @param value Value to be converted
     * @param type Class instance using that value/field
     * @return Returns the input value parsed to the correct data type.
     */
    private Object convert(String value, Class<?> type) {
        if (value == null || value.isBlank()) return null;
        try {
            if (type == String.class) return value;
            if (type == int.class || type == Integer.class) return Integer.parseInt(value);
            if (type == double.class || type == Double.class) return Double.parseDouble(value);
            if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(value);
            if (type == LocalDate.class) return LocalDate.parse(value);
        } catch (Exception e) {
            GlobalErrorHandler.log("Invalid value format found. Error: ", e);
            throw new ParsingException("Invalid value '" + value + "' for type " + type.getSimpleName(), e);
        }
        throw new ParsingException("Unsupported field type: " + type.getName());
    }
    /**
     * Func parseLine: Takes current line from input file, and returns a Java object of type T
     * @param line Current line passed from the input file
     * @param clazz Runtime class for parser
     * @param delimiter Character / Token to be used for splitting up the fields.
     * @return Java object of type T assigned to given class field.
     * @param <T> Generic type allowing for generic class support
     * @throws ReflectiveOperationException On failure during Runtime reflection and accessing class data.
     * RetentionType should be RUNTIME.
     */
    public <T> T parseLine(String line, Class<T> clazz, String delimiter)
            throws ReflectiveOperationException {

        String[] tokens = line.split("\\Q" + delimiter + "\\E");
        //Creates anew object of type T (class instance)
        T instance = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            //If a field doesnt have mapping/annotation -> ignore it.
            Column column = field.getAnnotation(Column.class);
            if (column == null) continue;

            int index = column.index();

            if (index >= tokens.length) {
                throw new ParsingException(
                        "Column index " + index +
                                " out of bounds for field '" + field.getName() +
                                "'. Expected at least " + (index + 1) +
                                " columns but found " + tokens.length +
                                ". Line: " + line
                );
            }

            field.setAccessible(true);

            try {
                //trim value so values like "  20 " work and get parsed coprrectly
                String rawValue = tokens[index].trim();
                Object converted = convert(rawValue, field.getType());
                field.set(instance, converted);
            } catch (Exception e) {
                throw new ParsingException(
                        "Failed parsing field '" + field.getName() +
                                "' at column index " + index +
                                " in line: " + line, e);
            }
        }
        return instance;
    }
}


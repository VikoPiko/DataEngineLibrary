package com.viko.parser;

import com.viko.annotations.Column;
import com.viko.annotations.FileSource;
import com.viko.exception.ParsingException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public class GenericParser {


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
    public <T> List<T> parse(String filePath, Class<T> clazz)
            throws IOException, ReflectiveOperationException {

        FileSource fileSource = clazz.getAnnotation(FileSource.class);
        if (fileSource == null) {
            throw new IllegalArgumentException("Missing @FileSource annotation");
        }

        String delimiter = fileSource.delimiter();
        List<T> result = new ArrayList<>();

        //Uses Stream (Buffer would also work here) to optimize code during runtime.
        //Parses the file line by line, instead of loading it all into memory with Files.readAllLines()
        //Which can really thank the load of the application and its resources leading to unexpected/unwanted behavior
        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            lines.filter(line -> line != null && !line.trim().isEmpty())
                .forEach(line -> {
                try{
                    T obj = parseLine(line.trim(), clazz, delimiter);
                    result.add(obj);
                } catch (ReflectiveOperationException e) {
                    throw new ParsingException("Failed parsing line: "+ line, e);
                }
            });
        }
        return result;
    }

    /**
     * Func convert: Takes in a value as an input and a Class type and returns the value in the correct type format.
     * @param value Value to be converted
     * @param type Class instance using that value/field
     * @return Returns the input value parsed to the correct data type.
     */
    private Object convert(String value, Class<?> type) {
        if (value == null || value.isEmpty()) return null;

        if (type.equals(String.class)) return value;
        if (type.equals(int.class) || type.equals(Integer.class))
            return Integer.parseInt(value);
        if (type.equals(double.class) || type.equals(Double.class))
            return Double.parseDouble(value);
        if (type.equals(boolean.class) || type.equals(Boolean.class))
            return Boolean.parseBoolean(value);
        if (type.equals(LocalDate.class))
            return LocalDate.parse(value);

        throw new IllegalArgumentException("Unsupported type: " + type);
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
        //Creates anew objhect of type T (class instance)
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
                String rawValue = tokens[index];
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


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

    public <T> List<T> parse(String filePath, Class<T> clazz)
            throws IOException, ReflectiveOperationException {

        FileSource fileSource = clazz.getAnnotation(FileSource.class);
        if (fileSource == null) {
            throw new IllegalArgumentException("Missing @FileSource annotation");
        }

        String delimiter = fileSource.delimiter();
        List<T> result = new ArrayList<>();

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

    private <T> T parseLine(String line, Class<T> clazz, String delimiter)
            throws ReflectiveOperationException {

        String[] tokens = line.split("\\Q" + delimiter + "\\E");
        T instance = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
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


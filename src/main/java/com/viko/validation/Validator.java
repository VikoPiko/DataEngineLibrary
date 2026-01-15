package com.viko.validation;

import com.viko.annotations.NotNull;
import com.viko.annotations.Range;
import com.viko.annotations.Regex;

import com.viko.exception.GlobalErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Validation Class:
 * Implements all defined strategies and loads them into a map.
 */

public class Validator {

    public static Logger logger = LogManager.getLogger(NotNullStrategy.class);

    private final Map<Class<? extends Annotation>, ValidationStrategy> strategies = Map.of(
            NotNull.class, new NotNullStrategy(),
            Regex.class, new RegexStrategy(),
            Range.class, new RangeStrategy()
    );

    /**
     * Func validate: Validates all fields of passed objects based on the annotations they have.
     * @param objects Input List of type T objects to validate.
     * @return errors: Returns errors object if any errors are present.
     * @param <T> Generic Template type, allowing for a wide range of input types.
     */

    public <T> Map<T, Set<String>> validate(List<T> objects) {
        Map<T, Set<String>> errors = new HashMap<>();

        for (T obj : objects) {
            Set<String> messages = new HashSet<>();
            logger.info("mapping through object: {}", obj);
            for (Field field : obj.getClass().getDeclaredFields()) {
                for (var entry : strategies.entrySet()) {
                    if (field.isAnnotationPresent(entry.getKey())) {
                        entry.getValue()
                                .validate(obj, field)
                                .ifPresent(messages::add);
                    }
                }
            }
            if (!messages.isEmpty()) {
                GlobalErrorHandler.log("Errors found during validation");
                errors.put(obj, messages);
            }
        }
        return errors;
    }
}

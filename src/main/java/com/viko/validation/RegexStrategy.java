package com.viko.validation;

import com.viko.annotations.Regex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.regex.Pattern;

public class RegexStrategy implements ValidationStrategy{

    public static Logger logger = LogManager.getLogger(NotNullStrategy.class);
    /**
     * Func validate
     * @param obj Object type
     * @param field Field to check
     * @return Returns potential error messages.
     */
    @Override
    public Optional<String> validate(Object obj, Field field){
        try{
            logger.info("validating Regex for field:", field.getName());
            field.setAccessible(true);
            Object value = field.get(obj);
            Regex annotation = field.getAnnotation(Regex.class);

            if (value == null) {
                return Optional.empty();
            }

            if (!Pattern.matches(annotation.pattern(), value.toString())) {
                return Optional.of(annotation.message());
            }
        }catch (IllegalAccessException e){
            System.err.println("Field is not accessible: " + e);
        }
        return Optional.empty();
    }
}

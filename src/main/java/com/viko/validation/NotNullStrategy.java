package com.viko.validation;

import com.viko.annotations.NotNull;
import com.viko.exception.GlobalErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * Class NotNullStrategy: Implementation of a NotNull check
 */
public class NotNullStrategy implements ValidationStrategy {
    public static Logger logger = LogManager.getLogger(NotNullStrategy.class);
    /**
     * Func validate
     * @param obj Object type
     * @param field Field to check
     * @return Returns potential error messages.
     */
    @Override
    public Optional<String> validate(Object obj, Field field) {
        //Surrounded with try catch for proper error catch & handle
        try{
            logger.info("validating NotNull for field:", field);
            field.setAccessible(true);
            Object value = field.get(obj);
            NotNull annotation = field.getAnnotation(NotNull.class);

            if(value == null) return Optional.of(annotation.message());

        }catch (IllegalAccessException e){
            GlobalErrorHandler.log("Error occurred validating Field: ", e);
                System.err.println("Field is not accessible");
                return Optional.of("Field cannot be empty! Error:" + e);
        }
        return Optional.empty();
    }

    /*
    Basically can also do with:
    if(value == null) -> early return, since no data -> do nothing
    Annotation annName = (Annotation) annotation;
    return value == null ? Optional.of(annName.message()) : else Optional.empty(); -> No errors
    * */
}

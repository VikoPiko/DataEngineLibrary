package com.viko.validation;

import com.viko.annotations.Range;
import com.viko.exception.GlobalErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Optional;

public class RangeStrategy implements ValidationStrategy{
    public static Logger logger = LogManager.getLogger(NotNullStrategy.class);
    /**
     * Func validate
     * @param obj Object type
     * @param field Field to check
     * @return Returns potential error messages.
     */
    @Override
    public Optional<String> validate(Object obj, Field field) {
        try{
            logger.info("validating Range for field:", field);
            field.setAccessible(true);
            Object value = field.get(obj);
            Range annotation = field.getAnnotation(Range.class);

            if(value instanceof Number num){
                double v = num.doubleValue();
                if(v < annotation.min() || v > annotation.max()){
                    return Optional.of(annotation.message());
                }
            }
        }catch (IllegalAccessException e){
            GlobalErrorHandler.log("Error occurred validating Range: ", e);
            System.err.println("Field is not accessible");
        }
        return Optional.empty();
    }
}

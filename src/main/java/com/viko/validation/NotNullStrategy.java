package com.viko.validation;

import com.viko.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Optional;

public class NotNullStrategy implements ValidationStrategy {
    @Override
    public Optional<String> validate(Object obj, Field field) {
        try{
            field.setAccessible(true);
            Object value = field.get(obj);
            NotNull annotation = field.getAnnotation(NotNull.class);

            if(value == null) return Optional.of(annotation.message());

        }catch (IllegalAccessException ignored){
                System.err.println("Field is not accessible");
        }
        return Optional.empty();
    }
}

package com.viko.validation;

import java.lang.reflect.Field;
import java.util.Optional;

public interface ValidationStrategy {
    Optional<String> validate(Object obj, Field field);
}

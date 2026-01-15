package com.viko.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Range {
    public static final double MAX = 999999999.00;
    double min();
    double max() default MAX;
    String message() default "Out of range!";
}

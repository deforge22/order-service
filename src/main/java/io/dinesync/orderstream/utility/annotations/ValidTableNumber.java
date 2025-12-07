package io.dinesync.orderstream.utility.annotations;

import io.dinesync.orderstream.utility.validator.TableNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TableNumberValidator.class)
public @interface ValidTableNumber {

    int min() default 1;
    int max() default 119;
    String message() default "Table number must be between 1 and 120";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}

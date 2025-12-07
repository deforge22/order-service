package io.dinesync.orderstream.utility.annotations;

import io.dinesync.orderstream.utility.validator.QuantityValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = QuantityValidator.class)
public @interface ValidQuantity {

    String min() default "0";
    String max() default "10000";
    String message() default "Quantity value must be between 0 and 10000";
    boolean allowFractional() default true;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}

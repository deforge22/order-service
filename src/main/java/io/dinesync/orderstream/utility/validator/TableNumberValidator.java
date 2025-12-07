package io.dinesync.orderstream.utility.validator;

import io.dinesync.orderstream.rest.model.request.OrderRequest;
import io.dinesync.orderstream.utility.annotations.ValidTableNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class TableNumberValidator implements ConstraintValidator<ValidTableNumber, Integer> {

    private int min;
    private int max;

    @Override
    public void initialize(ValidTableNumber annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return true;
        }
        boolean valid = value >= min && value <= max;

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("Table number must be between %d and %d, but got: %d", min, max, value)
            ).addConstraintViolation();
        }

        return valid;
    }
}

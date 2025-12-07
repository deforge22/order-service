package io.dinesync.orderstream.utility.validator;

import io.dinesync.orderstream.utility.annotations.ValidQuantity;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.Objects;

public class QuantityValidator implements ConstraintValidator<ValidQuantity, BigDecimal> {

    private BigDecimal min;
    private BigDecimal max;
    private boolean allowFractional;

    @Override
    public void initialize(ValidQuantity constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.min = new BigDecimal(constraintAnnotation.min());
        this.max = new BigDecimal(constraintAnnotation.max());
        this.allowFractional = constraintAnnotation.allowFractional();

    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return true;
        } else {
            if (!allowFractional && value.stripTrailingZeros().scale() > 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Fractional quantities are not allowed. Please provide a whole number."
                ).addConstraintViolation();
                return false;
            }

            if (value.compareTo(this.min) <= 0 || value.compareTo(this.max) >= 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        String.format("Value must be between %s and %s. Value provided is: %s",
                                this.min, this.max, value)
                ).addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}

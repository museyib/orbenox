package com.orbenox.erp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.math.BigDecimal;

/**
 * Custom validator for price fields - must be positive and non-null
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PositivePrice.PositivePriceValidator.class)
@Documented
public @interface PositivePrice {

    String message() default "{price.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean allowZero() default false;

    class PositivePriceValidator implements ConstraintValidator<PositivePrice, BigDecimal> {
        private boolean allowZero;

        @Override
        public void initialize(PositivePrice constraintAnnotation) {
            this.allowZero = constraintAnnotation.allowZero();
        }

        @Override
        public boolean isValid(BigDecimal price, ConstraintValidatorContext context) {
            if (price == null) {
                return false;
            }

            if (allowZero) {
                return price.compareTo(BigDecimal.ZERO) >= 0;
            } else {
                return price.compareTo(BigDecimal.ZERO) > 0;
            }
        }
    }
}

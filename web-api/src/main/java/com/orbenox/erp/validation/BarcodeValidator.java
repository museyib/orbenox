package com.orbenox.erp.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validator for barcode format
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BarcodeValidator.BarcodeValidatorImpl.class)
@Documented
public @interface BarcodeValidator {

    String message() default "{barcode.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class BarcodeValidatorImpl implements ConstraintValidator<BarcodeValidator, String> {

        @Override
        public boolean isValid(String barcode, ConstraintValidatorContext context) {
            if (barcode == null || barcode.isBlank()) {
                return true; // Use @NotBlank for null/empty checks
            }

            // Basic barcode validation: alphanumeric, min 3 chars, max 50 chars
            return barcode.matches("^[A-Za-z0-9-]+$") && barcode.length() >= 3 && barcode.length() <= 50;
        }
    }
}

package com.cts.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates password complexity (Story 10).
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    // >=8 chars, >=1 lowercase, >=1 uppercase, >=1 digit, >=1 special
    private static final Pattern PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null is handled by @NotBlank separately; treat null as "no opinion here"
        return value == null || PATTERN.matcher(value).matches();
    }
}
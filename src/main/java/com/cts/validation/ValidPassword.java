package com.cts.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Story 10: password complexity rule.
 * Min 8 chars, with upper, lower, digit, and special character.
 */
@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface ValidPassword {
    String message() default "Password must be at least 8 characters and include uppercase, lowercase, a digit, and a special character";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
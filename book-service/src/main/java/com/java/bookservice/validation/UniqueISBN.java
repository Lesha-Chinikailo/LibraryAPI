package com.java.bookservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ISBNValidator.class)
public @interface UniqueISBN {
    String message() default "ISBN of the book already exists in the database";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

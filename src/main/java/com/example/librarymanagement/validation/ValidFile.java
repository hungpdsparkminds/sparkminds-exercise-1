package com.example.librarymanagement.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {FileValidator.class})
public @interface ValidFile {
    String message() default "File type are not allowed";
    String[] fileTypeAllow() default {"image/png", "image/jpg", "image/jpeg"};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
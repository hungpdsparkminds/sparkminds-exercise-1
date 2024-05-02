package com.example.librarymanagement.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import org.thymeleaf.util.StringUtils;

public class PasswordMatchValidator implements ConstraintValidator<ValidPasswordValueMatch, Object> {

    private String oldField;
    private String field;
    private String fieldMatch;
    private String message;

    public void initialize(ValidPasswordValueMatch constraintAnnotation) {
        this.oldField = constraintAnnotation.oldField();
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
        this.message = constraintAnnotation.message();
    }

    public boolean isValid(Object value,
                           ConstraintValidatorContext context) {

        Object fieldValue = new BeanWrapperImpl(value)
                .getPropertyValue(field);
        Object fieldMatchValue = new BeanWrapperImpl(value)
                .getPropertyValue(fieldMatch);
        Object oldFieldValue = null;
        boolean isNewValueNotMatchOldValue = true;
        boolean isNotMatch = !StringUtils.equals(fieldMatchValue, fieldValue);
        if (!oldField.equals("")) {
            oldFieldValue = new BeanWrapperImpl(value)
                    .getPropertyValue(oldField);
            isNewValueNotMatchOldValue = !oldField.equals("")
                    && !StringUtils.equals(oldFieldValue, fieldValue);

        }


        if (isNotMatch) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(field)
                    .addConstraintViolation();

            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(fieldMatch)
                    .addConstraintViolation();
        }

        if (!isNewValueNotMatchOldValue) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("NEW Password must be different from OLD Password.")
                    .addPropertyNode(field)
                    .addConstraintViolation();
        }

        return !isNotMatch && isNewValueNotMatchOldValue;

    }

}
package com.example.librarymanagement.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
    private String[] contentTypeAllows;
    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.contentTypeAllows = constraintAnnotation.fileTypeAllow();
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {

        boolean result = true;

        String contentType = multipartFile.getContentType();
        if (!isSupportedContentType(contentType)) {
            result = false;
        }

        return result;
    }

    private boolean isSupportedContentType(String contentType) {
        return Arrays.asList(contentTypeAllows).contains(contentType);
    }
}
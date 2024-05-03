package com.example.librarymanagement.config;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.example.librarymanagement.model.exception.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

import static com.example.librarymanagement.utils.Constants.ERROR_CODE.ERROR_LOG_FORMAT;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {
    //400
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorVm> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ErrorVm errorVm = new ErrorVm(HttpStatus.BAD_REQUEST.toString(), "Validation exception", message);
        log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), HttpStatus.BAD_REQUEST, message);
        log.debug(ex.toString());
        return ResponseEntity.badRequest().body(errorVm);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorVm> handleValidationException(Exception ex, WebRequest request) {
        String message = ex.getMessage();
        ErrorVm errorVm = new ErrorVm(HttpStatus.BAD_REQUEST.toString(), "Validation exception", message);
        log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), HttpStatus.BAD_REQUEST, message);
        log.debug(ex.toString());
        return ResponseEntity.badRequest().body(errorVm);
    }

    //401
    @ExceptionHandler({BadCredentialsException.class,
            VerificationException.class})
    public <T extends RuntimeException> ResponseEntity<ErrorVm> jwtExceptionHandler(T ex, WebRequest request) {
        String message = ex.getMessage();
        ErrorVm errorVm = new ErrorVm(HttpStatus.UNAUTHORIZED.toString(), "Authentication exception", message);
        log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), 401, message);
        log.debug(ex.toString());
        return ResponseEntity.badRequest().body(errorVm);
    }

    //403
    @ExceptionHandler({AccessDeniedException.class})
    public <T extends RuntimeException> ResponseEntity<ErrorVm> accessDeniedExceptionHandler(T ex, WebRequest request) {
        String message = ex.getMessage();
        ErrorVm errorVm = new ErrorVm(HttpStatus.FORBIDDEN.toString(), "Access Denied exception", message);
        log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), HttpStatus.FORBIDDEN, message);
        log.debug(ex.toString());
        return ResponseEntity.badRequest().body(errorVm);
    }

    //404
    @ExceptionHandler({
            ResourceNotFoundException.class,
            NotFoundException.class,
            EntityNotFoundException.class
    })
    public <T extends RuntimeException> ResponseEntity<ErrorVm> notFoundExceptionHandler(T ex, WebRequest request) {
        String message = ex.getMessage();
        ErrorVm errorVm = new ErrorVm(HttpStatus.NOT_FOUND.toString(), "Not Found exception", message);
        log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), HttpStatus.NOT_FOUND, message);
        log.debug(ex.toString());
        return ResponseEntity.badRequest().body(errorVm);
    }

    //409
    @ExceptionHandler({
            DataIntegrityViolationException.class
    })
    public <T extends RuntimeException> ResponseEntity<ErrorVm> conflictExceptionHandler(T ex, WebRequest request) {
        String message = ex.getMessage();
        ErrorVm errorVm = new ErrorVm(HttpStatus.CONFLICT.toString(), "Conflict exception", message);
        log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), HttpStatus.CONFLICT, message);
        log.debug(ex.toString());
        return ResponseEntity.badRequest().body(errorVm);
    }

    //500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorVm> globalExceptionHandler(Exception ex, WebRequest request) {
        String message = ex.getMessage();
        ErrorVm errorVm = new ErrorVm(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Internal server error", message);
        log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), HttpStatus.INTERNAL_SERVER_ERROR, message);
        log.debug(ex.toString());
        return ResponseEntity.badRequest().body(errorVm);
    }

    //503
    @ExceptionHandler({
            AmazonServiceException.class,
            SdkClientException.class,
            AmazonClientException.class,
    })
    public ResponseEntity<ErrorVm> awsExceptionHandler(Exception ex, WebRequest request) {
        String message = ex.getMessage();
        ErrorVm errorVm = new ErrorVm(HttpStatus.SERVICE_UNAVAILABLE.toString(), "AWS Exception.", message);
        log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), HttpStatus.SERVICE_UNAVAILABLE, message);
        log.debug(ex.toString());
        return ResponseEntity.badRequest().body(errorVm);
    }

    private String getServletPath(WebRequest webRequest) {
        ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
        return servletRequest.getRequest().getServletPath();
    }
}

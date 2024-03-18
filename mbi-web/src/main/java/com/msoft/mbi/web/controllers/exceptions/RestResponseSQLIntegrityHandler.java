package com.msoft.mbi.web.controllers.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class RestResponseSQLIntegrityHandler extends ResponseEntityExceptionHandler {

    /*@ExceptionHandler(value= { SQLIntegrityConstraintViolationException.class })
    protected ResponseEntity<Object> sqlIntegrityConstraintViolation(
            RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getCause().getMessage();
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }*/
}

package com.emanueldev.sample_shop.handler;

import com.emanueldev.sample_shop.exceptions.ApplicationException;
import com.emanueldev.sample_shop.exceptions.HttpBadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpBadRequestException.class)
    public ResponseEntity<ApplicationException> handleHttpBadRequestException(HttpServletRequest request, HttpBadRequestException exception) {
        ApplicationException response = new ApplicationException(
                request,
                HttpStatus.BAD_REQUEST,
                exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApplicationException> methodArgumentNotValidException(
            HttpServletRequest request,
            MethodArgumentNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new ApplicationException(
                                request,
                                HttpStatus.UNPROCESSABLE_ENTITY,
                                "Validation error",
                                ex.getBindingResult()
                        )
                );
    }
}

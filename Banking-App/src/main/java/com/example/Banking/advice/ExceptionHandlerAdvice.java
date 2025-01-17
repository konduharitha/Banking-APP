package com.example.Banking.advice;

import com.example.Banking.exception.AccountNotFoundException;
import com.example.Banking.exception.InsufficientBalanceException;
import com.example.Banking.model.ResponseStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseStatus> handleGenericException(Exception ex){
        ResponseStatus response = ResponseStatus.builder()
                .isSuccess(Boolean.FALSE)
                .message("Something went wrong in the server")
                .detailMessage(ex.toString())
                .build();

        return ResponseEntity.internalServerError()
                .body(response);
    }


    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ResponseStatus> handleInsufficientBalance(InsufficientBalanceException ex){
        ResponseStatus response = ResponseStatus.builder()
                .isSuccess(Boolean.FALSE)
                .message(ex.getMessage())
                .build();

        return ResponseEntity.badRequest()
                .body(response);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ResponseStatus> accountNotFound(AccountNotFoundException ex){
        ResponseStatus response = ResponseStatus.builder()
                .isSuccess(Boolean.FALSE)
                .message(ex.getMessage())
                .build();

        return ResponseEntity.badRequest()
                .body(response);
    }
}

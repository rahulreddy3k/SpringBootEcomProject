package com.ecommerce.project.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // special v of controller advice. global exception interceptor
 // used to define methods in
public class MyGlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String,String> response = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(objectError -> {
            String fieldName = ((FieldError)objectError).getField();
            String message = objectError.getDefaultMessage();
            response.put(fieldName,message);
        });
        return new ResponseEntity<Map<String,String>>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> myResourceNotFoundException(ResourceNotFoundException e){
        String message = e.getMessage();
        return new ResponseEntity<String>(message,HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<String> myAPIException(APIException e){
        String message = e.getMessage();
        return new ResponseEntity<String>(message,HttpStatus.BAD_REQUEST);

    }


}

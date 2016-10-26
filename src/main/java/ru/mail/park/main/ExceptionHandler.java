package ru.mail.park.main;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(DataAccessException.class)
    public ResponseEntity  handleDataAccessException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{}");
    }
}

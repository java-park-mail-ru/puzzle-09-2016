package ru.mail.park.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @org.springframework.web.bind.annotation.ExceptionHandler(DataAccessException.class)
    public ResponseEntity  handleDataAccessException(DataAccessException e) {
        logger.error("Exception: ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{}");
    }
}

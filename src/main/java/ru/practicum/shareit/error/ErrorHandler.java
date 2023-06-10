package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.error.model.EmailDuplicatedFound;
import ru.practicum.shareit.error.model.EntityNotFoundException;
import ru.practicum.shareit.error.model.ErrorResponse;

import java.security.InvalidParameterException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(EntityNotFoundException e) {
        log.error("получен статус 404 Not found {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle(EmailDuplicatedFound e) {
        log.error("получен статус 409 Conflict, duplicated email={}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ErrorResponse handleIncorrectParameterException(InvalidParameterException e) {
//        log.debug("Получен статус 400 Bad request {}", e.getMessage(), e);
//        return new ErrorResponse(
//                String.format("Ошибка с полем \"%s\".", e.getMessage())
//        );
//    }
}
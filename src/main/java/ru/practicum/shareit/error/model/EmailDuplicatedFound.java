package ru.practicum.shareit.error.model;

public class EmailDuplicatedFound extends RuntimeException {
    public EmailDuplicatedFound(String message) {
        super(message);
    }
}

package ru.practicum.shareit.error.model;

public class OwnerShipConflictException extends RuntimeException {
    public OwnerShipConflictException(String message) {
        super(message);
    }
}

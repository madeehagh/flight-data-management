package com.flight.data.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class FlightValidationException extends RuntimeException {
    private final List<String> errors;

    public FlightValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }
}

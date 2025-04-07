package com.flight.data.mgmt.exception;

import com.flight.data.mgmt.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FlightExceptionHandler {

    @ExceptionHandler(FlightValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleFlightValidationException(
            FlightValidationException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "Validation Error",
                ex.getErrors()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}

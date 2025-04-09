package com.flight.data.mgmt.exception;

import com.flight.data.mgmt.dto.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.nio.channels.UnresolvedAddressException;
import java.util.Collections;

@RestControllerAdvice
public class FlightExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(FlightExceptionHandler.class);

    @ExceptionHandler(FlightValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> handleFlightValidationException(
            FlightValidationException e) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "Validation Error",
                e.getErrors()
        );

        log.error(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler({UnresolvedAddressException.class, IOException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ErrorResponseDTO> handleNetworkException(Exception e) {

        log.error(e.getMessage(), e);

        ErrorResponseDTO error = new ErrorResponseDTO(
                "Service Temporarily Unavailable",
                Collections.singletonList("Unable to connect to flight supplier service. Please try again later.")
        );
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception e) {

        log.error(e.getMessage(), e);
        ErrorResponseDTO error = new ErrorResponseDTO(
                "Internal Server Error",
                Collections.singletonList("An unexpected error occurred. Please try again later.")
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}

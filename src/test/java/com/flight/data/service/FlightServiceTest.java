package com.flight.data.service;

import com.flight.data.exception.FlightValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class FlightServiceTest {
    private FlightService flightService;

    @BeforeEach
    public void setUp() {
        flightService = new FlightService();
    }

    @Test
    @DisplayName("Should accept valid search parameters without throwing exception")
    void validateSearchParam_ValidParameters(){
        String departureAirport = "JFK";
        String destinationAirport = "LAX";
        Instant now = Instant.now();
        Instant departureTimeStart = now.plusSeconds(86400);    // plus 1 day
        Instant departureTimeEnd = now.plusSeconds(172800);     // plus 2 days
        Instant arrivalTimeStart = departureTimeStart.plusSeconds(7200);  // plus 2 hours
        Instant arrivalTimeEnd = departureTimeEnd.plusSeconds(7200);      // plus 2 hours

        assertDoesNotThrow(() -> flightService.validateSearchParam(
                departureAirport, destinationAirport,
                departureTimeStart, departureTimeEnd,
                arrivalTimeStart, arrivalTimeEnd
        ));
    }

    @Test
    @DisplayName("Should accept valid search parameters when arrival time is not given")
    void validateSearchParam_WithoutArrivalTime(){
        String departureAirport = "JFK";
        String destinationAirport = "LAX";
        Instant now = Instant.now();
        Instant departureTimeStart = now.plusSeconds(86400);    // plus 1 day
        Instant departureTimeEnd = now.plusSeconds(172800);     // plus 2 days

        assertDoesNotThrow(() -> flightService.validateSearchParam(
                departureAirport, destinationAirport,
                departureTimeStart, departureTimeEnd,
                null, null
        ));
    }

    @Test
    @DisplayName("Should throw exception when arrival time is before departure time")
    void validateSearchParam_ArrivalBeforeDeparture_ThrowsException() {
        // Given
        String departureAirport = "JFK";
        String destinationAirport = "LAX";
        Instant now = Instant.now();
        Instant departureTimeStart = now.plusSeconds(172800);   // plus 2 days
        Instant departureTimeEnd = now.plusSeconds(259200);     // plus 3 days
        Instant arrivalTimeStart = now.plusSeconds(86400);      // plus 1 day
        Instant arrivalTimeEnd = now.plusSeconds(345600);       // plus 4 days

        FlightValidationException exception = assertThrows(
                FlightValidationException.class,
                () -> flightService.validateSearchParam(
                        departureAirport, destinationAirport,
                        departureTimeStart, departureTimeEnd,
                        arrivalTimeStart, arrivalTimeEnd
                )
        );
        assertTrue(exception.getErrors().contains("Arrival time cannot be before departure time"));
    }
}

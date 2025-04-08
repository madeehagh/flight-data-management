package com.flight.data.mgmt.service;

import com.flight.data.mgmt.dto.FlightSearchCriteria;
import com.flight.data.mgmt.exception.FlightValidationException;
import com.flight.data.mgmt.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private CrazySupplierService crazySupplierService;

    @InjectMocks
    private FlightService flightService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should accept valid search parameters without throwing exception")
    void validateSearchParamValidParameters() {
        String departureAirport = "JFK";
        String destinationAirport = "LAX";
        Instant now = Instant.now();
        Instant departureTimeStart = now.plusSeconds(86400);    // plus 1 day
        Instant departureTimeEnd = now.plusSeconds(172800);     // plus 2 days
        Instant arrivalTimeStart = departureTimeStart.plusSeconds(7200);  // plus 2 hours
        Instant arrivalTimeEnd = departureTimeEnd.plusSeconds(7200);      // plus 2 hours

        assertDoesNotThrow(() -> flightService.validateSearchParam(
                FlightSearchCriteria.builder().departureAirport(departureAirport)
                        .destinationAirport(destinationAirport)
                        .departureTimeStart(departureTimeStart)
                        .departureTimeEnd(departureTimeEnd)
                        .arrivalTimeStart(arrivalTimeStart)
                        .arrivalTimeEnd(arrivalTimeEnd)
                        .build()
        ));
    }

    @Test
    @DisplayName("Should accept valid search parameters when arrival time is not given")
    void validateSearchParamWithoutArrivalTime() {
        String departureAirport = "JFK";
        String destinationAirport = "LAX";
        Instant now = Instant.now();
        Instant departureTimeStart = now.plusSeconds(86400);    // plus 1 day
        Instant departureTimeEnd = now.plusSeconds(172800);     // plus 2 days

        assertDoesNotThrow(() -> flightService.validateSearchParam(
                FlightSearchCriteria.builder().departureAirport(departureAirport)
                        .destinationAirport(destinationAirport)
                        .departureTimeStart(departureTimeStart)
                        .departureTimeEnd(departureTimeEnd)
                        .arrivalTimeStart(null)
                        .arrivalTimeEnd(null)
                        .build()
        ));
    }

    @Test
    @DisplayName("Should throw exception when arrival time is before departure time")
    void validateSearchParamArrivalBeforeDeparture_ThrowsException() {
        // Given
        String departureAirport = "JFK";
        String destinationAirport = "LAX";
        Instant now = Instant.now();
        Instant departureTimeStart = now.plus(2, ChronoUnit.DAYS);
        Instant departureTimeEnd = now.plus(3, ChronoUnit.DAYS);
        Instant arrivalTimeStart = now.plus(1, ChronoUnit.DAYS);
        Instant arrivalTimeEnd = now.plus(4, ChronoUnit.DAYS);

        FlightValidationException exception = assertThrows(
                FlightValidationException.class,
                () -> flightService.validateSearchParam(
                        FlightSearchCriteria.builder().departureAirport(departureAirport)
                                .destinationAirport(destinationAirport)
                                .departureTimeStart(departureTimeStart)
                                .departureTimeEnd(departureTimeEnd)
                                .arrivalTimeStart(arrivalTimeStart)
                                .arrivalTimeEnd(arrivalTimeEnd)
                                .build()
                ));
        assertTrue(exception.getErrors().contains("Arrival time cannot be before departure time"));
    }
}

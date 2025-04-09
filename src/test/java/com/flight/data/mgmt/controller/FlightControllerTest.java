package com.flight.data.mgmt.controller;

import com.flight.data.mgmt.dto.FlightResponseDTO;
import com.flight.data.mgmt.dto.FlightSearchCriteriaDTO;
import com.flight.data.mgmt.service.FlightService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(FlightController.class)
public class FlightControllerTest {

    @MockBean
    private FlightService flightService;

    Instant now = Instant.now();
    String departureAirport = "LAX";
    String destinationAirport = "BLR";
    Instant arrivalTime = now.plus(1, ChronoUnit.DAYS);

    @Test
    public void searchFlightSuccess() {
        FlightResponseDTO flight = FlightResponseDTO.builder()
                .departureAirport(departureAirport)
                .destinationAirport(destinationAirport)
                .departureTime(now)
                .arrivalTime(arrivalTime)
                .build();

        when(flightService.searchFlights(any(FlightSearchCriteriaDTO.class)))
                .thenReturn(Collections.singletonList(flight));

    }

}

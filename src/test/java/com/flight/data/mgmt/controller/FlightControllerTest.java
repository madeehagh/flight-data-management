package com.flight.data.mgmt.controller;

import com.flight.data.mgmt.dto.FlightSearchCriteria;
import com.flight.data.mgmt.model.Flight;
import com.flight.data.mgmt.service.FlightService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@WebMvcTest(FlightController.class)
public class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    Instant now = Instant.now();
    String departureAirport = "LAX";
    String destinationAirport = "BLR";
    Instant arrivalTime = now.plus(1, ChronoUnit.DAYS);
    @Test
    public void searchFlightSuccess() throws Exception {
        Flight flight = Flight.builder()
                .departureAirport(departureAirport)
                .destinationAirport(destinationAirport)
                .departureTime(now)
                .arrivalTime(arrivalTime)
                .build();

        when(flightService.searchFlights(any(FlightSearchCriteria.class)))
                .thenReturn(asList(flight));

    }

}

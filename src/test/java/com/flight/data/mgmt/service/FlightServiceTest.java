package com.flight.data.mgmt.service;

import com.flight.data.mgmt.dto.FlightResponseDTO;
import com.flight.data.mgmt.dto.FlightSearchCriteriaDTO;
import com.flight.data.mgmt.dto.RouteSearchRequestDTO;
import com.flight.data.mgmt.exception.FlightValidationException;
import com.flight.data.mgmt.mapper.FlightMapper;
import com.flight.data.mgmt.model.Flight;
import com.flight.data.mgmt.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FlightServiceTest {

    @InjectMocks
    private FlightService flightService;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightMapper flightMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should accept valid search parameters without throwing exception")
    void validateSearchParamValidParameters() {
        String departureAirport = "JFK";
        String destinationAirport = "BLR";
        Instant now = Instant.now();
        Instant outbound = now.plus(1, ChronoUnit.DAYS);
        Instant inbound = now.plus(2, ChronoUnit.DAYS);

        assertDoesNotThrow(() -> flightService.validateSearchParam(
                FlightSearchCriteriaDTO.builder().departureAirport(departureAirport)
                        .destinationAirport(destinationAirport)
                        .outboundDate(outbound)
                        .inboundDate(inbound)
                        .build()
        ));
    }

    @Test
    @DisplayName("Should accept valid search parameters when arrival time is not given")
    void validateSearchParamWithoutArrivalTime() {
        String departureAirport = "JFK";
        String destinationAirport = "BLR";
        Instant now = Instant.now();
        Instant outbound = now.plus(1, ChronoUnit.DAYS);
        Instant inbound = now.plus(2, ChronoUnit.DAYS);

        assertDoesNotThrow(() -> flightService.validateSearchParam(
                FlightSearchCriteriaDTO.builder().departureAirport(departureAirport)
                        .destinationAirport(destinationAirport)
                        .outboundDate(outbound)
                        .inboundDate(inbound)
                        .build()
        ));
    }

    @Test
    @DisplayName("Should throw exception when inbound time is before outbound time")
    void validateSearchParamArrivalBeforeDeparture_ThrowsException() {
        String departureAirport = "JFK";
        String destinationAirport = "BLR";
        Instant now = Instant.now();
        Instant inbound = now.plus(1, ChronoUnit.DAYS);
        Instant outbound = now.plus(2, ChronoUnit.DAYS);

        FlightValidationException exception = assertThrows(
                FlightValidationException.class,
                () -> flightService.validateSearchParam(
                        FlightSearchCriteriaDTO.builder().departureAirport(departureAirport)
                                .destinationAirport(destinationAirport)
                                .outboundDate(outbound)
                                .inboundDate(inbound)
                                .build()
                ));
        assertTrue(exception.getErrors().contains("Inbound must be after Outbound time"));
    }

    @Test
    @DisplayName("Should return flights for the given route")
    void searchByRoute_Success() {

        RouteSearchRequestDTO request = RouteSearchRequestDTO.builder()
                .departureAirport("JFK")
                .destinationAirport("BLR")
                .build();

        Flight flight = Flight.builder()
                .departureAirport("JFK")
                .destinationAirport("BLR")
                .build();

        FlightResponseDTO expectedDTO = FlightResponseDTO.builder()
                .departureAirport("JFK")
                .destinationAirport("BLR")
                .build();

        when(flightRepository.findByRoute("JFK", "BLR"))
                .thenReturn(Arrays.asList(flight));

        when(flightMapper.toDTO(flight))
                .thenReturn(expectedDTO);

        List<FlightResponseDTO> result = flightService.searchByRoute(request);
        verify(flightRepository).findByRoute("JFK", "BLR");
        verify(flightMapper).toDTO(flight);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("JFK", result.getFirst().getDepartureAirport());
        assertEquals("BLR", result.getFirst().getDestinationAirport());
    }
}

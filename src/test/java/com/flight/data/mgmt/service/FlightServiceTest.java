package com.flight.data.mgmt.service;

import com.flight.data.mgmt.config.ErrorMessages;
import com.flight.data.mgmt.dto.FlightRequestDTO;
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
import java.util.List;
import java.util.Optional;

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

        when(flightMapper.toFlightResponseDTO(flight))
                .thenReturn(expectedDTO);

        List<FlightResponseDTO> result = flightService.searchByRoute(request);
        verify(flightRepository).findByRoute("JFK", "BLR");
        verify(flightMapper).toFlightResponseDTO(flight);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("JFK", result.getFirst().getDepartureAirport());
        assertEquals("BLR", result.getFirst().getDestinationAirport());
    }

    @Test
    @DisplayName("Should update flight successfully")
    void updateFlight_Success() {

        String flightNumber = "AA123";
        FlightRequestDTO requestDTO = createValidFlightRequestDTO();
        Flight existingFlight = createExistingFlight(flightNumber);
        Flight updatedFlight = createUpdatedFlight(flightNumber);

        when(flightRepository.findByFlightNumber(flightNumber))
                .thenReturn(Optional.of(existingFlight));
        when(flightMapper.toFlightDto(requestDTO))
                .thenReturn(updatedFlight);
        when(flightRepository.save(updatedFlight))
                .thenReturn(updatedFlight);

        flightService.updateFlight(flightNumber, requestDTO);

        verify(flightRepository).findByFlightNumber(flightNumber);
        verify(flightMapper).toFlightDto(requestDTO);
        verify(flightRepository).save(updatedFlight);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent flight")
    void updateFlight_NotFound() {

        String flightNumber = "AA123";
        FlightRequestDTO requestDTO = createValidFlightRequestDTO();

        when(flightRepository.findByFlightNumber(flightNumber))
                .thenReturn(Optional.empty());

        FlightValidationException exception = assertThrows(
                FlightValidationException.class,
                () -> flightService.updateFlight(flightNumber, requestDTO)
        );
        assertTrue(exception.getErrors().contains(ErrorMessages.FLIGHT_NOT_FOUND_WITH_NUMBER + flightNumber));
    }

    @Test
    @DisplayName("Should delete flight successfully")
    void deleteFlight_Success() {

        String flightNumber = "AA123";
        Flight existingFlight = createExistingFlight(flightNumber);
        when(flightRepository.findByFlightNumber(flightNumber))
                .thenReturn(Optional.of(existingFlight));

        flightService.deleteFlight(flightNumber);

        verify(flightRepository).findByFlightNumber(flightNumber);
        verify(flightRepository).delete(existingFlight);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent flight")
    void deleteFlight_NotFound() {
        String flightNumber = "AA123";
        when(flightRepository.findByFlightNumber(flightNumber))
                .thenReturn(Optional.empty());

        FlightValidationException exception = assertThrows(
                FlightValidationException.class,
                () -> flightService.deleteFlight(flightNumber)
        );
        assertTrue(exception.getErrors().contains(ErrorMessages.FLIGHT_NOT_FOUND_WITH_NUMBER + flightNumber));
    }

    private FlightRequestDTO createValidFlightRequestDTO() {
        return FlightRequestDTO.builder()
                .airline("AA")
                .supplier("LocalDB")
                .fare(299.99)
                .departureAirport("JFK")
                .destinationAirport("LAX")
                .departureTime(Instant.now().plus(1, ChronoUnit.DAYS))
                .arrivalTime(Instant.now().plus(2, ChronoUnit.DAYS))
                .build();
    }

    private Flight createExistingFlight(String flightNumber) {
        return Flight.builder()
                .id(1L)
                .flightNumber(flightNumber)
                .airLine("UA")
                .supplier("LocalDB")
                .fare(199.99)
                .departureAirport("SFO")
                .destinationAirport("ORD")
                .departureTime(Instant.now())
                .arrivalTime(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();
    }

    private Flight createUpdatedFlight(String flightNumber) {
        return Flight.builder()
                .id(1L)
                .flightNumber(flightNumber)
                .airLine("AA")
                .supplier("LocalDB")
                .fare(299.99)
                .departureAirport("JFK")
                .destinationAirport("LAX")
                .departureTime(Instant.now().plus(1, ChronoUnit.DAYS))
                .arrivalTime(Instant.now().plus(2, ChronoUnit.DAYS))
                .build();
    }
}

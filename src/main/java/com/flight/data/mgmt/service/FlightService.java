package com.flight.data.mgmt.service;


import com.flight.data.mgmt.config.ErrorMessages;
import com.flight.data.mgmt.dto.*;
import com.flight.data.mgmt.exception.FlightValidationException;
import com.flight.data.mgmt.mapper.FlightMapper;
import com.flight.data.mgmt.model.Flight;
import com.flight.data.mgmt.repository.FlightRepository;
import com.flight.data.mgmt.util.FlightNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightService {

    private static final Logger log = LoggerFactory.getLogger(FlightService.class);

    private final FlightRepository flightRepository;
    private final CrazySupplierService crazySupplierService;
    private final FlightMapper flightMapper;


    public List<FlightResponseDTO> searchFlights(FlightSearchCriteriaDTO flightSearchCriteriaDTO) {

        validateSearchParam(flightSearchCriteriaDTO);

        List<Flight> localFlights = flightRepository.findByRoute(
                flightSearchCriteriaDTO.getDepartureAirport(),
                flightSearchCriteriaDTO.getDestinationAirport());

        List<Flight> crazySupplierFlights = crazySupplierService.searchFlights(flightSearchCriteriaDTO);

        List<Flight> allFlights = new ArrayList<>(localFlights);
        allFlights.addAll(crazySupplierFlights);

        return allFlights.stream().map(flightMapper::toFlightResponseDTO).collect(Collectors.toList());

    }


    public List<FlightResponseDTO> searchByRoute(String departureAirport, String destinationAirport) {

        List<Flight> flights = flightRepository.findByRoute(
                departureAirport,
                destinationAirport
        );

        if (flights.isEmpty()) {
            log.warn("No flights found for route from {} to {}", departureAirport, destinationAirport);
        }
        return flights.stream()
                .map(flightMapper::toFlightResponseDTO)
                .collect(Collectors.toList());
    }

    public List<FlightResponseDTO> searchByDepartureAndDestination(String departureAirport,
                                                                   Instant departureTime,
                                                                   Instant destinationTime) {

        List<Flight> flights = flightRepository.findByDepartureAndDestination(
                departureAirport,
                departureTime,
                destinationTime
        );

        if (flights.isEmpty()) {
            log.warn("No flights found for Origin {}", departureAirport);
        }

        return flights.stream()
                .map(flightMapper::toFlightResponseDTO)
                .collect(Collectors.toList());
    }

    public FlightResponseDTO createFlight(FlightRequestDTO flightRequestDTO) {
        log.debug("Received request to create flight: {}", flightRequestDTO);
        Flight flight = flightMapper.toFlightDto(flightRequestDTO);
        validateFlight(flight);

        String flightNumber = FlightNumberGenerator.generateFlightNumber(flight.getAirLine());
        while (flightRepository.findByFlightNumber(flightNumber).isPresent()) {
            flightNumber = FlightNumberGenerator.generateFlightNumber(flight.getAirLine());
        }

        flight.setFlightNumber(flightNumber);
        flightRepository.save(flight);

        log.info("Successfully saved flight with number: {}", flight.getFlightNumber());

        return flightMapper.toFlightResponseDTO(flight);
    }

    public void updateFlight(String flightNumber, FlightRequestDTO flightRequestDTO) {

        Flight existingFlight = isExistingFlight(flightNumber);
        Flight updatedFlight = flightMapper.toFlightDto(flightRequestDTO);
        updatedFlight.setId(existingFlight.getId());
        updatedFlight.setFlightNumber(existingFlight.getFlightNumber());

        validateFlight(updatedFlight);
        flightRepository.save(updatedFlight);
    }

    private Flight isExistingFlight(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new FlightValidationException(
                        ErrorMessages.FLIGHT_NOT_FOUND,
                        Collections.singletonList(ErrorMessages.FLIGHT_NOT_FOUND_WITH_NUMBER + flightNumber)
                ));
    }

    public void deleteFlight(String flightNumber) {

        Flight existingFlight = isExistingFlight(flightNumber);
        flightRepository.delete(existingFlight);
    }

    private boolean isInvalidAirportCode(String destinationAirport) {
        String AIRLINE_CODE_VALIDATION_PATTERN = "[A-Za-z]{3}";

        if (destinationAirport == null || destinationAirport.length() != 3) {
            return true;
        }
        return !destinationAirport.matches(AIRLINE_CODE_VALIDATION_PATTERN);
    }

    void validateSearchParam(FlightSearchCriteriaDTO searchCriteria) {
        {
            String departureAirport = searchCriteria.getDepartureAirport();
            String destinationAirport = searchCriteria.getDestinationAirport();
            Instant inboundDate = searchCriteria.getInboundDate();
            Instant outboundDate = searchCriteria.getOutboundDate();

            List<String> errors = new ArrayList<>();

            if (isInvalidAirportCode(departureAirport)) {
                errors.add("Invalid departure airport code: " + departureAirport);
            }

            if (isInvalidAirportCode(destinationAirport)) {
                errors.add("Invalid destination airport code: " + destinationAirport);
            }

            if (departureAirport.equals(destinationAirport)) {
                errors.add("Departure and destination airports cannot be the same");
            }

            Instant now = Instant.now();

            if (outboundDate.isBefore(now)) {
                errors.add("Outbound time cannot be in the past");
            }

            if (inboundDate.isBefore(outboundDate)) {
                errors.add("Inbound must be after Outbound time");
            }

            if (!errors.isEmpty()) {
                log.error(ErrorMessages.FLIGHT_VALIDATION_FAILED, errors);
                throw new FlightValidationException("Search parameter validation failed", errors);
            }
        }
    }

    private void validateFlight(Flight flight) {
        log.debug("Validating flight: {}", flight.getFlightNumber());
        List<String> errors = new ArrayList<>();

        if (isInvalidAirportCode(flight.getDepartureAirport())) {
            errors.add(ErrorMessages.INVALID_DEPARTURE_AIRPORT + flight.getDepartureAirport());
        }

        if (isInvalidAirportCode(flight.getDestinationAirport())) {
            errors.add(ErrorMessages.INVALID_DESTINATION_AIRPORT + flight.getDestinationAirport());
        }

        if (flight.getDepartureAirport().equals(flight.getDestinationAirport())) {
            errors.add(ErrorMessages.SAME_AIRPORTS);
        }

        if (flight.getArrivalTime().isBefore(flight.getDepartureTime())) {
            errors.add(ErrorMessages.ARRIVAL_BEFORE_DEPARTURE);
        }

        if (flight.getFare() <= 0) {
            errors.add(ErrorMessages.INVALID_FARE);
        }

        if (flight.getAirLine() == null || flight.getAirLine().trim().isEmpty()) {
            errors.add(ErrorMessages.MISSING_AIRLINE);
        }

        if (flight.getSupplier() == null || flight.getSupplier().trim().isEmpty()) {
            errors.add(ErrorMessages.MISSING_SUPPLIER);
        }

        if (!errors.isEmpty()) {
            log.error(ErrorMessages.FLIGHT_VALIDATION_FAILED, errors);
            throw new FlightValidationException(ErrorMessages.FLIGHT_VALIDATION_FAILED, errors);
        }
        log.debug("Validation successful for flight: {}", flight.getFlightNumber());
    }

}


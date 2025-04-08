package com.flight.data.mgmt.service;


import com.flight.data.mgmt.config.ErrorMessages;
import com.flight.data.mgmt.dto.AirlineSearchRequestDTO;
import com.flight.data.mgmt.dto.FlightResponseDTO;
import com.flight.data.mgmt.dto.FlightSearchCriteriaDTO;
import com.flight.data.mgmt.dto.RouteSearchRequestDTO;
import com.flight.data.mgmt.exception.FlightValidationException;
import com.flight.data.mgmt.mapper.FlightMapper;
import com.flight.data.mgmt.model.Flight;
import com.flight.data.mgmt.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightService {

    private static final Logger logger = LoggerFactory.getLogger(FlightService.class);

    private final FlightRepository flightRepository;
    private final CrazySupplierService crazySupplierService;
    private final FlightMapper flightMapper;


    public List<Flight> searchFlights(FlightSearchCriteriaDTO flightSearchCriteriaDTO) {

        validateSearchParam(flightSearchCriteriaDTO);

        List<Flight> crazySupplierFlights = crazySupplierService.searchFlights(flightSearchCriteriaDTO);

        crazySupplierFlights.sort(Comparator.comparing(Flight::getDepartureTime));

        return crazySupplierFlights;
    }


    public List<FlightResponseDTO> searchByRoute(RouteSearchRequestDTO routeSearchRequestDTO) {

        List<Flight> flights = flightRepository.findByRoute(
                routeSearchRequestDTO.getDepartureAirport(),
                routeSearchRequestDTO.getDestinationAirport()
        );

        if (flights.isEmpty()) {
            logger.warn("No flights found for route {}", routeSearchRequestDTO.getDepartureAirport());
        }
        return flights.stream()
                .map(flightMapper:: toDTO)
                .collect(Collectors.toList());
    }

    public List<FlightResponseDTO> searchByAirlineAndTime(AirlineSearchRequestDTO request) {

        List<Flight> flights = flightRepository.findByAirlineAndDepartureTime(
                request.getAirline(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (flights.isEmpty()) {
            logger.warn("No flights found for Airline {}", request.getAirline());
        }

        return flights.stream()
                .map(flightMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Flight getFlightById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new FlightValidationException(ErrorMessages.FLIGHT_NOT_FOUND,
                        Collections.singletonList(ErrorMessages.FLIGHT_NOT_FOUND_WITH_ID + id)));
    }

    public Flight createFlight(Flight flight) {
        validateFlight(flight);
        return flightRepository.save(flight);
    }

    public Flight updateFlight(Long id, Flight flight) {
        if (!flightRepository.existsById(id)) {
            throw new FlightValidationException(ErrorMessages.FLIGHT_NOT_FOUND,
                    Collections.singletonList(ErrorMessages.FLIGHT_NOT_FOUND_WITH_ID + id));
        }
        flight.setId(id);
        validateFlight(flight);
        return flightRepository.save(flight);
    }

    public void deleteFlight(Long id) {
        if (!flightRepository.existsById(id)) {
            throw new FlightValidationException(ErrorMessages.FLIGHT_NOT_FOUND,
                    Collections.singletonList(ErrorMessages.FLIGHT_NOT_FOUND_WITH_ID + id));
        }
        flightRepository.deleteById(id);
    }

    private boolean isInvalidAirportCode(String destinationAirport) {
        if (destinationAirport == null || destinationAirport.length() != 3) {
            return true;
        }
        return !destinationAirport.matches("[A-Za-z]{3}");
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
                throw new FlightValidationException("Search parameter validation failed", errors);
            }
        }
    }

    private void validateFlight(Flight flight) {
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
            throw new FlightValidationException(ErrorMessages.FLIGHT_VALIDATION_FAILED, errors);
        }
    }

}


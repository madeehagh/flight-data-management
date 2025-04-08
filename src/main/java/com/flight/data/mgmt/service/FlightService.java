package com.flight.data.mgmt.service;


import com.flight.data.mgmt.dto.FlightSearchCriteria;
import com.flight.data.mgmt.exception.FlightValidationException;
import com.flight.data.mgmt.model.Flight;
import com.flight.data.mgmt.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;
    private final CrazySupplierService crazySupplierService;

    public List<Flight> searchFlights(FlightSearchCriteria flightSearchCriteria) {

        validateSearchParam(flightSearchCriteria);

        List<Flight> localFlights = flightRepository.searchFlights(
                flightSearchCriteria.getDepartureAirport(),
                flightSearchCriteria.getDestinationAirport(),
                flightSearchCriteria.getAirlineCode(),
                flightSearchCriteria.getDepartureTimeStart(),
                flightSearchCriteria.getDepartureTimeEnd(),
                flightSearchCriteria.getArrivalTimeStart(),
                flightSearchCriteria.getArrivalTimeEnd()
        );

        List<Flight> crazySupplierFlights = crazySupplierService.searchFlights(flightSearchCriteria);

        List<Flight> allFlights = new ArrayList<>(localFlights);
        allFlights.addAll(crazySupplierFlights);
        allFlights.sort(Comparator.comparing(Flight::getDepartureTime));

        return allFlights;
    }

    void validateSearchParam(FlightSearchCriteria searchCriteria) {
        {
            String departureAirport = searchCriteria.getDepartureAirport();
            String destinationAirport = searchCriteria.getDestinationAirport();
            Instant departureTimeStart = searchCriteria.getDepartureTimeStart();
            Instant departureTimeEnd = searchCriteria.getDepartureTimeEnd();
            Instant arrivalTimeStart = searchCriteria.getArrivalTimeStart();
            Instant arrivalTimeEnd = searchCriteria.getArrivalTimeEnd();

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

            if (departureTimeStart.isBefore(now)) {
                errors.add("Departure start time cannot be in the past");
            }

            if (departureTimeEnd.isBefore(departureTimeStart)) {
                errors.add("Departure end time must be after departure start time");
            }

            if (arrivalTimeStart != null && arrivalTimeEnd != null) {
                if (arrivalTimeEnd.isBefore(arrivalTimeStart)) {
                    errors.add("Arrival end time must be after arrival start time");
                }

                if (arrivalTimeStart.isBefore(departureTimeStart)) {
                    errors.add("Arrival time cannot be before departure time");
                }
            }

            if (!errors.isEmpty()) {
                throw new FlightValidationException("Search parameter validation failed", errors);
            }
        }
    }

    private boolean isInvalidAirportCode(String destinationAirport) {
        if (destinationAirport == null || destinationAirport.length() != 3) {
            return true;
        }
        return !destinationAirport.matches("[A-Za-z]{3}");
    }
}


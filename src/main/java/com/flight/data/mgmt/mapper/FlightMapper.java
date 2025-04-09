package com.flight.data.mgmt.mapper;

import com.flight.data.mgmt.dto.FlightRequestDTO;
import com.flight.data.mgmt.dto.FlightResponseDTO;
import com.flight.data.mgmt.model.Flight;
import org.springframework.stereotype.Component;

@Component
public class FlightMapper {
    public FlightResponseDTO toFlightResponseDTO(Flight flight) {
        FlightResponseDTO dto = new FlightResponseDTO();
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setAirline(flight.getAirLine());
        dto.setSupplier(flight.getSupplier());
        dto.setFare(flight.getFare());
        dto.setDepartureAirport(flight.getDepartureAirport());
        dto.setDestinationAirport(flight.getDestinationAirport());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        return dto;
    }

    public Flight toFlightDto(FlightRequestDTO flightRequestDTO) {
        return Flight.builder()
                .airLine(flightRequestDTO.getAirline())
                .supplier(flightRequestDTO.getSupplier())
                .destinationAirport(flightRequestDTO.getDestinationAirport())
                .arrivalTime(flightRequestDTO.getArrivalTime())
                .departureAirport(flightRequestDTO.getDepartureAirport())
                .departureTime(flightRequestDTO.getDepartureTime())
                .fare(flightRequestDTO.getFare())
                .build();
    }

}

package com.flight.data.mgmt.mapper;

import com.flight.data.mgmt.dto.CrazySupplierFlightRequestDTO;
import com.flight.data.mgmt.dto.CrazySupplierFlightResponseDTO;
import com.flight.data.mgmt.dto.FlightSearchCriteriaDTO;
import com.flight.data.mgmt.model.Flight;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;

@Component
public class CrazySupplierMapper {
    private static final String SUPPLIER_NAME = "CrazySupplier";
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId CET = ZoneId.of("CET");

    public Flight toFlight(CrazySupplierFlightResponseDTO dto) {
        if (dto == null) {
            return null;
        }

        return Flight.builder()
                .airLine(dto.getCarrier())
                .supplier(SUPPLIER_NAME)
                .fare(calculateTotalFare(dto))
                .departureAirport(dto.getDepartureAirport())
                .destinationAirport(dto.getArrivalAirport())
                .departureTime(convertToUTC(dto.getOutboundDateTime()))
                .arrivalTime(convertToUTC(dto.getInboundDateTime()))
                .build();
    }

    private Instant convertToUTC(Instant outboundDateTime) {
        return outboundDateTime.atZone(CET)
                .withZoneSameInstant(UTC)
                .toInstant();
    }

    private double calculateTotalFare(CrazySupplierFlightResponseDTO crazySupplierFlightResponseDTO) {
        return crazySupplierFlightResponseDTO.getBasePrice() + crazySupplierFlightResponseDTO.getTax();
    }

    public CrazySupplierFlightRequestDTO toCrazySupplierRequestDTO(FlightSearchCriteriaDTO flightSearchCriteriaDTO) {
        if (flightSearchCriteriaDTO == null) {
            throw new IllegalArgumentException("Flight search criteria cannot be null");
        }

        return CrazySupplierFlightRequestDTO.builder()
                .fromAirLine(flightSearchCriteriaDTO.getDepartureAirport())
                .inboundDateTime(flightSearchCriteriaDTO.getOutboundDate())
                .outboundDateTime(flightSearchCriteriaDTO.getInboundDate())
                .build();
    }
}

package com.flight.data.mgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponseDTO {
    private String flightNumber;
    private String airline;
    private String supplier;
    private double fare;
    private String departureAirport;
    private String destinationAirport;
    private Instant departureTime;
    private Instant arrivalTime;
}

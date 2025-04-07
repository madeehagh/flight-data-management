package com.flight.data.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class CrazySupplierFlightResponseDTO {
    private String carrier;
    private Double basePrice;
    private Double tax;
    private String departureAirport;
    private String arrivalAirport;
    private Instant outboundDateTime;
    private Instant inboundDateTime;
}

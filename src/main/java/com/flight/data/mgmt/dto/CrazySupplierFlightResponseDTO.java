package com.flight.data.mgmt.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CrazySupplierFlightResponseDTO {
    private String carrier;
    private Double basePrice;
    private Double tax;
    private String departureAirport;
    private String arrivalAirport;
    private Instant outboundDateTime;
    private Instant inboundDateTime;
}

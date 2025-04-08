package com.flight.data.mgmt.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
@Builder
public class FlightSearchCriteriaDTO {
    @NotNull
    @Size(min = 3, max = 3, message = "Airport Code must be 3 chars")
    private String departureAirport;

    @NotNull
    @Size(min = 3, max = 3, message = "Airport Code must be 3 chars")
    private String destinationAirport;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant outboundDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant inboundDate;
}

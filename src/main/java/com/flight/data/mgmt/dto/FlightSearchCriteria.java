package com.flight.data.mgmt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class FlightSearchCriteria {
    @NotNull
    private String departureAirport;

    @NotNull
    private String destinationAirport;

    @NotNull
    private Instant departureTimeStart;

    @NotNull
    private Instant departureTimeEnd;
}

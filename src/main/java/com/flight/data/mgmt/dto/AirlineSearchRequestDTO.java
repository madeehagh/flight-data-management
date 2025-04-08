package com.flight.data.mgmt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class AirlineSearchRequestDTO {
    @NotNull
    private String airline;

    @NotNull
    private Instant startTime;

    @NotNull
    private Instant endTime;
}

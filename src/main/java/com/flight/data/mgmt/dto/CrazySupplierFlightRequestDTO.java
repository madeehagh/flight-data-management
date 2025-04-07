package com.flight.data.mgmt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CrazySupplierFlightRequestDTO {
    @NotNull
    private String fromAirLine;

    @NotNull
    private String toAirLine;

    @NotNull
    private Instant outboundDateTime;

    @NotNull
    private Instant inboundDateTime;
}

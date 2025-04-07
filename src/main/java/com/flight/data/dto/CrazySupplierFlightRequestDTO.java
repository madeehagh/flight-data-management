package com.flight.data.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@Data
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

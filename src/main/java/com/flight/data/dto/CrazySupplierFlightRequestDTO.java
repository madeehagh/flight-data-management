package com.flight.data.dto;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

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

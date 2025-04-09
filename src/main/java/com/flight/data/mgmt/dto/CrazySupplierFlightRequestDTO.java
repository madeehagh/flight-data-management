package com.flight.data.mgmt.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

@Data
@Builder
public class CrazySupplierFlightRequestDTO {
    @NotNull
    @Size(min = 3, max = 3, message = "Airport Code must be 3 chars")
    private String fromAirLine;

    @NotNull
    @Size(min = 3, max = 3, message = "Airport Code must be 3 chars")
    private String toAirLine;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant outboundDateTime;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant inboundDateTime;
}

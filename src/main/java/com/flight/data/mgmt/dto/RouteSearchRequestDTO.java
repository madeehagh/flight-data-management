package com.flight.data.mgmt.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RouteSearchRequestDTO {
    @NotNull
    @Size(min = 3, max = 3)
    private String departureAirport;

    @NotNull
    @Size(min = 3, max = 3)
    private String destinationAirport;
}

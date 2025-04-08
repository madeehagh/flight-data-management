package com.flight.data.mgmt.controller;

import com.flight.data.mgmt.dto.FlightSearchCriteria;
import com.flight.data.mgmt.model.Flight;
import com.flight.data.mgmt.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/v1/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping("/search")
    @Operation(summary = "search flights", description = "returns list of flights")
    public ResponseEntity<List<Flight>> search(@Valid FlightSearchCriteria searchCriteria) {

        List<Flight> flights = flightService.searchFlights(searchCriteria);

        return ResponseEntity.ok(flights);
    }
}

package com.flight.data.mgmt.controller;

import com.flight.data.mgmt.dto.*;
import com.flight.data.mgmt.model.Flight;
import com.flight.data.mgmt.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping("/search")
    @Operation(summary = "search flights", description = "returns list of flights")
    public ResponseEntity<List<FlightResponseDTO>> search(@Valid FlightSearchCriteriaDTO searchCriteria) {

        List<FlightResponseDTO> flights = flightService.searchFlights(searchCriteria);

        return ResponseEntity.ok(flights);
    }

    @GetMapping("/route")
    @Operation(summary = "search flights by routes", description = "returns list of flights")
    public ResponseEntity<List<FlightResponseDTO>> searchByRoute(
            @Valid @RequestBody RouteSearchRequestDTO request) {
        return ResponseEntity.ok(flightService.searchByRoute(request));
    }

    @GetMapping("/airline")
    @Operation(summary = "search flights by airline, outbound and inbound time", description = "returns list of flights")
    public ResponseEntity<List<FlightResponseDTO>> searchByAirlineAndTime(
            @Valid @RequestBody AirlineSearchRequestDTO request) {
        return ResponseEntity.ok(flightService.searchByAirlineAndTime(request));
    }


    @PostMapping
    @Operation(summary = "create Flight entity", description = "returns 201")
    public ResponseEntity<Void> createFlight(@RequestBody @Valid FlightRequestDTO flight) {
        flightService.createFlight(flight);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{flightNumber}")
    public ResponseEntity<Void> updateFlight(@PathVariable String flightNumber, @RequestBody @Valid FlightRequestDTO flightRequestDTO) {
        Flight updatedFlight = flightService.updateFlight(flightNumber, flightRequestDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{flightNumber}")
    public ResponseEntity<Void> deleteFlight(@PathVariable String flightNumber) {
        flightService.deleteFlight(flightNumber);
        return ResponseEntity.noContent().build();
    }
}

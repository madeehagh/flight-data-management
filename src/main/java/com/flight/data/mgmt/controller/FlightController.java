package com.flight.data.mgmt.controller;

import com.flight.data.mgmt.dto.*;
import com.flight.data.mgmt.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/v1/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping(value = "/search-airline", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "search flights", description = "returns list of flights")
    public ResponseEntity<List<FlightResponseDTO>> search(@Valid FlightSearchCriteriaDTO searchCriteria) {

        List<FlightResponseDTO> flights = flightService.searchFlights(searchCriteria);

        return ResponseEntity.ok(flights);
    }

    @GetMapping(value = "/route", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "search flights by routes", description = "returns list of flights")
    public ResponseEntity<List<FlightResponseDTO>> searchByRoute(
            @Valid @RequestParam String departureAirport, @RequestParam String destinationAirport) {
        return ResponseEntity.ok(flightService.searchByRoute(departureAirport, destinationAirport));
    }

    @GetMapping(value = "/departure-airport", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "search flights by airline, outbound and inbound time", description = "returns list of flights")
    public ResponseEntity<List<FlightResponseDTO>> searchByDepartureAndDestination(
            @RequestParam String departureAirport, @RequestParam Instant departureTime, @RequestParam(required = false) Instant destinationTime) {
        return ResponseEntity.ok(flightService.searchByDepartureAndDestination(departureAirport, departureTime, destinationTime));
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "create Flight entity", description = "returns 201")
    public ResponseEntity<FlightResponseDTO> createFlight(@RequestBody @Valid FlightRequestDTO flight) {
        return ResponseEntity.ok(flightService.createFlight(flight));
    }

    @PutMapping(value = "/{flightNumber}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateFlight(@PathVariable String flightNumber, @RequestBody @Valid FlightRequestDTO flightRequestDTO) {
        flightService.updateFlight(flightNumber, flightRequestDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(value = "/{flightNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteFlight(@PathVariable String flightNumber) {
        flightService.deleteFlight(flightNumber);
        return ResponseEntity.noContent().build();
    }
}

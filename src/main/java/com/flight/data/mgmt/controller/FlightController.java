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

import java.util.List;

@RestController
@RequestMapping("/v1/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "search flights", description = "returns list of flights")
    public ResponseEntity<List<FlightResponseDTO>> search(@Valid FlightSearchCriteriaDTO searchCriteria) {

        List<FlightResponseDTO> flights = flightService.searchFlights(searchCriteria);

        return ResponseEntity.ok(flights);
    }

    @GetMapping(value = "/route", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "search flights by routes", description = "returns list of flights")
    public ResponseEntity<List<FlightResponseDTO>> searchByRoute(
            @Valid @RequestBody RouteSearchRequestDTO request) {
        return ResponseEntity.ok(flightService.searchByRoute(request));
    }

    @GetMapping(value = "/airline", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "search flights by airline, outbound and inbound time", description = "returns list of flights")
    public ResponseEntity<List<FlightResponseDTO>> searchByAirlineAndTime(
            @Valid @RequestBody AirlineSearchRequestDTO request) {
        return ResponseEntity.ok(flightService.searchByAirlineAndTime(request));
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "create Flight entity", description = "returns 201")
    public ResponseEntity<Void> createFlight(@RequestBody @Valid FlightRequestDTO flight) {
        flightService.createFlight(flight);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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

package com.flight.data.mgmt.controller;

import com.flight.data.mgmt.dto.AirlineSearchRequestDTO;
import com.flight.data.mgmt.dto.FlightResponseDTO;
import com.flight.data.mgmt.dto.FlightSearchCriteriaDTO;
import com.flight.data.mgmt.dto.RouteSearchRequestDTO;
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
    public ResponseEntity<List<Flight>> search(@Valid FlightSearchCriteriaDTO searchCriteria) {

        List<Flight> flights = flightService.searchFlights(searchCriteria);

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
    public ResponseEntity<Flight> createFlight(@RequestBody @Valid Flight flight) {
        Flight createdFlight = flightService.createFlight(flight);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFlight);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Flight> updateFlight(@PathVariable Long id, @RequestBody @Valid Flight flight) {
        Flight updatedFlight = flightService.updateFlight(id, flight);
        return ResponseEntity.ok(updatedFlight);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }
}

package com.flight.data.mgmt.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "flights", indexes = {
        // Composite indexes for patterns route, airline and departure time
        @Index(name = "idx_route", columnList = "departureAirport, destinationAirport"),
        @Index(name = "idx_airline_departure", columnList = "departureAirport, departureTime")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number", unique = true)
    private String flightNumber;

    @NotNull
    private String airLine;

    @NotNull
    private String supplier;

    @NotNull
    private double fare;

    @NotNull
    @Column(name = "departure_airport", length = 3)
    private String departureAirport;

    @NotNull
    @Column(name = "destination_airport", length = 3)
    private String destinationAirport;

    @NotNull
    @Column(name = "departure_time")
    private Instant departureTime;

    @NotNull
    @Column(name = "arrival_time")
    private Instant arrivalTime;
}
package com.flight.data.model;


import jakarta.persistence.*;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.Instant;

@Entity
@Table(name = "flights")
@Data
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String airLine;

    @NotNull
    private String supplier;

    @NotNull
    private Double fare;

    @NotNull
    @Column(name = "departure_airport", length = 3, nullable = false)
    private String departureAirport;

    @NotNull
    @Column(name = "destination_airport", length = 3, nullable = false)
    private String destinationAirport;

    @NotNull
    @Column(name = "departure_time", nullable = false)
    private Instant departureTime;

    @NotNull
    @Column(name = "arrival_time", nullable = false)
    private Instant arrivalTime;
}
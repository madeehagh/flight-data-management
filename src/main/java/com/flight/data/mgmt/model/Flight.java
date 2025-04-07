package com.flight.data.mgmt.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "flights")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
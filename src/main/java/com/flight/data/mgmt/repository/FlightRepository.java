package com.flight.data.mgmt.repository;

import com.flight.data.mgmt.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    // search flights by route
    @Query("SELECT f FROM Flight f WHERE " +
            "f.departureAirport = :departureAirport AND " +
            "f.destinationAirport = :destinationAirport")
    List<Flight> findByRoute(
            @Param("departureAirport") String departureAirport,
            @Param("destinationAirport") String destinationAirport
    );

    // search flight and departure time range
    @Query("SELECT f FROM Flight f WHERE " +
            "f.departureAirport = :departureAirport AND " +
            "f.departureTime BETWEEN :startTime AND :endTime")
    List<Flight> findByDepartureAndDestination(
            @Param("departureAirport") String departureAirport,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime
    );

    @Query("SELECT f FROM Flight f WHERE f.flightNumber = :flightNumber")
    Optional<Flight> findByFlightNumber(@Param("flightNumber") String flightNumber);
}

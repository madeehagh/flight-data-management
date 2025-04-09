package com.flight.data.mgmt.repository;

import com.flight.data.mgmt.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("SELECT f FROM Flight f WHERE " +
            "f.departureAirport = :departureAirport AND " +
            "f.destinationAirport = :destinationAirport")
    List<Flight> findByRoute(
            @Param("departureAirport") String departureAirport,
            @Param("destinationAirport") String destinationAirport
    );

    @Query("SELECT f FROM Flight f WHERE " +
            "f.departureAirport = :departureAirport AND " +
            "(:destinationTime IS NULL OR " +
            "   (f.departureTime >= :departureTime AND f.departureTime <= :destinationTime)" +
            ")")
    List<Flight> findByDepartureAndDestination(
            @Param("departureAirport") String departureAirport,
            @Param("departureTime") Instant departureTime,
            @Param("destinationTime") Instant destinationTime
    );

    @Query("SELECT f FROM Flight f WHERE f.flightNumber = :flightNumber")
    Optional<Flight> findByFlightNumber(@Param("flightNumber") String flightNumber);
}

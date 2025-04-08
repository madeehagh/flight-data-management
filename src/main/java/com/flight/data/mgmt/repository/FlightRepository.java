package com.flight.data.mgmt.repository;

import com.flight.data.mgmt.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

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
            "f.airLine = :airline AND " +
            "f.departureTime BETWEEN :startTime AND :endTime")
    List<Flight> findByAirlineAndDepartureTime(
            @Param("airline") String airline,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime
    );
}

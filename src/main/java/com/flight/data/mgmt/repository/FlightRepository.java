package com.flight.data.mgmt.repository;

import com.flight.data.mgmt.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Query("SELECT f FROM Flight f WHERE " +
            "f.departureAirport = :departureAirport AND " +
            "f.destinationAirport = :destinationAirport AND " +
            "f.departureTime BETWEEN :departureTimeStart AND :departureTimeEnd AND " +
            "(:airline IS NULL OR f.airLine = :airline) AND " +
            "(:arrivalTimeStart IS NULL OR f.arrivalTime >= :arrivalTimeStart) AND " +
            "(:arrivalTimeEnd IS NULL OR f.arrivalTime <= :arrivalTimeEnd)")
    List<Flight> searchFlights(
            @Param("departureAirport") String departureAirport,
            @Param("destinationAirport") String destinationAirport,
            @Param("airline") String airline,
            @Param("departureTimeStart") Instant departureTimeStart,
            @Param("departureTimeEnd") Instant departureTimeEnd,
            @Param("arrivalTimeStart") Instant arrivalTimeStart,
            @Param("arrivalTimeEnd") Instant arrivalTimeEnd
    );

}

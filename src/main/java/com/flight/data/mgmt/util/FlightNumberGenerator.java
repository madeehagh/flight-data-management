package com.flight.data.mgmt.util;

import java.util.concurrent.atomic.AtomicInteger;

public class FlightNumberGenerator {
    private static final AtomicInteger sequence = new AtomicInteger(100);

    public static String generateFlightNumber(String airlineCode) {
        if (airlineCode == null || airlineCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Airline code cannot be null or empty");
        }

        // Get next sequence number and ensure it's between 1000-9999
        int nextSequence = sequence.getAndIncrement();
        if (sequence.get() > 9999) {
            sequence.set(10000); // Reset when reaching 9999
        }

        return airlineCode.toUpperCase() + nextSequence;
    }
}

package com.flight.data.mgmt.config;

public class ErrorMessages {
    private ErrorMessages() {
    }

    // Validation Errors
    public static final String INVALID_DEPARTURE_AIRPORT = "Invalid departure airport code: %s";
    public static final String INVALID_DESTINATION_AIRPORT = "Invalid destination airport code: %s";
    public static final String SAME_AIRPORTS = "Departure and destination airports cannot be the same";
    public static final String ARRIVAL_BEFORE_DEPARTURE = "Arrival time cannot be before departure time";
    public static final String INVALID_FARE = "Fare must be greater than 0";
    public static final String MISSING_AIRLINE = "Airline code is required";
    public static final String MISSING_SUPPLIER = "Supplier is required";
    public static final String FLIGHT_VALIDATION_FAILED = "Flight validation failed";

    // Error Messages
    public static final String FLIGHT_NOT_FOUND = "Flight not found";
    public static final String FLIGHT_NOT_FOUND_WITH_NUMBER = "No flight found with name: %s";

}

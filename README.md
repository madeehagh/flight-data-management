# Flight Data Management System

A Spring Boot application for managing and searching flight data from multiple sources.

## Core Features

### Flight Search
- Search by route (departure and destination airports)
- Search by airline and time range
- Aggregated results from:
    - Local database
    - CrazySupplier API (real-time)
- Fault-tolerant: System continues to work with local data if external API fails

### Flight Management
- Create, update, get and delete flight records locally
- Store flight data locally
- Real-time integration with CrazySupplier API

##### Assumptions

All time fields are in UTC
A user can at least search by departure-airport and departure time or arrival time.
A user can request to get flight info for source to destination
API call /search-airline returns aggregated value from local and external API call
Used H2 for in-memory storage for local data 
Composite Indexing done based on search criteria 

## Technical Stack

- Java 21
- Spring Boot 3.4.4
- H2 Database
- WebClient
- Maven

## API Endpoints

### Flight Management
```http
POST   /v1/api/flights                # Create flight
PUT    /v1/api/flights/{flightNumber} # Update flight
DELETE /v1/api/flights/{flightNumber} # Delete flight
GET /v1/api/flights/route             # Search flights by route
GET /v1/api/flights/departure-airport  # Search flights by departure-airport

```

### Flight Search
```http
GET /v1/api/flights/search-airline            # Search flights
```

#### Search by Route Parameters
Required:
- `departureAirport` (3-letter code)
- `destinationAirport` (3-letter code)

#### Search by Airline Parameters
Required:
- `airline` (airline code)
- `departureTime` (UTC timestamp)
- `destinationTime` (UTC timestamp)

#### General Search Parameters
Required:
- `departureAirport` (3-letter code)
- `destinationAirport` (3-letter code)
- `departureTimeStart` and `departureTimeEnd` (UTC)

Optional:
- `airline`
- `arrivalTimeStart` and `arrivalTimeEnd` (UTC)

### CrazySupplier API Requirements
All fields required for external API queries:
- `from`: 3-letter airline code
- `to`: 3-letter airline code
- `outboundDate`: ISO_LOCAL_DATE format – CET timezone
- `inboundDate`: ISO_LOCAL_DATE format – CET timezone

## Setup Guide

### Prerequisites
- Java 21+
- Maven 3.6+
- IDE (IntelliJ IDEA recommended)

### Installation Steps

1. Clone repository:
```bash
git clone https://github.com/yourusername/flight-data-management.git
```

2. Navigate to project:
```bash
cd flight-data-management
```

3. Build:
```bash
mvn clean install
```

4. Run:
```bash
mvn spring-boot:run
```

Application runs at: `http://localhost:8080`

## Testing

### Run Tests
```bash
mvn test
```
Or run the application and use swagger to test the APIs  http://localhost:8080/swagger-ui/index.html#/flight-controller/


### API Documentation
Access Swagger UI after starting the application:
# Flight Data Management System 🛫

This is a Spring Boot application that helps manage and search flight data from multiple sources.
Let me walk you through what this project is all about.

## What Does It Do?

This system helps you:

- Search for flights across multiple sources
- Manage your own flight data
- Get real-time flight information from CrazySupplier (external API call)
- Combine results from different sources
- Handle flight data efficiently

## Key Features ✨

- **Smart Search**: Search flights by:
    - Departure airport
    - Destination airport
    - Airline
    - Departure time
    - Arrival time

- **Multiple Data Sources**:
    - Local flight database
    - Real-time CrazySupplier API integration

- **RESTful API Endpoints**:
    - Create flights
    - Update flight information
    - Delete flights
    - Search flights
    - Get flight details

## Tech Stack 🛠️

- Java 21
- Spring Boot 3.4.4
- H2 Database (for local flight data)
- WebClient (for API integration)
- Maven (for dependency management)

## Getting Started 🚀

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Your favorite IDE (IntelliJ IDEA recommended)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/flight-data-management.git
   ```

2. Navigate to the project directory:
   ```bash
   cd flight-data-management
   ```

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

### Consideration

Users typically search for flights with at least origin and destination
Dates are important for flight searches
Airline might be optional for comparison shopping

**Make these parameters required:**

1. Origin (departure airport)
2. Destination (arrival airport)
3. Departure time range (from-to)

**Make these parameters optional:**

1. Airline
2. Arrival time range

## API Endpoints

### Manage Flights

```
POST  /v1/api/flights          # Create a new flight
PUT    /v1/api/flights/{id}     # Update a flight
DELETE /v1/api/flights/{id}     # Delete a flight
GET    /v1/api/flights/{id}     # Get flight details
```

### Search Flights

```
GET /v1/api/flights/search
```

##### Consideration

Users typically search for flights with at least origin and destination
Dates are important for flight searches
Airline might be optional for comparison shopping

**Make these parameters required:**

1. Origin (departure airport)
2. Destination (arrival airport)
3. Departure time range (from-to)

**Make these parameters optional:**

1. Airline
2. Arrival time range

Query parameters:

- `departureAirport`: 3-letter airport code
- `destinationAirport`: 3-letter airport code
- `airline`: Airline name (optional)
- `departureTimeStart`: ISO date-time (UTC)
- `departureTimeEnd`: ISO date-time (UTC)
- `arrivalTimeStart`: ISO date-time (UTC)
- `arrivalTimeEnd`: ISO date-time (UTC)

## How It Works

1. When you search for flights:
    - The system checks our local database
    - Simultaneously queries CrazySupplier API
    - Combines results from both sources
    - Returns sorted flight list

2. For flight management:
    - Local flights are stored in H2 database
    - CrazySupplier data is fetched in real-time
    - No CrazySupplier data is stored

## Configuration

The application uses these main configuration files:

- `application.properties`: Main configuration
- `pom.xml`: Dependencies and build settings

## Testing 🧪

Run tests with:

```bash
mvn test
```
or Open swagger link once the application is up and running by executing
  ```bash
   mvn spring-boot:run
   ```
http://localhost:8080/swagger-ui/index.html#/flight-controller


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

## API Endpoints 📡

### Search Flights
```
GET /api/flights/search
```
Query parameters:
- `departureAirport`: 3-letter airport code
- `destinationAirport`: 3-letter airport code
- `airline`: Airline name (optional)
- `departureTimeStart`: ISO date-time (UTC)
- `departureTimeEnd`: ISO date-time (UTC)
- `arrivalTimeStart`: ISO date-time (UTC)
- `arrivalTimeEnd`: ISO date-time (UTC)

### Manage Flights
```
POST   /api/flights          # Create a new flight
PUT    /api/flights/{id}     # Update a flight
DELETE /api/flights/{id}     # Delete a flight
GET    /api/flights/{id}     # Get flight details
```

## How It Works 🔄

1. When you search for flights:
   - The system checks our local database
   - Simultaneously queries CrazySupplier API
   - Combines results from both sources
   - Returns sorted flight list

2. For flight management:
   - Local flights are stored in H2 database
   - CrazySupplier data is fetched in real-time
   - No CrazySupplier data is stored

## Configuration ⚙️

The application uses these main configuration files:
- `application.properties`: Main configuration
- `pom.xml`: Dependencies and build settings

## Contributing 🤝

We welcome contributions! Here's how you can help:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## Testing 🧪

Run tests with:
```bash
mvn test
```

## License 📄

This project is licensed under the MIT License - see the LICENSE file for details.

## Support 💬

If you have questions or need help:
- Open an issue
- Contact the development team
- Check the documentation

## Acknowledgments 🙏

- Spring Boot team
- CrazySupplier API team
- All contributors

---

Made with ❤️ by the Flight Data Management Team 
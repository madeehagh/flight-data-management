package com.flight.data.mgmt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.flight.data.mgmt.dto.CrazySupplierFlightRequestDTO;
import com.flight.data.mgmt.dto.CrazySupplierFlightResponseDTO;
import com.flight.data.mgmt.dto.FlightSearchCriteria;
import com.flight.data.mgmt.mapper.CrazySupplierMapper;
import com.flight.data.mgmt.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CrazySupplierServiceTest {

    private static final String API_URL = "https://mocked.api/flights";
    private static final String MOCK_REQUEST_BODY = "{\"request\":\"data\"}";
    private static final String MOCK_RESPONSE_BODY = "[{\"response\":\"data\"}]";
    private static final String ERROR_RESPONSE_BODY = "{\"error\":\"Internal Server Error\"}";
    private static final String EMPTY_RESPONSE_BODY = "[]";

    @Mock
    private HttpClient httpClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CrazySupplierMapper mapper;

    @InjectMocks
    private CrazySupplierService crazySupplierService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(crazySupplierService, "apiUrl", API_URL);
    }

    @Nested
    @DisplayName("fetchFlightsFromApi Tests")
    class FetchFlightsFromApiTests {

        @Test
        @DisplayName("Should successfully fetch and map flights when API returns 200")
        void success() throws Exception {
            
            CrazySupplierFlightRequestDTO request = buildCrazySupplierFlightRequestDTO();
            CrazySupplierFlightResponseDTO responseDTO = buildCrazySupplierFlightResponseDTO();
            Flight expectedFlight = buildExpectedFlight();
            
            
            mockSuccessfulApiCall(request, responseDTO, expectedFlight);
            List<Flight> result = crazySupplierService.fetchFlightsFromApi(request);
            
            
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals(expectedFlight.getAirLine(), result.getFirst().getAirLine());

            verify(objectMapper).writeValueAsString(request);
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
            verify(objectMapper).getTypeFactory();
            verify(mapper).toFlight(responseDTO);
        }

        @Test
        @DisplayName("Should throw exception when API returns non-200 status")
        void non200Status_ThrowsException() throws Exception {
            
            CrazySupplierFlightRequestDTO request = buildCrazySupplierFlightRequestDTO();
            
            mockErrorApiCall(request);
            
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> crazySupplierService.fetchFlightsFromApi(request)
            );
            
            assertTrue(exception.getMessage().contains("API call failed with status: 500"));

            verify(objectMapper).writeValueAsString(request);
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
            verify(objectMapper, never()).getTypeFactory();
            verify(mapper, never()).toFlight(any());
        }

        @Test
        @DisplayName("Should handle empty response from API")
        void emptyResponse_ReturnsEmptyList() throws Exception {
            
            CrazySupplierFlightRequestDTO request = buildCrazySupplierFlightRequestDTO();
            mockEmptyApiCall(request);
            List<Flight> result = crazySupplierService.fetchFlightsFromApi(request);
            
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(objectMapper).writeValueAsString(request);
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
            verify(objectMapper).getTypeFactory();
            verify(mapper, never()).toFlight(any());
        }
    }
    
    @Nested
    @DisplayName("searchFlights Tests")
    class SearchFlightsTests {
        
        @Test
        @DisplayName("Should successfully search flights using search criteria")
        void success() throws Exception {
            
            FlightSearchCriteria searchCriteria = buildFlightSearchCriteria();
            CrazySupplierFlightRequestDTO requestDTO = buildCrazySupplierFlightRequestDTO();
            CrazySupplierFlightResponseDTO responseDTO = buildCrazySupplierFlightResponseDTO();
            Flight expectedFlight = buildExpectedFlight();

            when(mapper.toCrazySupplierRequestDTO(searchCriteria)).thenReturn(requestDTO);
            mockSuccessfulApiCall(requestDTO, responseDTO, expectedFlight);
            
            List<Flight> result = crazySupplierService.searchFlights(searchCriteria);
            
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals(expectedFlight.getAirLine(), result.getFirst().getAirLine());

            verify(mapper).toCrazySupplierRequestDTO(searchCriteria);
            verify(objectMapper).writeValueAsString(requestDTO);
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
            verify(objectMapper).getTypeFactory();
            verify(mapper).toFlight(responseDTO);
        }
        
        @Test
        @DisplayName("Should handle IOException during API call")
        void ioException_ThrowsRuntimeException() throws Exception {
            
            FlightSearchCriteria searchCriteria = buildFlightSearchCriteria();
            CrazySupplierFlightRequestDTO requestDTO = buildCrazySupplierFlightRequestDTO();

            when(mapper.toCrazySupplierRequestDTO(searchCriteria)).thenReturn(requestDTO);
            mockRequestSerialisation(requestDTO);
            when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new IOException("Network error"));
            
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> crazySupplierService.searchFlights(searchCriteria)
            );

            assertInstanceOf(IOException.class, exception.getCause());
            assertEquals("Network error", exception.getCause().getMessage());

            verify(mapper).toCrazySupplierRequestDTO(searchCriteria);
            verify(objectMapper).writeValueAsString(requestDTO);
            verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
            verify(objectMapper, never()).getTypeFactory();
            verify(mapper, never()).toFlight(any());
        }
    }

    private void mockSuccessfulApiCall(
            CrazySupplierFlightRequestDTO request,
            CrazySupplierFlightResponseDTO responseDTO,
            Flight expectedFlight) throws IOException, InterruptedException {

        // Mock request serialization
        mockRequestSerialisation(request);

        // Mock HTTP response
        HttpResponse<String> mockResponse = mockResponseWithStatusCodeBody(200, MOCK_RESPONSE_BODY);

        // Mock HttpClient.send()
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        // Mock TypeFactory chain
        mockTypeFactory();

        // Mock the readValue method
        when(objectMapper.readValue(any(String.class), any(JavaType.class)))
                .thenReturn(Collections.singletonList(responseDTO));
                
        // Mock mapping to Flight
        when(mapper.toFlight(responseDTO)).thenReturn(expectedFlight);
    }

    private static HttpResponse<String> mockResponseWithStatusCodeBody(int statusCode, String mockResponseBody) {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(statusCode);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        return mockResponse;
    }

    private void mockRequestSerialisation(CrazySupplierFlightRequestDTO request) throws JsonProcessingException {
        when(objectMapper.writeValueAsString(request)).thenReturn(MOCK_REQUEST_BODY);
    }

    private void mockErrorApiCall(CrazySupplierFlightRequestDTO request) throws IOException, InterruptedException {
        // Mock request serialization
        mockRequestSerialisation(request);

        // Mock HTTP response with error status
        HttpResponse<String> mockResponse = mockResponseWithStatusCodeBody(500, ERROR_RESPONSE_BODY);

        // Mock HttpClient.send()
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
    }
    
    private void mockEmptyApiCall(CrazySupplierFlightRequestDTO request) throws IOException, InterruptedException {
        // Mock request serialization
        mockRequestSerialisation(request);

        // Mock HTTP response with empty body
        HttpResponse<String> mockResponse = mockResponseWithStatusCodeBody(200, EMPTY_RESPONSE_BODY);

        // Mock HttpClient.send()
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        // Mock TypeFactory chain
        mockTypeFactory();

        // Mock the readValue method to return empty list
        when(objectMapper.readValue(any(String.class), any(JavaType.class)))
                .thenReturn(Collections.emptyList());
    }

    private void mockTypeFactory() {
        TypeFactory typeFactory = mock(TypeFactory.class);
        when(objectMapper.getTypeFactory()).thenReturn(typeFactory);
        CollectionType collectionType = mock(CollectionType.class);
        when(typeFactory.constructCollectionType(List.class, CrazySupplierFlightResponseDTO.class))
                .thenReturn(collectionType);
    }

    // Helper methods for building test data
    private FlightSearchCriteria buildFlightSearchCriteria() {
        Instant now = Instant.now();
        return FlightSearchCriteria.builder()
                .departureAirport("JFK")
                .destinationAirport("LAX")
                .departureTimeStart(now)
                .departureTimeEnd(now.plus(1, ChronoUnit.DAYS))
                .build();
    }

    private CrazySupplierFlightResponseDTO buildCrazySupplierFlightResponseDTO() {
        Instant now = Instant.now();
        return CrazySupplierFlightResponseDTO.builder()
                .carrier("TestAir")
                .basePrice(100.0)
                .tax(20.0)
                .departureAirport("JFK")
                .arrivalAirport("LAX")
                .outboundDateTime(now)
                .inboundDateTime(now.plus(5, ChronoUnit.HOURS))
                .build();
    }

    private CrazySupplierFlightRequestDTO buildCrazySupplierFlightRequestDTO() {
        Instant now = Instant.now();
        return CrazySupplierFlightRequestDTO.builder()
                .inboundDateTime(now)
                .outboundDateTime(now.plus(1, ChronoUnit.DAYS))
                .fromAirLine("CFG")
                .toAirLine("BLR")
                .build();
    }

    private Flight buildExpectedFlight() {
        Instant now = Instant.now();
        return Flight.builder()
                .airLine("TestAir")
                .supplier("CrazySupplier")
                .fare(120.0)
                .departureAirport("JFK")
                .destinationAirport("LAX")
                .departureTime(now)
                .arrivalTime(now.plus(5, ChronoUnit.HOURS))
                .build();
    }
}
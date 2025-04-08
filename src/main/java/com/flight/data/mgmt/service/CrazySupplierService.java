package com.flight.data.mgmt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight.data.mgmt.dto.CrazySupplierFlightRequestDTO;
import com.flight.data.mgmt.dto.CrazySupplierFlightResponseDTO;
import com.flight.data.mgmt.dto.FlightSearchCriteria;
import com.flight.data.mgmt.mapper.CrazySupplierMapper;
import com.flight.data.mgmt.model.Flight;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CrazySupplierService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final CrazySupplierMapper mapper;
    private String apiUrl;

    public CrazySupplierService(
            HttpClient httpClient,
            ObjectMapper objectMapper,
            CrazySupplierMapper mapper,
            @Value("${crazy-supplier.api.url}") String apiUrl) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.mapper = mapper;
        this.apiUrl = apiUrl;
    }

    public List<Flight> searchFlights(FlightSearchCriteria flightSearchCriteria) {
        CrazySupplierFlightRequestDTO request = mapper.toCrazySupplierRequestDTO(flightSearchCriteria);
        try {
            return fetchFlightsFromExternalApi(request);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected List<Flight> fetchFlightsFromExternalApi(CrazySupplierFlightRequestDTO request) throws IOException, InterruptedException {

        String requestBody = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "/flights"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (httpResponse.statusCode() != 200) {
            throw new RuntimeException(
                    "API call failed with status: " + httpResponse.statusCode()
            );
        }

        List<CrazySupplierFlightResponseDTO> responses = objectMapper.readValue(
                httpResponse.body(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, CrazySupplierFlightResponseDTO.class)
        );

        return responses.stream().map(mapper::toFlight)
                .collect(Collectors.toList());

    }
}


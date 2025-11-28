package com.flightapp.test;


import com.flightapp.controller.FlightController;
import com.flightapp.request.AddFlightRequest;
import com.flightapp.request.SearchFlightRequest;
import com.flightapp.service.FlightService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightController.class)
public class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightService flightService;

    @Test
    void testAddFlight() throws Exception {

        when(flightService.addFlight(org.mockito.ArgumentMatchers.any()))
                .thenReturn(
                        java.util.Map.of(
                                "message", "Flight added successfully",
                                "status", "CREATED"
                        ));

        mockMvc.perform(
                post("/api/v1.0/flight/airline/inventory/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "airlineName": "Indigo",
                                  "source": "Bhubaneswar",
                                  "destination": "Delhi",
                                  "startDate": "2025-12-01T09:00:00",
                                  "endDate": "2025-12-01T11:00:00",
                                  "availableSeats": 120,
                                  "ticketPrice": 4500,
                                  "mealStatus": true
                                }
                                """)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value("Flight added successfully"));
    }


    @Test
    void testSearchFlight() throws Exception {

        when(flightService.searchFlight(org.mockito.ArgumentMatchers.any()))
                .thenReturn(
                        java.util.Map.of(
                                "onwardFlights", java.util.List.of()
                        ));

        mockMvc.perform(
                post("/api/v1.0/flight/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "source": "Bhubaneswar",
                                  "destination": "Delhi",
                                  "journeyDate": "2025-12-01",
                                  "numberOfPassengers": 2,
                                  "roundTrip": false
                                }
                                """)
        )
        .andExpect(status().isOk());
    }
}

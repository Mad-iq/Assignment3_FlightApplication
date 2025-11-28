package com.flightapp.test;


import com.flightapp.controller.BookingController;
import com.flightapp.request.BookingRequest;
import com.flightapp.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void testBookTicket() throws Exception {

        when(bookingService.bookTicket(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(
                java.util.Map.of(
                        "pnr", "PNR10001",
                        "message", "Booking successful",
                        "totalPrice", 9000,
                        "flightId", "FL123"
                )
        );

        mockMvc.perform(
                post("/api/v1.0/flight/booking/FL123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "abc@example.com",
                                  "name": "Debashrita",
                                  "numberOfSeats": 2,
                                  "passengers": [
                                    {"name":"Debashrita","gender":"Female","age":21},
                                    {"name":"Aahana","gender":"Female","age":22}
                                  ],
                                  "mealPreference": "VEG",
                                  "seatNumbers": ["12A","12B"]
                                }
                                """)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.pnr").value("PNR10001"))
        .andExpect(jsonPath("$.message").value("Booking successful"));
    }

    @Test
    void testGetTicketByPnr() throws Exception {

        when(bookingService.getTicketByPnr("PNR10001"))
                .thenReturn(
                        java.util.Map.of(
                            "pnr", "PNR10001",
                            "flightId", "FL123",
                            "email", "user@example.com"
                        )
                );

        mockMvc.perform(get("/api/v1.0/flight/ticket/PNR10001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pnr").value("PNR10001"));
    }

    @Test
    void testCancelBooking() throws Exception {

        when(bookingService.cancelBooking("PNR10001"))
                .thenReturn(
                        java.util.Map.of(
                                "pnr", "PNR10001",
                                "message", "Ticket cancelled successfully"
                        )
                );

        mockMvc.perform(
                delete("/api/v1.0/flight/booking/cancel/PNR10001")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Ticket cancelled successfully"));
    }
}

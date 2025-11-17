package com.flightapp.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddFlightRequest {

    @NotBlank(message = "Airline name is required")
    private String airlineName;

    @NotBlank(message = "Source is required")
    private String source;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotBlank(message = "Start date is required")
    private String startDate;
    
    @NotBlank(message = "End date is required")
    private String endDate;

    @Min(value = 1, message = "Available seats must be at least 1")
    private int availableSeats;

    @Min(value = 1, message = "Ticket price must be positive")
    private double ticketPrice;
    private boolean mealStatus;
}

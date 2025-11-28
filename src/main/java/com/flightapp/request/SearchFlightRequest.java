package com.flightapp.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchFlightRequest {

    @NotBlank(message = "Source is required")
    private String source;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotBlank(message = "Journey date is required (yyyy-mm-dd)")
    private String journeyDate;

    @Min(1)
    private int numberOfPassengers;

    private boolean roundTrip;        
    private String returnDate;        
}

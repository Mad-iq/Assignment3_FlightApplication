package com.flightapp.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class BookingRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @Min(1)
    private int numberOfSeats;

    @NotEmpty
    private List<PassengerRequest> passengers;

    @NotBlank
    private String mealPreference;

    @NotEmpty
    private List<String> seatNumbers;
}

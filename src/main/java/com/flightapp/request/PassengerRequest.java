package com.flightapp.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerRequest {

    @NotBlank(message = "Passenger name is required")
    private String name;

    @NotBlank(message= "Passenger gender is required")
    private String gender;

    @Min(value = 1, message= "Age must be valid")
    private int age;
}

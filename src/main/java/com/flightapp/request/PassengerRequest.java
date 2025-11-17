package com.flightapp.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PassengerRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String gender;

    @Min(value = 1)
    private int age;
}

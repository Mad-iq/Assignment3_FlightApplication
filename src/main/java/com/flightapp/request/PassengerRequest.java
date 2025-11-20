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

    @NotBlank
    private String name;

    @NotBlank
    private String gender;

    @Min(value = 1)
    private int age;
}

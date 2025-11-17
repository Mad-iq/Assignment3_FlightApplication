package com.flightapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Airline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}

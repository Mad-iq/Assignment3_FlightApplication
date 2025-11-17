package com.flightapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String flightId;

    private String source;
    private String destination;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private int availableSeats;
    private double ticketPrice;

    private boolean mealStatus;

    @ManyToOne
    private Airline airline;
}

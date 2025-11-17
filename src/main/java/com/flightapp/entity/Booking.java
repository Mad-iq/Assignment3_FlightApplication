package com.flightapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String pnr;

    private String email;
    private String name;

    private LocalDateTime timeOfBooking;
    private LocalDateTime timeOfJourney;

    private int numberOfSeats;
    private double totalPrice;

    private String mealPreference;

    private boolean cancelled;

    @ManyToOne
    private Flight flight;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Passenger> passengers;
}

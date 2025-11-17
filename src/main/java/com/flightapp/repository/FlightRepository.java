package com.flightapp.repository;

import com.flightapp.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    Flight findByAirline_NameAndSourceAndDestinationAndStartDate(
            String airlineName,
            String fromPlace,
            String toPlace,
            LocalDateTime startDateTime
    );

    List<Flight> findBySourceAndDestinationAndStartDateBetween(
            String from,
            String to,
            LocalDateTime start,
            LocalDateTime end
    );

    Flight findByFlightId(String flightId);
}

package com.flightapp.utility;

import com.flightapp.entity.Airline;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FlightIDGenerator {

    public static String generateFlightId(Airline airline, String from, String to, LocalDateTime start) {
        String airlineCode = airline.getName().replaceAll("\\s+", "").toUpperCase();
        String time = start.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        return airlineCode + "-" + from.toUpperCase() + "-" + to.toUpperCase() + "-" + time;
    }

    public static String generatePnr() {
        return "PNR" + (100000 + (int) (Math.random() * 900000));
    }
}

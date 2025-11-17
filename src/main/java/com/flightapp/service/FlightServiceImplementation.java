package com.flightapp.service;

import com.flightapp.request.AddFlightRequest;
import com.flightapp.request.SearchFlightRequest;
import com.flightapp.entity.Airline;
import com.flightapp.entity.Flight;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.service.FlightService;
import com.flightapp.utility.FlightIDGenerator;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FlightServiceImplementation implements FlightService {

    private final AirlineRepository airlineRepo;
    private final FlightRepository flightRepo;

    @Override
    public Map<String, Object> addFlight(AddFlightRequest req) {

        String airlineName = req.getAirlineName();

        Airline airline = airlineRepo.findByName(airlineName);
        if (airline == null) {
            airline = new Airline();
            airline.setName(airlineName);
            airlineRepo.save(airline);
        }

        LocalDateTime start = LocalDateTime.parse(req.getStartDate());
        LocalDateTime end = LocalDateTime.parse(req.getEndDate());

        Flight duplicate = flightRepo.findByAirline_NameAndSourceAndDestinationAndStartDate(
                airlineName,
                req.getSource(),
                req.getDestination(),
                start
        );

        if (duplicate != null) {
            throw new RuntimeException("Flight already exists for this schedule");
        }

        String flightId = FlightIDGenerator.generateFlightId(
                airline,
                req.getSource(),
                req.getDestination(),
                start
        );

        Flight flight = new Flight();
        flight.setFlightId(flightId);
        flight.setSource(req.getSource());
        flight.setDestination(req.getDestination());
        flight.setStartDate(start);
        flight.setEndDate(end);
        flight.setAvailableSeats(req.getAvailableSeats());
        flight.setTicketPrice(req.getTicketPrice());
        flight.setMealStatus(req.isMealStatus());
        flight.setAirline(airline);

        flightRepo.save(flight);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("message", "Flight added successfully");
        resp.put("flightId", flightId);
        resp.put("status", "CREATED");

        return resp;
    }

    @Override
    public Map<String, Object> searchFlight(SearchFlightRequest req) {

        String source = req.getSource();
        String destination = req.getDestination();
        LocalDate journeyDate = LocalDate.parse(req.getJourneyDate());

   
        List<Flight> onwardFlights = flightRepo.findBySourceAndDestinationAndStartDateBetween(
                source,
                destination,
                journeyDate.atStartOfDay(),
                journeyDate.atTime(23, 59)
        );

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("onwardFlights", mapFlights(onwardFlights));

        if (req.isRoundTrip()) {

            if (req.getReturnDate() == null || req.getReturnDate().isBlank()) {
                throw new RuntimeException("Return date is required for round trip search");
            }

            LocalDate returnDate = LocalDate.parse(req.getReturnDate());

            List<Flight> returnFlights = flightRepo.findBySourceAndDestinationAndStartDateBetween(
                    destination,  
                    source,       
                    returnDate.atStartOfDay(),
                    returnDate.atTime(23, 59)
            );

            resp.put("returnFlights", mapFlights(returnFlights));
        }

        return resp;
    }

    private List<Map<String, Object>> mapFlights(List<Flight> flights) {

        List<Map<String, Object>> list = new ArrayList<>();

        for (Flight f : flights) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("flightId", f.getFlightId());
            m.put("airlineName", f.getAirline().getName());
            m.put("dateTime", f.getStartDate().toString());
            m.put("price", f.getTicketPrice());
            list.add(m);
        }

        return list;
    }
}

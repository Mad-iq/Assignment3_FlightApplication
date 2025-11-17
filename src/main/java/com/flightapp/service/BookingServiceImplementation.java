package com.flightapp.service;

import com.flightapp.request.BookingRequest;
import com.flightapp.request.PassengerRequest;
import com.flightapp.entity.Booking;
import com.flightapp.entity.Flight;
import com.flightapp.entity.Passenger;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.service.BookingService;
import com.flightapp.utility.FlightIDGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImplementation implements BookingService {

    private final FlightRepository flightRepo;
    private final BookingRepository bookingRepo;

    @Override
    @Transactional
    public Map<String, Object> bookTicket(String flightId, BookingRequest req) {

        Flight flight = flightRepo.findByFlightId(flightId);
        if (flight == null) {
            throw new RuntimeException("Flight not found");
        }

        int numberOfSeats = req.getNumberOfSeats();
        if (numberOfSeats <= 0) {
            throw new RuntimeException("Invalid numberOfSeats");
        }

        if (flight.getAvailableSeats() < numberOfSeats) {
            throw new RuntimeException("Not enough seats available");
        }

        List<Passenger> passengers = new ArrayList<>();
        if (req.getPassengers() != null) {
            for (PassengerRequest pr : req.getPassengers()) {
                Passenger p = new Passenger();
                p.setName(pr.getName());
                p.setGender(pr.getGender());
                p.setAge(pr.getAge());
                passengers.add(p);
            }
        }

        List<String> seatNumbers = req.getSeatNumbers();
        if (seatNumbers != null && seatNumbers.size() == passengers.size()) {
            for (int i = 0; i < passengers.size(); i++) {
                passengers.get(i).setSeatNumber(seatNumbers.get(i));
            }
        }

        double totalPrice = flight.getTicketPrice() * numberOfSeats;

        Booking booking = new Booking();
        booking.setPnr(FlightIDGenerator.generatePnr());
        booking.setEmail(req.getEmail());
        booking.setName(req.getName());
        booking.setTimeOfBooking(LocalDateTime.now());
        booking.setTimeOfJourney(flight.getStartDate());
        booking.setNumberOfSeats(numberOfSeats);
        booking.setTotalPrice(totalPrice);
        booking.setMealPreference(req.getMealPreference());
        booking.setCancelled(false);
        booking.setFlight(flight);
        booking.setPassengers(passengers);

        flight.setAvailableSeats(flight.getAvailableSeats() - numberOfSeats);
        flightRepo.save(flight);

        bookingRepo.save(booking);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("pnr", booking.getPnr());
        resp.put("message", "Booking successful");
        resp.put("totalPrice", booking.getTotalPrice());
        resp.put("flightId", flight.getFlightId());

        return resp;
    }

    @Override
    public Map<String, Object> getTicketByPnr(String pnr) {
        Booking b = bookingRepo.findByPnr(pnr);
        if (b == null) {
            throw new RuntimeException("Booking not found");
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("pnr", b.getPnr());
        resp.put("flightId", b.getFlight().getFlightId());
        resp.put("airlineName", b.getFlight().getAirline().getName());
        resp.put("journeyDateTime", b.getTimeOfJourney().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        resp.put("source", b.getFlight().getSource());
        resp.put("destination", b.getFlight().getDestination());

        List<Map<String, Object>> passengers = new ArrayList<>();
        if (b.getPassengers() != null) {
            for (Passenger p : b.getPassengers()) {
                Map<String, Object> pm = new LinkedHashMap<>();
                pm.put("name", p.getName());
                pm.put("gender", p.getGender());
                pm.put("age", p.getAge());
                passengers.add(pm);
            }
        }
        resp.put("passengers", passengers);

        resp.put("mealPreference", b.getMealPreference());

        List<String> seats = new ArrayList<>();
        if (b.getPassengers() != null) {
            for (Passenger p : b.getPassengers()) {
                if (p.getSeatNumber() != null) seats.add(p.getSeatNumber());
            }
        }
        resp.put("seatNumbers", seats);
        resp.put("email", b.getEmail());

        return resp;
    }

    @Override
    public Map<String, Object> getBookingHistory(String email) {
        List<Booking> bookings = bookingRepo.findByEmail(email);
        List<Map<String, Object>> history = new ArrayList<>();

        for (Booking b : bookings) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("pnr", b.getPnr());
            item.put("flightId", b.getFlight().getFlightId());
            item.put("date", b.getTimeOfJourney().toLocalDate().toString());
            item.put("status", b.isCancelled() ? "CANCELLED" : "BOOKED");
            history.add(item);
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("email", email);
        resp.put("history", history);

        return resp;
    }

    @Override
    @Transactional
    public Map<String, Object> cancelBooking(String pnr) {
        Booking b = bookingRepo.findByPnr(pnr);
        if (b == null) {
            throw new RuntimeException("Booking not found");
        }

        if (b.isCancelled()) {
            throw new RuntimeException("Booking already cancelled");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = b.getTimeOfJourney().minusHours(24);

        if (!now.isBefore(deadline)) {
            throw new RuntimeException("Cannot cancel booking less than 24 hours before journey");
        }

        b.setCancelled(true);
        bookingRepo.save(b);

        Flight f = b.getFlight();
        f.setAvailableSeats(f.getAvailableSeats() + b.getNumberOfSeats());
        flightRepo.save(f);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("pnr", pnr);
        resp.put("message", "Ticket cancelled successfully");

        return resp;
    }
}

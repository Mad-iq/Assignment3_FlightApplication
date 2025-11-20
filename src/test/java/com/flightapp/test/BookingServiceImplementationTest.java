package com.flightapp.test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.flightapp.entity.Airline;
import com.flightapp.entity.Booking;
import com.flightapp.entity.Flight;
import com.flightapp.entity.Passenger;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.request.BookingRequest;
import com.flightapp.request.PassengerRequest;
import com.flightapp.service.BookingServiceImplementation;

class BookingServiceImplementationTest {

    @Mock
    private FlightRepository flightRepo;

    @Mock
    private BookingRepository bookingRepo;

    @InjectMocks
    private BookingServiceImplementation bookingService;

    private Flight flight;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        flight = new Flight();
        flight.setFlightId("F123");
        flight.setAvailableSeats(10);
        flight.setTicketPrice(1000);
        flight.setStartDate(LocalDateTime.now().plusDays(2));
    }

    // ---------- BOOK TICKET TESTS ---------- //

    @Test
    void testBookTicket_Success() {
        when(flightRepo.findByFlightId("F123")).thenReturn(flight);

        BookingRequest req = new BookingRequest();
        req.setEmail("test@mail.com");
        req.setName("John");
        req.setMealPreference("Veg");
        req.setNumberOfSeats(2);

        PassengerRequest p1 = new PassengerRequest("A", "Female", 22);
        PassengerRequest p2 = new PassengerRequest("B", "Male", 25);
        req.setPassengers(Arrays.asList(p1, p2));
        req.setSeatNumbers(Arrays.asList("1A", "1B"));

        Map<String, Object> result = bookingService.bookTicket("F123", req);

        assertNotNull(result.get("pnr"));
        assertEquals("Booking successful", result.get("message"));
        assertEquals(2000.0, result.get("totalPrice"));

        verify(bookingRepo, times(1)).save(any());
    }

    @Test
    void testBookTicket_FlightNotFound() {
        when(flightRepo.findByFlightId("X")).thenReturn(null);

        BookingRequest req = new BookingRequest();
        req.setNumberOfSeats(1);

        assertThrows(RuntimeException.class,
                () -> bookingService.bookTicket("X", req));
    }

    @Test
    void testBookTicket_InvalidSeatCount() {
        when(flightRepo.findByFlightId("F123")).thenReturn(flight);

        BookingRequest req = new BookingRequest();
        req.setNumberOfSeats(0);

        assertThrows(RuntimeException.class,
                () -> bookingService.bookTicket("F123", req));
    }

    @Test
    void testBookTicket_NotEnoughSeats() {
        flight.setAvailableSeats(1);
        when(flightRepo.findByFlightId("F123")).thenReturn(flight);

        BookingRequest req = new BookingRequest();
        req.setNumberOfSeats(5);

        assertThrows(RuntimeException.class,
                () -> bookingService.bookTicket("F123", req));
    }

    // ---------- GET TICKET BY PNR ---------- //

    @Test
    void testGetTicketByPnr_Success() {
        Booking booking = new Booking();
        booking.setPnr("PNR123");
        booking.setTimeOfJourney(LocalDateTime.now());
        booking.setEmail("mail@mail.com");

        Flight f = new Flight();
        Airline airline = new Airline();
        airline.setName("Air India");

        f.setFlightId("F123");
        f.setAirline(airline);
        f.setSource("DEL");
        f.setDestination("BLR");
        booking.setFlight(f);

        Passenger p = new Passenger();
        p.setName("Alex");
        p.setGender("Male");
        p.setAge(30);

        booking.setPassengers(List.of(p));

        when(bookingRepo.findByPnr("PNR123")).thenReturn(booking);

        Map<String, Object> resp = bookingService.getTicketByPnr("PNR123");

        assertEquals("F123", resp.get("flightId"));
        assertEquals("DEL", resp.get("source"));
        assertEquals("BLR", resp.get("destination"));
    }

    @Test
    void testGetTicketByPnr_NotFound() {
        when(bookingRepo.findByPnr("X")).thenReturn(null);
        assertThrows(RuntimeException.class,
                () -> bookingService.getTicketByPnr("X"));
    }

    // ---------- BOOKING HISTORY ---------- //

    @Test
    void testGetBookingHistory() {
        Booking b1 = new Booking();
        b1.setPnr("PNR1");
        b1.setCancelled(false);
        b1.setTimeOfJourney(LocalDateTime.now());
        b1.setFlight(flight);

        when(bookingRepo.findByEmail("abc@mail.com")).thenReturn(List.of(b1));

        Map<String, Object> resp = bookingService.getBookingHistory("abc@mail.com");

        List<?> history = (List<?>) resp.get("history");
        assertEquals(1, history.size());
    }

    // ---------- CANCEL BOOKING ---------- //

    @Test
    void testCancelBooking_Success() {
        Booking b = new Booking();
        b.setPnr("PNR1");
        b.setCancelled(false);
        b.setNumberOfSeats(2);
        b.setFlight(flight);
        b.setTimeOfJourney(LocalDateTime.now().plusDays(2));

        when(bookingRepo.findByPnr("PNR1")).thenReturn(b);

        Map<String, Object> resp = bookingService.cancelBooking("PNR1");

        assertEquals("Ticket cancelled successfully", resp.get("message"));
        verify(bookingRepo, times(1)).save(b);
    }

    @Test
    void testCancelBooking_NotFound() {
        when(bookingRepo.findByPnr("X")).thenReturn(null);
        assertThrows(RuntimeException.class,
                () -> bookingService.cancelBooking("X"));
    }

    @Test
    void testCancelBooking_AlreadyCancelled() {
        Booking b = new Booking();
        b.setCancelled(true);
        when(bookingRepo.findByPnr("P")).thenReturn(b);

        assertThrows(RuntimeException.class,
                () -> bookingService.cancelBooking("P"));
    }

    @Test
    void testCancelBooking_LessThan24Hours() {
        Booking b = new Booking();
        b.setCancelled(false);
        b.setTimeOfJourney(LocalDateTime.now().plusHours(5));
        when(bookingRepo.findByPnr("P")).thenReturn(b);

        assertThrows(RuntimeException.class,
                () -> bookingService.cancelBooking("P"));
    }
}

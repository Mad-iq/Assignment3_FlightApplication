package com.flightapp.test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.flightapp.entity.Airline;
import com.flightapp.entity.Flight;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.request.AddFlightRequest;
import com.flightapp.request.SearchFlightRequest;
import com.flightapp.service.FlightServiceImplementation;

class FlightServiceImplementationTest {

    @Mock
    private AirlineRepository airlineRepo;

    @Mock
    private FlightRepository flightRepo;

    @InjectMocks
    private FlightServiceImplementation flightService;

    private Airline airline;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        airline = new Airline();
        airline.setName("Indigo");
    }

    // ---------- ADD FLIGHT ---------- //

    @Test
    void testAddFlight_Success() {
        when(airlineRepo.findByName("Indigo")).thenReturn(airline);
        when(flightRepo.findByAirline_NameAndSourceAndDestinationAndStartDate(
                any(), any(), any(), any()
        )).thenReturn(null);

        AddFlightRequest req = new AddFlightRequest();
        req.setAirlineName("Indigo");
        req.setSource("DEL");
        req.setDestination("BLR");
        req.setStartDate(LocalDateTime.now().plusDays(1).toString());
        req.setEndDate(LocalDateTime.now().plusDays(1).plusHours(2).toString());
        req.setAvailableSeats(150);
        req.setTicketPrice(5000);
        req.setMealStatus(true);

        Map<String, Object> resp = flightService.addFlight(req);

        assertEquals("Flight added successfully", resp.get("message"));
        verify(flightRepo, times(1)).save(any());
    }

    @Test
    void testAddFlight_NewAirlineCreated() {
        when(airlineRepo.findByName("NewAir")).thenReturn(null);

        AddFlightRequest req = new AddFlightRequest();
        req.setAirlineName("NewAir");
        req.setSource("DEL");
        req.setDestination("BLR");
        req.setStartDate(LocalDateTime.now().toString());
        req.setEndDate(LocalDateTime.now().plusHours(2).toString());
        req.setAvailableSeats(100);
        req.setTicketPrice(6000);

        flightService.addFlight(req);

        verify(airlineRepo, times(1)).save(any());
    }

    @Test
    void testAddFlight_Duplicate() {
        when(airlineRepo.findByName("Indigo")).thenReturn(airline);
        when(flightRepo.findByAirline_NameAndSourceAndDestinationAndStartDate(any(), any(), any(), any()))
                .thenReturn(new Flight());

        AddFlightRequest req = new AddFlightRequest();
        req.setAirlineName("Indigo");
        req.setSource("DEL");
        req.setDestination("BLR");
        req.setStartDate(LocalDateTime.now().toString());
        req.setEndDate(LocalDateTime.now().toString());

        assertThrows(RuntimeException.class,
                () -> flightService.addFlight(req));
    }

    // ---------- SEARCH FLIGHT ---------- //

    @Test
    void testSearchFlight_OneWay() {
        Flight f = new Flight();
        f.setFlightId("F1");
        f.setAirline(airline);
        f.setTicketPrice(3000);
        f.setStartDate(LocalDateTime.now());

        when(flightRepo.findBySourceAndDestinationAndStartDateBetween(any(), any(), any(), any()))
                .thenReturn(List.of(f));

        SearchFlightRequest req = new SearchFlightRequest();
        req.setSource("DEL");
        req.setDestination("BLR");
        req.setJourneyDate(LocalDate.now().toString());
        req.setRoundTrip(false);

        Map<String, Object> resp = flightService.searchFlight(req);

        assertTrue(((List<?>) resp.get("onwardFlights")).size() > 0);
    }

    @Test
    void testSearchFlight_RoundTripMissingReturnDate() {
        SearchFlightRequest req = new SearchFlightRequest();
        req.setSource("DEL");
        req.setDestination("BLR");
        req.setJourneyDate(LocalDate.now().toString());
        req.setRoundTrip(true);

        assertThrows(RuntimeException.class,
                () -> flightService.searchFlight(req));
    }

    @Test
    void testSearchFlight_RoundTripSuccess() {
        Flight onward = new Flight();
        onward.setFlightId("O1");
        onward.setAirline(airline);
        onward.setStartDate(LocalDateTime.now());

        Flight ret = new Flight();
        ret.setFlightId("R1");
        ret.setAirline(airline);
        ret.setStartDate(LocalDateTime.now().plusDays(1));

        when(flightRepo.findBySourceAndDestinationAndStartDateBetween(
                any(), any(), any(), any()
        )).thenReturn(List.of(onward), List.of(ret));

        SearchFlightRequest req = new SearchFlightRequest();
        req.setSource("DEL");
        req.setDestination("BLR");
        req.setJourneyDate(LocalDate.now().toString());
        req.setReturnDate(LocalDate.now().plusDays(1).toString());
        req.setRoundTrip(true);

        Map<String, Object> resp = flightService.searchFlight(req);

        assertNotNull(resp.get("returnFlights"));
    }
}

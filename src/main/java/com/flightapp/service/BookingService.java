package com.flightapp.service;

import com.flightapp.request.BookingRequest;

import java.util.Map;

public interface BookingService {

    Map<String, Object> bookTicket(String flightId, BookingRequest req);

    Map<String, Object> getTicketByPnr(String pnr);

    Map<String, Object> getBookingHistory(String email);

    Map<String, Object> cancelBooking(String pnr);
}

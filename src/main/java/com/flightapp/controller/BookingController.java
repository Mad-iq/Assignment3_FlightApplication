package com.flightapp.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightapp.request.BookingRequest;
import com.flightapp.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1.0/flight")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/booking/{flightId}")
    public ResponseEntity<?> bookTicket(
            @PathVariable("flightId") String flightId,
            @Valid @RequestBody BookingRequest request) {
        Map<String, Object> resp = bookingService.bookTicket(flightId, request);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    @GetMapping("/ticket/{pnr}")
    public ResponseEntity<?> getTicket(@PathVariable("pnr") String pnr) {
        Map<String, Object> resp = bookingService.getTicketByPnr(pnr);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/booking/history/{emailId}")
    public ResponseEntity<?> getHistory(@PathVariable("emailId") String emailId) {
        Map<String, Object> resp = bookingService.getBookingHistory(emailId);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/booking/cancel/{pnr}")
    public ResponseEntity<?> cancelBooking(@PathVariable("pnr") String pnr) {
        Map<String, Object> resp = bookingService.cancelBooking(pnr);
        return ResponseEntity.ok(resp);
    }
}

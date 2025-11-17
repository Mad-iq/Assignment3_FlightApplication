package com.flightapp.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightapp.request.AddFlightRequest;
import com.flightapp.request.SearchFlightRequest;
import com.flightapp.service.FlightService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1.0/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping("/airline/inventory/add")
    public ResponseEntity<?> addFlight(@Valid @RequestBody AddFlightRequest request) {
        Map<String, Object> resp = flightService.addFlight(request);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@Valid @RequestBody SearchFlightRequest request) {
        Map<String, Object> resp = flightService.searchFlight(request);
        return ResponseEntity.ok(resp);
    }
}

package com.flightapp.service;

import com.flightapp.request.AddFlightRequest;
import com.flightapp.request.SearchFlightRequest;

import java.util.Map;

public interface FlightService {

    Map<String, Object> addFlight(AddFlightRequest req);

    Map<String, Object> searchFlight(SearchFlightRequest req);
}

package com.flightapp.repository;

import com.flightapp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findByPnr(String pnr);
    List<Booking> findByEmail(String email);
}

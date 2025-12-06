package com.example.restaurant.service;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Value("${restaurant.opening-time:10:00}")
    private String openingTime;

    @Value("${restaurant.closing-time:22:00}")
    private String closingTime;

    @Value("${restaurant.max-capacity:50}")
    private int maxCapacity;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Reservation createReservation(Reservation reservation) {
        validateReservation(reservation);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsForDate(LocalDate date) {
        return reservationRepository.findByReservationDate(date);
    }

    public List<Reservation> getUpcomingReservations() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        return reservationRepository.findUpcomingReservations(today, now);
    }

    @Transactional
    public void confirmReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        reservation.setConfirmed(true);
        reservationRepository.save(reservation);
    }

    @Transactional
    public void cancelReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    private void validateReservation(Reservation reservation) {
        LocalDate reservationDate = reservation.getReservationDate();
        LocalTime reservationTime = reservation.getReservationTime();
        
        // Check if reservation is for future date/time
        if (reservationDate.isBefore(LocalDate.now()) || 
            (reservationDate.isEqual(LocalDate.now()) && reservationTime.isBefore(LocalTime.now()))) {
            throw new IllegalArgumentException("Reservation must be for a future date and time");
        }

        // Check restaurant operating hours
        LocalTime opening = LocalTime.parse(openingTime);
        LocalTime closing = LocalTime.parse(closingTime);
        if (reservationTime.isBefore(opening) || reservationTime.isAfter(closing.minusHours(2))) {
            throw new IllegalArgumentException("Reservation time must be within operating hours");
        }

        // Check capacity for the time slot
        LocalTime endTime = reservationTime.plusHours(2); // Assuming 2-hour dining duration
        
        // Get all reservations for the day
        List<Reservation> daysReservations = reservationRepository.findByReservationDate(reservationDate);
        
        // Filter for overlapping reservations
        List<Reservation> overlappingReservations = daysReservations.stream()
            .filter(r -> {
                LocalTime rEnd = r.getReservationTime().plusHours(2);
                return !(rEnd.isBefore(reservationTime) || r.getReservationTime().isAfter(endTime));
            })
            .toList();
        
        int totalPeople = overlappingReservations.stream()
            .mapToInt(Reservation::getNumberOfPeople)
            .sum() + reservation.getNumberOfPeople();
            
        if (totalPeople > maxCapacity) {
            throw new IllegalStateException("No available tables for the selected time");
        }
    }

    public List<LocalTime> getAvailableTimeSlots(LocalDate date, int partySize) {
        // Implementation to get available time slots for a given date and party size
        // This is a simplified version - you might want to enhance it
        return List.of(
            LocalTime.of(11, 0),
            LocalTime.of(13, 0),
            LocalTime.of(15, 0),
            LocalTime.of(17, 0),
            LocalTime.of(19, 0)
        );
    }
}

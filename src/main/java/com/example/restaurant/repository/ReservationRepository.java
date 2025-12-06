package com.example.restaurant.repository;

import com.example.restaurant.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    List<Reservation> findByReservationDate(LocalDate date);
    
    List<Reservation> findByReservationDateAndReservationTimeBetween(
        LocalDate date, 
        LocalTime startTime, 
        LocalTime endTime
    );
    
    @Query("SELECT r FROM Reservation r WHERE r.reservationDate = :date " +
           "AND r.reservationTime BETWEEN :startTime AND :endTime")
    List<Reservation> findOverlappingReservations(
        @Param("date") LocalDate date,
        @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime
    );
    
    long countByReservationDate(LocalDate date);
    
    @Query("SELECT r FROM Reservation r WHERE r.reservationDate > :date OR (r.reservationDate = :date AND r.reservationTime > :time) ORDER BY r.reservationDate, r.reservationTime")
    List<Reservation> findUpcomingReservations(@Param("date") LocalDate date, @Param("time") LocalTime time);
}

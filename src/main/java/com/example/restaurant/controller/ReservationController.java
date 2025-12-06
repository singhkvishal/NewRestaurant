package com.example.restaurant.controller;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Value("${restaurant.opening-time:10:00}")
    private String openingTime;

    @Value("${restaurant.closing-time:22:00}")
    private String closingTime;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public String showReservationForm(@RequestParam(required = false) String date,
                                    @RequestParam(required = false) String time,
                                    Model model) {
        if (!model.containsAttribute("reservation")) {
            Reservation reservation = new Reservation();
            if (date != null && !date.isEmpty()) {
                reservation.setReservationDate(LocalDate.parse(date, dateFormatter));
            }
            if (time != null && !time.isEmpty()) {
                reservation.setReservationTime(LocalTime.parse(time, timeFormatter));
            }
            model.addAttribute("reservation", reservation);
        }
        
        // Add available time slots for the next 7 days
        LocalDate now = LocalDate.now();
        LocalDate startDate = now;
        LocalDate endDate = startDate.plusDays(7);
        
        // Add necessary date utilities to the model
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("openingTime", LocalTime.parse(openingTime));
        model.addAttribute("closingTime", LocalTime.parse(closingTime));
        model.addAttribute("currentDate", now);
        model.addAttribute("dateFormatter", dateFormatter);
        
        return "reservations/booking";
    }

    @PostMapping
    public String submitReservation(@Valid @ModelAttribute("reservation") Reservation reservation,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.reservation", result);
            redirectAttributes.addFlashAttribute("reservation", reservation);
            return "redirect:/reservations";
        }

        try {
            // Save the reservation temporarily in session
            redirectAttributes.addFlashAttribute("pendingReservation", reservation);
            
            // Calculate deposit amount (e.g., $10 per person)
            double depositAmount = 10.0 * reservation.getNumberOfPeople();
            
            // Redirect to payment page
            return "redirect:/payment/checkout?amount=" + depositAmount;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing your reservation: " + e.getMessage());
            redirectAttributes.addFlashAttribute("reservation", reservation);
            return "redirect:/reservations";
        }
    }

    @GetMapping("/confirmation")
    public String showConfirmation() {
        return "reservations/confirmation";
    }

    @GetMapping("/check-availability")
    @ResponseBody
    public List<LocalTime> checkAvailability(
            @RequestParam String date,
            @RequestParam int partySize) {
        LocalDate reservationDate = LocalDate.parse(date, dateFormatter);
        return reservationService.getAvailableTimeSlots(reservationDate, partySize);
    }

    @GetMapping("/manage")
    public String manageReservations(Model model) {
        try {
            List<Reservation> upcomingReservations = reservationService.getUpcomingReservations();
            model.addAttribute("reservations", upcomingReservations != null ? upcomingReservations : List.of());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading reservations: " + e.getMessage());
            model.addAttribute("reservations", List.of());
        }
        return "reservations/manage";
    }

    @PostMapping("/{id}/confirm")
    public String confirmReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.confirmReservation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation confirmed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error confirming reservation: " + e.getMessage());
        }
        return "redirect:/reservations/manage";
    }

    @PostMapping("/{id}/cancel")
    public String cancelReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.cancelReservation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error cancelling reservation: " + e.getMessage());
        }
        return "redirect:/reservations/manage";
    }
}

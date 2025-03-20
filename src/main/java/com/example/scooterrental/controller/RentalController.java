package com.example.scooterrental.controller;

import com.example.scooterrental.dto.RentalDto;
import com.example.scooterrental.dto.RentalInfoDto;
import com.example.scooterrental.exception.RentalNotFoundException;
import com.example.scooterrental.exception.ScooterNotFoundException;
import com.example.scooterrental.exception.UserNotFoundException;
import com.example.scooterrental.service.RentalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<RentalDto> createRental(@Valid @RequestBody RentalDto rentalDto)
            throws UserNotFoundException, ScooterNotFoundException {
        RentalDto createdRental = rentalService.createRental(rentalDto);
        return new ResponseEntity<>(createdRental, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<RentalInfoDto> getRentalById(@PathVariable Long id)
            throws RentalNotFoundException {
        RentalInfoDto rental = rentalService.getRentalById(id);
        return new ResponseEntity<>(rental, HttpStatus.OK);
    }

    @PutMapping("/{id}/end")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<RentalDto> endRental(@PathVariable Long id)
            throws RentalNotFoundException, ScooterNotFoundException {
        RentalDto endRental = rentalService.endRental(id);
        return new ResponseEntity<>(endRental, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<RentalInfoDto>> getAllRentals() {
        List<RentalInfoDto> rentals = rentalService.getAllRentals();
        return new ResponseEntity<>(rentals, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<RentalInfoDto>> getRentalsByUser(@PathVariable Long userId) {
        List<RentalInfoDto> rentals = rentalService.getRentalsByUser(userId);
        return new ResponseEntity<>(rentals, HttpStatus.OK);
    }

    @GetMapping("/scooter/{scooterId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<RentalInfoDto>> getRentalsByScooter(@PathVariable Long scooterId) {
        List<RentalInfoDto> rentals = rentalService.getRentalsByScooter(scooterId);
        return new ResponseEntity<>(rentals, HttpStatus.OK);
    }

    @GetMapping("/scooter/{scooterId}/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RentalInfoDto>> getRentalHistoryByScooter(
            @PathVariable Long scooterId) {
        List<RentalInfoDto> rentals = rentalService.getRentalHistoryByScooter(scooterId);
        return new ResponseEntity<>(rentals, HttpStatus.OK);
    }
}

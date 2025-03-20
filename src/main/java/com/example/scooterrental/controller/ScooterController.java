package com.example.scooterrental.controller;

import com.example.scooterrental.dto.ScooterDto;
import com.example.scooterrental.dto.ScooterInfoDto;
import com.example.scooterrental.exception.RentalPointNotFoundException;
import com.example.scooterrental.exception.ScooterNotFoundException;
import com.example.scooterrental.model.ScooterStatus;
import com.example.scooterrental.service.ScooterService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scooters")
public class ScooterController {

    private final ScooterService scooterService;

    @Autowired
    public ScooterController(ScooterService scooterService) {
        this.scooterService = scooterService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ScooterDto> createScooter(@Valid @RequestBody ScooterDto scooterDto)
            throws RentalPointNotFoundException {
        ScooterDto createdScooter = scooterService.createScooter(scooterDto);
        return new ResponseEntity<>(createdScooter, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScooterInfoDto> getScooterById(@PathVariable Long id)
            throws ScooterNotFoundException {
        ScooterInfoDto scooter = scooterService.getScooterById(id);
        return new ResponseEntity<>(scooter, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<ScooterDto> updateScooter(
            @PathVariable Long id, @Valid @RequestBody ScooterDto scooterDto)
            throws ScooterNotFoundException, RentalPointNotFoundException {
        ScooterDto updatedScooter = scooterService.updateScooter(id, scooterDto);
        return new ResponseEntity<>(updatedScooter, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteScooter(@PathVariable Long id)
            throws ScooterNotFoundException {
        scooterService.deleteScooter(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<ScooterInfoDto>> getAllScooters() {
        List<ScooterInfoDto> scooters = scooterService.getAllScooters();
        return new ResponseEntity<>(scooters, HttpStatus.OK);
    }

    @GetMapping("/rental-point/{rentalPointId}")
    public ResponseEntity<List<ScooterInfoDto>> getScootersByRentalPoint(
            @PathVariable Long rentalPointId) {
        List<ScooterInfoDto> scooters = scooterService.getScootersByRentalPoint(rentalPointId);
        return new ResponseEntity<>(scooters, HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Void> updateScooterStatus(
            @PathVariable Long id, @RequestParam ScooterStatus newStatus)
            throws ScooterNotFoundException {
        scooterService.updateScooterStatus(id, newStatus);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

package com.example.scooterrental.controller;

import com.example.scooterrental.dto.RentalPointDto;
import com.example.scooterrental.dto.RentalPointInfoDto;
import com.example.scooterrental.exception.RentalPointNotFoundException;
import com.example.scooterrental.service.RentalPointService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental-points")
public class RentalPointController {

    private final RentalPointService rentalPointService;

    @Autowired
    public RentalPointController(RentalPointService rentalPointService) {
        this.rentalPointService = rentalPointService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<RentalPointDto> createRentalPoint(
            @Valid @RequestBody RentalPointDto rentalPointDto) throws RentalPointNotFoundException {
        RentalPointDto createdRentalPoint = rentalPointService.createRentalPoint(rentalPointDto);
        return new ResponseEntity<>(createdRentalPoint, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalPointInfoDto> getRentalPointById(@PathVariable Long id)
            throws RentalPointNotFoundException {
        RentalPointInfoDto rentalPoint = rentalPointService.getRentalPointById(id);
        return new ResponseEntity<>(rentalPoint, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<RentalPointDto> updateRentalPoint(
            @PathVariable Long id, @Valid @RequestBody RentalPointDto rentalPointDto)
            throws RentalPointNotFoundException {
        RentalPointDto updatedRentalPoint =
                rentalPointService.updateRentalPoint(id, rentalPointDto);
        return new ResponseEntity<>(updatedRentalPoint, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRentalPoint(@PathVariable Long id)
            throws RentalPointNotFoundException {
        rentalPointService.deleteRentalPoint(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<RentalPointInfoDto>> getAllRentalPoints() {
        List<RentalPointInfoDto> rentalPoints = rentalPointService.getAllRentalPoints();
        return new ResponseEntity<>(rentalPoints, HttpStatus.OK);
    }

    @GetMapping("/root")
    public ResponseEntity<List<RentalPointInfoDto>> getRootRentalPoints() {
        List<RentalPointInfoDto> rootRentalPoints = rentalPointService.getRootRentalPoints();
        return new ResponseEntity<>(rootRentalPoints, HttpStatus.OK);
    }

    @GetMapping("/{id}/children")
    public ResponseEntity<List<RentalPointInfoDto>> getChildRentalPoints(@PathVariable Long id)
            throws RentalPointNotFoundException {
        List<RentalPointInfoDto> childRentalPoints = rentalPointService.getChildRentalPoints(id);
        return new ResponseEntity<>(childRentalPoints, HttpStatus.OK);
    }
}

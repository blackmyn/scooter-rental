package com.example.scooterrental.controller;

import com.example.scooterrental.dto.TariffDto;
import com.example.scooterrental.exception.TariffNotFoundException;
import com.example.scooterrental.service.TariffService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tariffs")
public class TariffController {

    private final TariffService tariffService;

    @Autowired
    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<TariffDto> createTariff(@Valid @RequestBody TariffDto tariffDto) {
        TariffDto createdTariff = tariffService.createTariff(tariffDto);
        return new ResponseEntity<>(createdTariff, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TariffDto> getTariffById(@PathVariable Long id)
            throws TariffNotFoundException {
        TariffDto tariff = tariffService.getTariffById(id);
        return new ResponseEntity<>(tariff, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<TariffDto> updateTariff(
            @PathVariable Long id, @Valid @RequestBody TariffDto tariffDto)
            throws TariffNotFoundException {
        TariffDto updatedTariff = tariffService.updateTariff(id, tariffDto);
        return new ResponseEntity<>(updatedTariff, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTariff(@PathVariable Long id) throws TariffNotFoundException {
        tariffService.deleteTariff(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<TariffDto>> getAllTariffs() {
        List<TariffDto> tariffs = tariffService.getAllTariffs();
        return new ResponseEntity<>(tariffs, HttpStatus.OK);
    }
}

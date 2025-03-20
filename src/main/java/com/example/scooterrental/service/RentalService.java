package com.example.scooterrental.service;

import com.example.scooterrental.dto.RentalDto;
import com.example.scooterrental.dto.RentalInfoDto;
import com.example.scooterrental.exception.RentalNotFoundException;
import com.example.scooterrental.exception.ScooterNotFoundException;
import com.example.scooterrental.exception.TariffNotFoundException;
import com.example.scooterrental.exception.UserNotFoundException;

import java.util.List;

public interface RentalService {
    RentalDto createRental(RentalDto rentalDto)
            throws UserNotFoundException, ScooterNotFoundException;

    RentalInfoDto getRentalById(Long id) throws RentalNotFoundException;

    RentalDto endRental(Long id)
            throws RentalNotFoundException, ScooterNotFoundException, TariffNotFoundException;

    List<RentalInfoDto> getAllRentals();

    List<RentalInfoDto> getRentalsByUser(Long userId);

    List<RentalInfoDto> getRentalsByScooter(Long scooterId);

    List<RentalInfoDto> getRentalHistoryByScooter(Long scooterId);
}

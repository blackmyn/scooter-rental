package com.example.scooterrental.service;

import com.example.scooterrental.dto.ScooterDto;
import com.example.scooterrental.dto.ScooterInfoDto;
import com.example.scooterrental.exception.RentalPointNotFoundException;
import com.example.scooterrental.exception.ScooterNotFoundException;
import com.example.scooterrental.exception.TariffNotFoundException;
import com.example.scooterrental.model.ScooterStatus;

import java.util.List;

public interface ScooterService {
    ScooterDto createScooter(ScooterDto scooterDto)
            throws RentalPointNotFoundException, TariffNotFoundException;

    ScooterInfoDto getScooterById(Long id) throws ScooterNotFoundException;

    ScooterDto updateScooter(Long id, ScooterDto scooterDto)
            throws ScooterNotFoundException, RentalPointNotFoundException, TariffNotFoundException;

    void deleteScooter(Long id) throws ScooterNotFoundException;

    List<ScooterInfoDto> getAllScooters();

    List<ScooterInfoDto> getScootersByRentalPoint(Long rentalPointId)
            throws RentalPointNotFoundException;

    void updateScooterStatus(Long scooterId, ScooterStatus newStatus)
            throws ScooterNotFoundException;
}

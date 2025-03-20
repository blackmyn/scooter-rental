package com.example.scooterrental.service;

import com.example.scooterrental.dto.RentalPointDto;
import com.example.scooterrental.dto.RentalPointInfoDto;
import com.example.scooterrental.exception.RentalPointNotFoundException;

import java.util.List;

public interface RentalPointService {
    RentalPointDto createRentalPoint(RentalPointDto rentalPointDto)
            throws RentalPointNotFoundException;

    RentalPointInfoDto getRentalPointById(Long id) throws RentalPointNotFoundException;

    RentalPointDto updateRentalPoint(Long id, RentalPointDto rentalPointDto)
            throws RentalPointNotFoundException;

    void deleteRentalPoint(Long id) throws RentalPointNotFoundException;

    List<RentalPointInfoDto> getAllRentalPoints();

    List<RentalPointInfoDto> getRootRentalPoints();

    List<RentalPointInfoDto> getChildRentalPoints(Long parentId)
            throws RentalPointNotFoundException;
}

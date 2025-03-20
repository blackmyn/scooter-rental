package com.example.scooterrental.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.example.scooterrental.dto.RentalPointDto;
import com.example.scooterrental.dto.RentalPointInfoDto;
import com.example.scooterrental.exception.RentalPointNotFoundException;
import com.example.scooterrental.model.RentalPoint;
import com.example.scooterrental.repository.RentalPointRepository;
import com.example.scooterrental.service.ScooterService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RentalPointServiceImplTest {

    @Mock private RentalPointRepository rentalPointRepository;

    @Mock private ScooterService scooterService;

    @InjectMocks private RentalPointServiceImpl rentalPointService;

    private RentalPoint rentalPoint1;
    private RentalPoint rentalPoint2;
    private RentalPointDto rentalPointDto1;

    @BeforeEach
    void setUp() {
        rentalPoint1 = new RentalPoint(1L, "Точка 1", "Адрес 1", 55.75, 37.62, null, null, null);
        rentalPoint2 = new RentalPoint(2L, "Точка 2", "Адрес 2", 55.76, 37.63, null, null, null);
        rentalPointDto1 = new RentalPointDto(null, "Точка DTO", "Адрес DTO", 55.77, 37.64, null);

        new RentalPointInfoDto(1L, "Точка 1", "Адрес 1", 55.75, 37.62, null, null, null);
    }

    @Test
    void createRentalPoint_ShouldReturnRentalPointDto_WhenRentalPointIsCreated()
            throws RentalPointNotFoundException {
        RentalPoint savedRentalPoint = new RentalPoint();
        savedRentalPoint.setId(1L);
        savedRentalPoint.setName(rentalPointDto1.getName());
        savedRentalPoint.setAddress(rentalPointDto1.getAddress());
        savedRentalPoint.setLatitude(rentalPointDto1.getLatitude());
        savedRentalPoint.setLongitude(rentalPointDto1.getLongitude());

        when(rentalPointRepository.save(any(RentalPoint.class))).thenReturn(savedRentalPoint);

        RentalPointDto createdRentalPoint = rentalPointService.createRentalPoint(rentalPointDto1);

        assertNotNull(createdRentalPoint);
        assertEquals(savedRentalPoint.getName(), createdRentalPoint.getName());
        assertEquals(savedRentalPoint.getAddress(), createdRentalPoint.getAddress());
        assertEquals(savedRentalPoint.getId(), createdRentalPoint.getId());
        verify(rentalPointRepository, times(1)).save(any(RentalPoint.class));
    }

    @Test
    void getRentalPointById_ShouldReturnRentalPointDto_WhenRentalPointExists()
            throws RentalPointNotFoundException {
        when(rentalPointRepository.findById(1L)).thenReturn(Optional.of(rentalPoint1));
        when(scooterService.getScootersByRentalPoint(anyLong())).thenReturn(List.of());

        RentalPointInfoDto rentalPointDto = rentalPointService.getRentalPointById(1L);

        assertNotNull(rentalPointDto);
        assertEquals(rentalPoint1.getId(), rentalPointDto.getId());
        assertEquals(rentalPoint1.getName(), rentalPointDto.getName());
        verify(rentalPointRepository, times(1)).findById(1L);
    }

    @Test
    void getRentalPointById_ShouldThrowRentalPointNotFoundException_WhenRentalPointDoesNotExist() {
        when(rentalPointRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                RentalPointNotFoundException.class,
                () -> rentalPointService.getRentalPointById(1L));
        verify(rentalPointRepository, times(1)).findById(1L);
    }

    @Test
    void updateRentalPoint_ShouldReturnUpdatedRentalPointDto_WhenRentalPointExists()
            throws RentalPointNotFoundException {
        RentalPointDto rentalPointDtoUpdate =
                new RentalPointDto(1L, "Новое название", "Новый адрес", 55.78, 37.65, null);
        when(rentalPointRepository.findById(1L)).thenReturn(Optional.of(rentalPoint1));
        when(rentalPointRepository.save(any(RentalPoint.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RentalPointDto updatedRentalPointDto =
                rentalPointService.updateRentalPoint(1L, rentalPointDtoUpdate);

        assertNotNull(updatedRentalPointDto);
        assertEquals(rentalPointDtoUpdate.getName(), updatedRentalPointDto.getName());
        assertEquals(rentalPointDtoUpdate.getAddress(), updatedRentalPointDto.getAddress());
        verify(rentalPointRepository, times(1)).findById(1L);
        verify(rentalPointRepository, times(1)).save(any(RentalPoint.class));
    }

    @Test
    void updateRentalPoint_ShouldThrowRentalPointNotFoundException_WhenRentalPointDoesNotExist() {
        RentalPointDto rentalPointDtoUpdate =
                new RentalPointDto(1L, "Новое название", "Новый адрес", 55.78, 37.65, null);
        when(rentalPointRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                RentalPointNotFoundException.class,
                () -> rentalPointService.updateRentalPoint(1L, rentalPointDtoUpdate));
        verify(rentalPointRepository, times(1)).findById(1L);
        verify(rentalPointRepository, never()).save(any(RentalPoint.class));
    }

    @Test
    void deleteRentalPoint_ShouldDeleteRentalPoint_WhenRentalPointExists()
            throws RentalPointNotFoundException {
        when(rentalPointRepository.findById(1L)).thenReturn(Optional.of(rentalPoint1));

        rentalPointService.deleteRentalPoint(1L);

        verify(rentalPointRepository, times(1)).findById(1L);
        verify(rentalPointRepository, times(1)).delete(rentalPoint1);
    }

    @Test
    void deleteRentalPoint_ShouldThrowRentalPointNotFoundException_WhenRentalPointDoesNotExist() {
        when(rentalPointRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                RentalPointNotFoundException.class, () -> rentalPointService.deleteRentalPoint(1L));
        verify(rentalPointRepository, times(1)).findById(1L);
        verify(rentalPointRepository, never()).delete(any(RentalPoint.class));
    }

    @Test
    void getAllRentalPoints_ShouldReturnListOfRentalPointDtos() {
        List<RentalPoint> rentalPoints = Arrays.asList(rentalPoint1, rentalPoint2);
        when(rentalPointRepository.findAll()).thenReturn(rentalPoints);
        when(scooterService.getScootersByRentalPoint(anyLong())).thenReturn(List.of());

        List<RentalPointInfoDto> rentalPointDtos = rentalPointService.getAllRentalPoints();

        assertNotNull(rentalPointDtos);
        assertEquals(2, rentalPointDtos.size());
        verify(rentalPointRepository, times(1)).findAll();
    }

    @Test
    void getRootRentalPoints_ShouldReturnListOfRootRentalPointDtos() {
        List<RentalPoint> rootRentalPoints = Arrays.asList(rentalPoint1, rentalPoint2);
        when(rentalPointRepository.findRootRentalPoints()).thenReturn(rootRentalPoints);
        when(scooterService.getScootersByRentalPoint(anyLong())).thenReturn(List.of());

        List<RentalPointInfoDto> rootRentalPointDtos = rentalPointService.getRootRentalPoints();

        assertNotNull(rootRentalPointDtos);
        assertEquals(2, rootRentalPointDtos.size());
        verify(rentalPointRepository, times(1)).findRootRentalPoints();
    }

    @Test
    void getChildRentalPoints_ShouldReturnListOfChildRentalPointDtos()
            throws RentalPointNotFoundException {
        RentalPoint parent = new RentalPoint(3L, "Родитель", "Адрес", 55.0, 37.0, null, null, null);
        rentalPoint1.setParentPoint(parent);
        rentalPoint2.setParentPoint(parent);
        List<RentalPoint> childRentalPoints = Arrays.asList(rentalPoint1, rentalPoint2);

        when(rentalPointRepository.existsById(3L)).thenReturn(true);
        when(rentalPointRepository.findByParentPointId(3L)).thenReturn(childRentalPoints);
        when(scooterService.getScootersByRentalPoint(anyLong())).thenReturn(List.of());

        List<RentalPointInfoDto> childRentalPointDtos = rentalPointService.getChildRentalPoints(3L);

        assertNotNull(childRentalPointDtos);
        assertEquals(2, childRentalPointDtos.size());
        verify(rentalPointRepository, times(1)).findByParentPointId(3L);
    }

    @Test
    void getChildRentalPoints_ShouldThrowRentalPointNotFoundException_WhenParentDoesNotExist() {
        when(rentalPointRepository.existsById(3L)).thenReturn(false);

        assertThrows(
                RentalPointNotFoundException.class,
                () -> rentalPointService.getChildRentalPoints(3L));
        verify(rentalPointRepository, times(1)).existsById(3L);
        verify(rentalPointRepository, never()).findByParentPointId(anyLong());
    }
}

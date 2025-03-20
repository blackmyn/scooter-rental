package com.example.scooterrental.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.scooterrental.dto.ScooterDto;
import com.example.scooterrental.dto.ScooterInfoDto;
import com.example.scooterrental.exception.RentalPointNotFoundException;
import com.example.scooterrental.exception.ScooterNotFoundException;
import com.example.scooterrental.model.RentalPoint;
import com.example.scooterrental.model.Scooter;
import com.example.scooterrental.model.ScooterStatus;
import com.example.scooterrental.model.Tariff;
import com.example.scooterrental.repository.RentalPointRepository;
import com.example.scooterrental.repository.ScooterRepository;
import com.example.scooterrental.repository.TariffRepository;

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
public class ScooterServiceImplTest {

    @Mock private ScooterRepository scooterRepository;

    @Mock private RentalPointRepository rentalPointRepository;

    @Mock private TariffRepository tariffRepository;

    @InjectMocks private ScooterServiceImpl scooterService;

    private Scooter scooter1;
    private Scooter scooter2;
    private ScooterDto scooterDto1;
    private RentalPoint rentalPoint1;
    private Tariff tariff1;

    @BeforeEach
    void setUp() {
        rentalPoint1 = new RentalPoint(1L, "Точка1", "Адрес1", 1.0, 1.0, null, null, null);
        tariff1 = new Tariff(1L, "Почасовой", "Описание", 100.0, null, null, false);
        scooter1 =
                new Scooter(
                        1L,
                        "Model 1",
                        "SN1",
                        ScooterStatus.AVAILABLE,
                        80,
                        100.0,
                        rentalPoint1,
                        tariff1);
        scooter2 =
                new Scooter(
                        2L,
                        "Model 2",
                        "SN2",
                        ScooterStatus.IN_USE,
                        50,
                        200.0,
                        rentalPoint1,
                        tariff1);
        scooterDto1 =
                new ScooterDto(
                        null, "Model DTO", "SN DTO", ScooterStatus.AVAILABLE, 90, 50.0, 1L, 1L);
    }

    @Test
    void createScooter_ShouldReturnScooterDto_WhenScooterIsCreated()
            throws RentalPointNotFoundException {
        when(scooterRepository.existsBySerialNumber(anyString())).thenReturn(false);
        when(rentalPointRepository.findById(1L)).thenReturn(Optional.of(rentalPoint1));
        when(tariffRepository.findById(1L)).thenReturn(Optional.of(tariff1));

        Scooter savedScooter = new Scooter();
        savedScooter.setId(1L);
        savedScooter.setModel(scooterDto1.getModel());
        savedScooter.setSerialNumber(scooterDto1.getSerialNumber());
        savedScooter.setStatus(scooterDto1.getStatus());
        savedScooter.setChargeLevel(scooterDto1.getChargeLevel());
        savedScooter.setMileage(scooterDto1.getMileage());
        savedScooter.setRentalPoint(rentalPoint1);
        savedScooter.setTariff(tariff1);

        when(scooterRepository.save(any(Scooter.class))).thenReturn(savedScooter);

        ScooterDto createdScooter = scooterService.createScooter(scooterDto1);

        assertNotNull(createdScooter);
        assertEquals(savedScooter.getModel(), createdScooter.getModel());
        assertEquals(savedScooter.getSerialNumber(), createdScooter.getSerialNumber());
        verify(scooterRepository, times(1)).save(any(Scooter.class));
    }

    @Test
    void createScooter_ShouldThrowException_WhenSerialNumberExist() {
        when(scooterRepository.existsBySerialNumber(anyString())).thenReturn(true);

        IllegalArgumentException thrown =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> scooterService.createScooter(scooterDto1),
                        "Expected doThing() to throw, but it didn't");

        assertEquals("Самокат с таким серийным номером уже существует", thrown.getMessage());
    }

    @Test
    void createScooter_ShouldThrowRentalPointNotFoundException_WhenRentalPointDoesNotExist() {
        when(scooterRepository.existsBySerialNumber(anyString())).thenReturn(false);
        when(rentalPointRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                RentalPointNotFoundException.class,
                () -> scooterService.createScooter(scooterDto1));
        verify(scooterRepository, never()).save(any(Scooter.class));
    }

    @Test
    void getScooterById_ShouldReturnScooterInfoDto_WhenScooterExists()
            throws ScooterNotFoundException {
        when(scooterRepository.findById(1L)).thenReturn(Optional.of(scooter1));

        ScooterInfoDto scooterInfoDto = scooterService.getScooterById(1L);

        assertNotNull(scooterInfoDto);
        assertEquals(scooter1.getId(), scooterInfoDto.getId());
        assertEquals(scooter1.getModel(), scooterInfoDto.getModel());
        verify(scooterRepository, times(1)).findById(1L);
    }

    @Test
    void getScooterById_ShouldThrowScooterNotFoundException_WhenScooterDoesNotExist() {
        when(scooterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ScooterNotFoundException.class, () -> scooterService.getScooterById(1L));
        verify(scooterRepository, times(1)).findById(1L);
    }

    @Test
    void updateScooter_ShouldReturnUpdatedScooterDto_WhenScooterExists()
            throws ScooterNotFoundException, RentalPointNotFoundException {
        ScooterDto scooterDtoUpdate =
                new ScooterDto(
                        1L, "New Model", "New SN", ScooterStatus.MAINTENANCE, 90, 150.0, 1L, 1L);
        when(scooterRepository.findById(1L)).thenReturn(Optional.of(scooter1));
        when(rentalPointRepository.findById(1L)).thenReturn(Optional.of(rentalPoint1));
        when(tariffRepository.findById(1L)).thenReturn(Optional.of(tariff1));
        when(scooterRepository.save(any(Scooter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ScooterDto updatedScooterDto = scooterService.updateScooter(1L, scooterDtoUpdate);

        assertNotNull(updatedScooterDto);
        assertEquals(scooterDtoUpdate.getModel(), updatedScooterDto.getModel());
        assertEquals(scooterDtoUpdate.getStatus(), updatedScooterDto.getStatus());
        verify(scooterRepository, times(1)).findById(1L);
        verify(scooterRepository, times(1)).save(any(Scooter.class));
    }

    @Test
    void updateScooter_ShouldThrowScooterNotFoundException_WhenScooterDoesNotExist() {
        ScooterDto scooterDtoUpdate =
                new ScooterDto(
                        1L, "New Model", "New SN", ScooterStatus.MAINTENANCE, 90, 150.0, 1L, 1L);
        when(scooterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                ScooterNotFoundException.class,
                () -> scooterService.updateScooter(1L, scooterDtoUpdate));
        verify(scooterRepository, times(1)).findById(1L);
        verify(scooterRepository, never()).save(any(Scooter.class));
    }

    @Test
    void updateScooter_ShouldThrowRentalPointNotFoundException_WhenRentalPointDoesNotExist() {
        ScooterDto scooterDtoUpdate =
                new ScooterDto(
                        1L, "New Model", "New SN", ScooterStatus.MAINTENANCE, 90, 150.0, 2L, 1L);
        when(scooterRepository.findById(1L)).thenReturn(Optional.of(scooter1));
        when(rentalPointRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(
                RentalPointNotFoundException.class,
                () -> scooterService.updateScooter(1L, scooterDtoUpdate));
        verify(scooterRepository, times(1)).findById(1L);
        verify(scooterRepository, never()).save(any(Scooter.class));
    }

    @Test
    void deleteScooter_ShouldDeleteScooter_WhenScooterExists() throws ScooterNotFoundException {
        when(scooterRepository.findById(1L)).thenReturn(Optional.of(scooter1));

        scooterService.deleteScooter(1L);

        verify(scooterRepository, times(1)).findById(1L);
        verify(scooterRepository, times(1)).delete(scooter1);
    }

    @Test
    void deleteScooter_ShouldThrowScooterNotFoundException_WhenScooterDoesNotExist() {
        when(scooterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ScooterNotFoundException.class, () -> scooterService.deleteScooter(1L));
        verify(scooterRepository, times(1)).findById(1L);
        verify(scooterRepository, never()).delete(any(Scooter.class));
    }

    @Test
    void getAllScooters_ShouldReturnListOfScooterInfoDtos() {
        List<Scooter> scooters = Arrays.asList(scooter1, scooter2);
        when(scooterRepository.findAll()).thenReturn(scooters);

        List<ScooterInfoDto> scooterInfoDtos = scooterService.getAllScooters();

        assertNotNull(scooterInfoDtos);
        assertEquals(2, scooterInfoDtos.size());
        verify(scooterRepository, times(1)).findAll();
    }

    @Test
    void updateScooterStatus_ShouldUpdateScooterStatus_WhenScooterExists()
            throws ScooterNotFoundException {
        when(scooterRepository.findById(1L)).thenReturn(Optional.of(scooter1));
        when(scooterRepository.save(any(Scooter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        scooterService.updateScooterStatus(1L, ScooterStatus.MAINTENANCE);

        assertEquals(ScooterStatus.MAINTENANCE, scooter1.getStatus());
        verify(scooterRepository, times(1)).findById(1L);
        verify(scooterRepository, times(1)).save(any(Scooter.class));
    }

    @Test
    void updateScooterStatus_ShouldThrowScooterNotFoundException_WhenScooterDoesNotExist() {
        when(scooterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                ScooterNotFoundException.class,
                () -> scooterService.updateScooterStatus(1L, ScooterStatus.MAINTENANCE));
        verify(scooterRepository, times(1)).findById(1L);
        verify(scooterRepository, never()).save(any(Scooter.class));
    }
}

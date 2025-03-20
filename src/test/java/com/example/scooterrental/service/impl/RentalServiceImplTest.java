package com.example.scooterrental.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.scooterrental.dto.RentalDto;
import com.example.scooterrental.dto.RentalInfoDto;
import com.example.scooterrental.exception.RentalNotFoundException;
import com.example.scooterrental.exception.ScooterNotFoundException;
import com.example.scooterrental.exception.UserNotFoundException;
import com.example.scooterrental.model.*;
import com.example.scooterrental.repository.RentalRepository;
import com.example.scooterrental.repository.ScooterRepository;
import com.example.scooterrental.repository.TariffRepository;
import com.example.scooterrental.repository.UserRepository;
import com.example.scooterrental.service.ScooterService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RentalServiceImplTest {

    @Mock private RentalRepository rentalRepository;

    @Mock private UserRepository userRepository;

    @Mock private ScooterRepository scooterRepository;

    @Mock private ScooterService scooterService;

    @Mock private TariffRepository tariffRepository;

    @InjectMocks private RentalServiceImpl rentalService;

    private User user;
    private Scooter scooter;
    private Tariff tariff;
    private Rental rental1;
    private Rental rental2;
    private RentalDto rentalDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "user1", "password", "Имя", "Фамилия", "email", "телефон", null);
        scooter = new Scooter(1L, "Model", "SN123", ScooterStatus.AVAILABLE, 100, 0.0, null, null);
        tariff = new Tariff(1L, "TariffName", "TariffDescription", 10.0, 100.0, 0.1, true);
        rental1 =
                new Rental(
                        1L,
                        user,
                        scooter,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(1),
                        0.0,
                        10.0,
                        10.0,
                        tariff);
        rental2 =
                new Rental(
                        2L,
                        user,
                        scooter,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(2),
                        0.0,
                        20.0,
                        20.0,
                        tariff);
        rentalDto = new RentalDto(null, 1L, 1L, LocalDateTime.now(), null, null, null, null, 1L);
    }

    @Test
    void createRental_ShouldReturnRentalDto_WhenRentalIsCreated()
            throws UserNotFoundException, ScooterNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(scooterRepository.findById(1L)).thenReturn(Optional.of(scooter));
        when(tariffRepository.findById(1L)).thenReturn(Optional.of(tariff));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental1);
        doNothing().when(scooterService).updateScooterStatus(1L, ScooterStatus.IN_USE);

        RentalDto createdRental = rentalService.createRental(rentalDto);

        assertNotNull(createdRental);
        assertEquals(rental1.getId(), createdRental.getId());
        verify(rentalRepository, times(1)).save(any(Rental.class));
        verify(scooterService, times(1)).updateScooterStatus(1L, ScooterStatus.IN_USE);
    }

    @Test
    void createRental_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> rentalService.createRental(rentalDto));
        verify(rentalRepository, never()).save(any(Rental.class));
        verify(scooterService, never()).updateScooterStatus(any(), any());
        verify(tariffRepository, never()).findById(any());
    }

    @Test
    void createRental_ShouldThrowScooterNotFoundException_WhenScooterDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(scooterRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ScooterNotFoundException.class, () -> rentalService.createRental(rentalDto));
        verify(rentalRepository, never()).save(any(Rental.class));
        verify(scooterService, never()).updateScooterStatus(any(), any());
        verify(tariffRepository, never()).findById(any());
    }

    @Test
    void getRentalById_ShouldReturnRentalDto_WhenRentalExists() throws RentalNotFoundException {
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental1));

        RentalInfoDto rentalDto = rentalService.getRentalById(1L);

        assertNotNull(rentalDto);
        assertEquals(rental1.getId(), rentalDto.getId());
        verify(rentalRepository, times(1)).findById(1L);
    }

    @Test
    void getRentalById_ShouldThrowRentalNotFoundException_WhenRentalDoesNotExist() {
        when(rentalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RentalNotFoundException.class, () -> rentalService.getRentalById(1L));
        verify(rentalRepository, times(1)).findById(1L);
    }

    @Test
    void getAllRentals_ShouldReturnListOfRentalDtos() {
        List<Rental> rentals = Arrays.asList(rental1, rental2);
        when(rentalRepository.findAll()).thenReturn(rentals);

        List<RentalInfoDto> rentalDtos = rentalService.getAllRentals();

        assertNotNull(rentalDtos);
        assertEquals(2, rentalDtos.size());
        verify(rentalRepository, times(1)).findAll();
    }

    @Test
    void getRentalsByUser_ShouldReturnListOfRentalDtos_WhenUserExists() {
        List<Rental> rentals = Arrays.asList(rental1, rental2);
        when(rentalRepository.findByUserId(1L)).thenReturn(rentals);

        List<RentalInfoDto> rentalDtos = rentalService.getRentalsByUser(1L);

        assertNotNull(rentalDtos);
        assertEquals(2, rentalDtos.size());
        verify(rentalRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getRentalsByScooter_ShouldReturnListOfRentalDtos_WhenScooterExists() {
        List<Rental> rentals = Arrays.asList(rental1, rental2);
        when(rentalRepository.findByScooterId(1L)).thenReturn(rentals);

        List<RentalInfoDto> rentalDtos = rentalService.getRentalsByScooter(1L);

        assertNotNull(rentalDtos);
        assertEquals(2, rentalDtos.size());
        verify(rentalRepository, times(1)).findByScooterId(1L);
    }
}

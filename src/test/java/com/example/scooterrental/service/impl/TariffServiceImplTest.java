package com.example.scooterrental.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.scooterrental.dto.TariffDto;
import com.example.scooterrental.exception.TariffNotFoundException;
import com.example.scooterrental.model.Tariff;
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
public class TariffServiceImplTest {

    @Mock private TariffRepository tariffRepository;

    @InjectMocks private TariffServiceImpl tariffService;

    private Tariff tariff1;
    private Tariff tariff2;
    private TariffDto tariffDto1;

    @BeforeEach
    void setUp() {
        tariff1 = new Tariff(1L, "Почасовой", "Описание 1", 100.0, null, null, false);
        tariff2 = new Tariff(2L, "Абонемент", "Описание 2", null, 500.0, 0.1, true);
        tariffDto1 =
                new TariffDto(null, "Почасовой DTO", "Описание 1 DTO", 100.0, null, null, false);
    }

    @Test
    void createTariff_ShouldReturnTariffDto_WhenTariffIsCreated() {
        Tariff savedTariff = new Tariff();
        savedTariff.setId(1L);
        savedTariff.setName(tariffDto1.getName());
        savedTariff.setDescription(tariffDto1.getDescription());
        savedTariff.setPricePerHour(tariffDto1.getPricePerHour());
        savedTariff.setSubscriptionPrice(tariffDto1.getSubscriptionPrice());
        savedTariff.setDiscount(tariffDto1.getDiscount());
        savedTariff.setIsSubscription(tariffDto1.getIsSubscription());
        when(tariffRepository.save(any(Tariff.class))).thenReturn(savedTariff);

        TariffDto createdTariff = tariffService.createTariff(tariffDto1);

        assertNotNull(createdTariff);
        assertEquals(savedTariff.getName(), createdTariff.getName());
        assertEquals(savedTariff.getDescription(), createdTariff.getDescription());
        assertEquals(savedTariff.getPricePerHour(), createdTariff.getPricePerHour());
        assertEquals(savedTariff.getSubscriptionPrice(), createdTariff.getSubscriptionPrice());
        assertEquals(savedTariff.getDiscount(), createdTariff.getDiscount());
        assertEquals(savedTariff.getIsSubscription(), createdTariff.getIsSubscription());
        verify(tariffRepository, times(1)).save(any(Tariff.class));
    }

    @Test
    void getTariffById_ShouldReturnTariffDto_WhenTariffExists() throws TariffNotFoundException {
        when(tariffRepository.findById(1L)).thenReturn(Optional.of(tariff1));

        TariffDto tariffDto = tariffService.getTariffById(1L);

        assertNotNull(tariffDto);
        assertEquals(tariff1.getId(), tariffDto.getId());
        assertEquals(tariff1.getName(), tariffDto.getName());
        verify(tariffRepository, times(1)).findById(1L);
    }

    @Test
    void getTariffById_ShouldThrowTariffNotFoundException_WhenTariffDoesNotExist() {
        when(tariffRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TariffNotFoundException.class, () -> tariffService.getTariffById(1L));
        verify(tariffRepository, times(1)).findById(1L);
    }

    @Test
    void updateTariff_ShouldReturnUpdatedTariffDto_WhenTariffExists()
            throws TariffNotFoundException {
        TariffDto tariffDtoUpdate =
                new TariffDto(1L, "Новое название", "Новое описание", 120.0, null, null, false);
        when(tariffRepository.findById(1L)).thenReturn(Optional.of(tariff1));
        when(tariffRepository.save(any(Tariff.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TariffDto updatedTariffDto = tariffService.updateTariff(1L, tariffDtoUpdate);

        assertNotNull(updatedTariffDto);
        assertEquals(tariffDtoUpdate.getName(), updatedTariffDto.getName());
        assertEquals(tariffDtoUpdate.getDescription(), updatedTariffDto.getDescription());
        verify(tariffRepository, times(1)).findById(1L);
        verify(tariffRepository, times(1)).save(any(Tariff.class));
    }

    @Test
    void updateTariff_ShouldThrowTariffNotFoundException_WhenTariffDoesNotExist() {
        TariffDto tariffDtoUpdate =
                new TariffDto(1L, "Новое название", "Новое описание", 120.0, null, null, false);
        when(tariffRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                TariffNotFoundException.class,
                () -> tariffService.updateTariff(1L, tariffDtoUpdate));
        verify(tariffRepository, times(1)).findById(1L);
        verify(tariffRepository, never()).save(any(Tariff.class));
    }

    @Test
    void deleteTariff_ShouldDeleteTariff_WhenTariffExists() throws TariffNotFoundException {
        when(tariffRepository.findById(1L)).thenReturn(Optional.of(tariff1));
        tariffService.deleteTariff(1L);

        verify(tariffRepository, times(1)).findById(1L);
        verify(tariffRepository, times(1)).delete(tariff1);
    }

    @Test
    void deleteTariff_ShouldThrowTariffNotFoundException_WhenTariffDoesNotExist() {
        when(tariffRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TariffNotFoundException.class, () -> tariffService.deleteTariff(1L));
        verify(tariffRepository, times(1)).findById(1L);
        verify(tariffRepository, never()).delete(any(Tariff.class));
    }

    @Test
    void getAllTariffs_ShouldReturnListOfTariffDtos() {
        List<Tariff> tariffs = Arrays.asList(tariff1, tariff2);
        when(tariffRepository.findAll()).thenReturn(tariffs);

        List<TariffDto> tariffDtos = tariffService.getAllTariffs();

        assertNotNull(tariffDtos);
        assertEquals(2, tariffDtos.size());
        verify(tariffRepository, times(1)).findAll();
    }
}

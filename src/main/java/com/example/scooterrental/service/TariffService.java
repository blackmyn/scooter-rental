package com.example.scooterrental.service;

import com.example.scooterrental.dto.TariffDto;
import com.example.scooterrental.exception.TariffNotFoundException;

import java.util.List;

public interface TariffService {
    TariffDto createTariff(TariffDto tariffDto);

    TariffDto getTariffById(Long id) throws TariffNotFoundException;

    TariffDto updateTariff(Long id, TariffDto tariffDto) throws TariffNotFoundException;

    void deleteTariff(Long id) throws TariffNotFoundException;

    List<TariffDto> getAllTariffs();
}

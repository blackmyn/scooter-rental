package com.example.scooterrental.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.scooterrental.dto.TariffDto;
import com.example.scooterrental.exception.TariffNotFoundException;
import com.example.scooterrental.service.TariffService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(TariffController.class)
public class TariffControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private TariffService tariffService;

    private TariffDto tariffDto1;
    private TariffDto tariffDto2;

    @BeforeEach
    void setUp() {
        tariffDto1 = new TariffDto(1L, "Почасовой", "Описание 1", 100.0, null, null, false);
        tariffDto2 = new TariffDto(2L, "Абонемент", "Описание 2", null, 500.0, 0.1, true);
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void createTariff_ShouldReturnCreatedTariff_WhenTariffIsValid() throws Exception {
        when(tariffService.createTariff(any(TariffDto.class))).thenReturn(tariffDto1);

        mockMvc.perform(
                        post("/api/tariffs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tariffDto1))
                                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(tariffDto1.getId()))
                .andExpect(jsonPath("$.name").value(tariffDto1.getName()));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void createTariff_ShouldReturnBadRequest_WhenTariffIsInvalid() throws Exception {
        TariffDto invalidTariffDto = new TariffDto();

        mockMvc.perform(
                        post("/api/tariffs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidTariffDto))
                                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void getTariffById_ShouldReturnTariff_WhenTariffExists() throws Exception {
        when(tariffService.getTariffById(anyLong())).thenReturn(tariffDto1);

        mockMvc.perform(get("/api/tariffs/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(tariffDto1.getId()))
                .andExpect(jsonPath("$.name").value(tariffDto1.getName()));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void getTariffById_ShouldReturnNotFound_WhenTariffDoesNotExist() throws Exception {
        when(tariffService.getTariffById(anyLong()))
                .thenThrow(new TariffNotFoundException("Тариф не найден"));

        mockMvc.perform(get("/api/tariffs/1").with(csrf())).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void updateTariff_ShouldReturnUpdatedTariff_WhenTariffIsValid() throws Exception {
        when(tariffService.updateTariff(anyLong(), any(TariffDto.class))).thenReturn(tariffDto1);

        mockMvc.perform(
                        put("/api/tariffs/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(tariffDto1))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(tariffDto1.getId()))
                .andExpect(jsonPath("$.name").value(tariffDto1.getName()));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void deleteTariff_ShouldReturnNoContent_WhenTariffExists() throws Exception {
        mockMvc.perform(delete("/api/tariffs/1").with(csrf())).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void getAllTariffs_ShouldReturnListOfTariffs() throws Exception {
        List<TariffDto> tariffDtos = Arrays.asList(tariffDto1, tariffDto2);
        when(tariffService.getAllTariffs()).thenReturn(tariffDtos);

        mockMvc.perform(get("/api/tariffs").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(tariffDto1.getId()))
                .andExpect(jsonPath("$[0].name").value(tariffDto1.getName()))
                .andExpect(jsonPath("$[1].id").value(tariffDto2.getId()))
                .andExpect(jsonPath("$[1].name").value(tariffDto2.getName()));
    }
}

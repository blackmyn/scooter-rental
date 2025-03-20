package com.example.scooterrental.controller;

import com.example.scooterrental.dto.RentalPointDto;
import com.example.scooterrental.dto.RentalPointInfoDto;
import com.example.scooterrental.exception.RentalPointNotFoundException;
import com.example.scooterrental.service.RentalPointService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RentalPointController.class)
public class RentalPointControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private RentalPointService rentalPointService;

    private RentalPointDto rentalPointDto1;
    private RentalPointInfoDto rentalPointInfoDto1;

    @BeforeEach
    void setUp() {
        rentalPointDto1 = new RentalPointDto(1L, "Точка DTO", "Адрес DTO", 55.77, 37.64, null);
        rentalPointInfoDto1 =
                new RentalPointInfoDto(1L, "Точка 1", "Адрес 1", 55.75, 37.62, null, null, null);
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void createRentalPoint_ShouldReturnCreatedRentalPoint_WhenRentalPointIsValid()
            throws Exception {
        when(rentalPointService.createRentalPoint(any(RentalPointDto.class)))
                .thenReturn(rentalPointDto1);

        mockMvc.perform(
                        post("/api/rental-points")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(rentalPointDto1))
                                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(rentalPointDto1.getId()))
                .andExpect(jsonPath("$.name").value(rentalPointDto1.getName()));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void createRentalPoint_ShouldReturnBadRequest_WhenRentalPointIsInvalid() throws Exception {
        RentalPointDto invalidRentalPointDto = new RentalPointDto();

        mockMvc.perform(
                        post("/api/rental-points")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRentalPointDto))
                                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void getRentalPointById_ShouldReturnRentalPoint_WhenRentalPointExists() throws Exception {
        when(rentalPointService.getRentalPointById(anyLong())).thenReturn(rentalPointInfoDto1);

        mockMvc.perform(get("/api/rental-points/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(rentalPointInfoDto1.getId()))
                .andExpect(jsonPath("$.name").value(rentalPointInfoDto1.getName()));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void getRentalPointById_ShouldReturnNotFound_WhenRentalPointDoesNotExist() throws Exception {
        when(rentalPointService.getRentalPointById(anyLong()))
                .thenThrow(new RentalPointNotFoundException("Точка не найдена"));

        mockMvc.perform(get("/api/rental-points/1").with(csrf())).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void updateRentalPoint_ShouldReturnUpdatedRentalPoint_WhenRentalPointIsValid()
            throws Exception {
        when(rentalPointService.updateRentalPoint(anyLong(), any(RentalPointDto.class)))
                .thenReturn(rentalPointDto1);

        mockMvc.perform(
                        put("/api/rental-points/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(rentalPointDto1))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(rentalPointDto1.getId()))
                .andExpect(jsonPath("$.name").value(rentalPointDto1.getName()));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void deleteRentalPoint_ShouldReturnNoContent_WhenRentalPointExists() throws Exception {
        mockMvc.perform(delete("/api/rental-points/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void getAllRentalPoints_ShouldReturnListOfRentalPoints() throws Exception {
        List<RentalPointInfoDto> rentalPointDtos =
                Arrays.asList(rentalPointInfoDto1, rentalPointInfoDto1);
        when(rentalPointService.getAllRentalPoints()).thenReturn(rentalPointDtos);

        mockMvc.perform(get("/api/rental-points").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(rentalPointInfoDto1.getId()))
                .andExpect(jsonPath("$[0].name").value(rentalPointInfoDto1.getName()))
                .andExpect(jsonPath("$[1].id").value(rentalPointInfoDto1.getId()))
                .andExpect(jsonPath("$[1].name").value(rentalPointInfoDto1.getName()));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void getRootRentalPoints_ShouldReturnListOfRootRentalPoints() throws Exception {
        List<RentalPointInfoDto> rentalPointDtos =
                Arrays.asList(rentalPointInfoDto1, rentalPointInfoDto1);
        when(rentalPointService.getRootRentalPoints()).thenReturn(rentalPointDtos);

        mockMvc.perform(get("/api/rental-points/root").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(rentalPointInfoDto1.getId()))
                .andExpect(jsonPath("$[0].name").value(rentalPointInfoDto1.getName()))
                .andExpect(jsonPath("$[1].id").value(rentalPointInfoDto1.getId()))
                .andExpect(jsonPath("$[1].name").value(rentalPointInfoDto1.getName()));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void getChildRentalPoints_ShouldReturnListOfChildRentalPoints() throws Exception {
        List<RentalPointInfoDto> rentalPointDtos =
                Arrays.asList(rentalPointInfoDto1, rentalPointInfoDto1);
        when(rentalPointService.getChildRentalPoints(anyLong())).thenReturn(rentalPointDtos);

        mockMvc.perform(get("/api/rental-points/1/children").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(rentalPointInfoDto1.getId()))
                .andExpect(jsonPath("$[0].name").value(rentalPointInfoDto1.getName()))
                .andExpect(jsonPath("$[1].id").value(rentalPointInfoDto1.getId()))
                .andExpect(jsonPath("$[1].name").value(rentalPointInfoDto1.getName()));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void getChildRentalPoints_ShouldReturnNotFound_WhenParentDoesNotExist() throws Exception {
        when(rentalPointService.getChildRentalPoints(anyLong()))
                .thenThrow(new RentalPointNotFoundException("Точка не найдена"));

        mockMvc.perform(get("/api/rental-points/1/children").with(csrf()))
                .andExpect(status().isNotFound());
    }
}

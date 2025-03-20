package com.example.scooterrental.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.scooterrental.dto.ScooterDto;
import com.example.scooterrental.dto.ScooterInfoDto;
import com.example.scooterrental.exception.ScooterNotFoundException;
import com.example.scooterrental.model.ScooterStatus;
import com.example.scooterrental.service.ScooterService;
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

@WebMvcTest(ScooterController.class)
public class ScooterControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private ScooterService scooterService;

    private ScooterDto scooterDto1;
    private ScooterInfoDto scooterInfoDto1;

    @BeforeEach
    void setUp() {
        scooterDto1 =
                new ScooterDto(
                        1L, "МОДЕЛЬ DTO", "SN123 DTO", ScooterStatus.AVAILABLE, 90, 50.0, 1L, 1L);
        scooterInfoDto1 =
                new ScooterInfoDto(
                        1L,
                        "INFO",
                        "SN123",
                        ScooterStatus.AVAILABLE,
                        90,
                        50.0,
                        1L,
                        "ТОЧКА123",
                        1L,
                        "ТАРИФ1");
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void createScooter_ShouldReturnCreatedScooter_WhenScooterIsValid() throws Exception {
        when(scooterService.createScooter(any(ScooterDto.class))).thenReturn(scooterDto1);

        mockMvc.perform(
                        post("/api/scooters")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(scooterDto1))
                                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(scooterDto1.getId()))
                .andExpect(jsonPath("$.model").value(scooterDto1.getModel()));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void createScooter_ShouldReturnBadRequest_WhenScooterIsInvalid() throws Exception {
        ScooterDto invalidScooterDto = new ScooterDto();

        mockMvc.perform(
                        post("/api/scooters")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidScooterDto))
                                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getScooterById_ShouldReturnScooter_WhenScooterExists() throws Exception {
        when(scooterService.getScooterById(anyLong())).thenReturn(scooterInfoDto1);

        mockMvc.perform(get("/api/scooters/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(scooterInfoDto1.getId()))
                .andExpect(jsonPath("$.model").value(scooterInfoDto1.getModel()));
    }

    @Test
    @WithMockUser
    void getScooterById_ShouldReturnNotFound_WhenScooterDoesNotExist() throws Exception {
        when(scooterService.getScooterById(anyLong()))
                .thenThrow(new ScooterNotFoundException("Самокат не найден"));

        mockMvc.perform(get("/api/scooters/1")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void updateScooter_ShouldReturnUpdatedScooter_WhenScooterIsValid() throws Exception {
        when(scooterService.updateScooter(anyLong(), any(ScooterDto.class)))
                .thenReturn(scooterDto1);

        mockMvc.perform(
                        put("/api/scooters/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(scooterDto1))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(scooterDto1.getId()))
                .andExpect(jsonPath("$.model").value(scooterDto1.getModel()));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void deleteScooter_ShouldReturnNoContent_WhenScooterExists() throws Exception {
        mockMvc.perform(delete("/api/scooters/1").with(csrf())).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void getAllScooters_ShouldReturnListOfScooters() throws Exception {
        List<ScooterInfoDto> scooterDtos = Arrays.asList(scooterInfoDto1, scooterInfoDto1);
        when(scooterService.getAllScooters()).thenReturn(scooterDtos);

        mockMvc.perform(get("/api/scooters"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(scooterInfoDto1.getId()))
                .andExpect(jsonPath("$[0].model").value(scooterInfoDto1.getModel()));
    }

    @Test
    @WithMockUser
    void getScootersByRentalPoint_ShouldReturnListOfScooters() throws Exception {
        List<ScooterInfoDto> scooterDtos = Arrays.asList(scooterInfoDto1, scooterInfoDto1);
        when(scooterService.getScootersByRentalPoint(anyLong())).thenReturn(scooterDtos);

        mockMvc.perform(get("/api/scooters/rental-point/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(scooterInfoDto1.getId()))
                .andExpect(jsonPath("$[0].model").value(scooterInfoDto1.getModel()));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void updateScooterStatus_ShouldReturnNoContent_WhenScooterExists() throws Exception {
        mockMvc.perform(
                        patch("/api/scooters/1/status")
                                .param("newStatus", "MAINTENANCE")
                                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}

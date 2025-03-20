package com.example.scooterrental.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.scooterrental.dto.RentalDto;
import com.example.scooterrental.dto.RentalInfoDto;
import com.example.scooterrental.exception.RentalNotFoundException;
import com.example.scooterrental.service.RentalService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@WebMvcTest(RentalController.class)
public class RentalControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private RentalService rentalService;

    private RentalDto rentalDto;
    private RentalInfoDto rentalInfoDto1;
    private RentalInfoDto rentalInfoDto2;

    @BeforeEach
    void setUp() {
        rentalDto = new RentalDto(null, 1L, 1L, LocalDateTime.now(), null, null, null, null, null);
        rentalInfoDto1 =
                new RentalInfoDto(
                        1L,
                        1L,
                        "user1",
                        1L,
                        "scooter1",
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        0.0,
                        0.0,
                        10.0,
                        null,
                        null);
        rentalInfoDto2 =
                new RentalInfoDto(
                        2L,
                        2L,
                        "user2",
                        2L,
                        "scooter2",
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        0.0,
                        0.0,
                        20.0,
                        null,
                        null);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void createRental_ShouldReturnCreatedRental_WhenRentalIsValid() throws Exception {
        when(rentalService.createRental(any(RentalDto.class))).thenReturn(rentalDto);

        mockMvc.perform(
                        post("/api/rentals")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(rentalDto))
                                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(rentalDto.getUserId()))
                .andExpect(jsonPath("$.scooterId").value(rentalDto.getScooterId()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void createRental_ShouldReturnBadRequest_WhenRentalIsInvalid() throws Exception {
        RentalDto invalidRentalDto = new RentalDto();

        mockMvc.perform(
                        post("/api/rentals")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRentalDto))
                                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getRentalById_ShouldReturnRental_WhenRentalExists() throws Exception {
        when(rentalService.getRentalById(1L)).thenReturn(rentalInfoDto1);

        mockMvc.perform(get("/api/rentals/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(rentalInfoDto1.getUserId()))
                .andExpect(jsonPath("$.scooterId").value(rentalInfoDto1.getScooterId()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getRentalById_ShouldReturnNotFound_WhenRentalDoesNotExist() throws Exception {
        when(rentalService.getRentalById(1L))
                .thenThrow(new RentalNotFoundException("Аренда не найдена"));

        mockMvc.perform(get("/api/rentals/1").with(csrf())).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void getAllRentals_ShouldReturnListOfRentals() throws Exception {
        List<RentalInfoDto> rentals = Arrays.asList(rentalInfoDto1, rentalInfoDto2);
        when(rentalService.getAllRentals()).thenReturn(rentals);

        mockMvc.perform(get("/api/rentals").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].userId").value(rentalInfoDto1.getUserId()))
                .andExpect(jsonPath("$[1].userId").value(rentalInfoDto2.getUserId()));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getRentalsByUser_ShouldReturnListOfRentals_WhenUserExists() throws Exception {
        List<RentalInfoDto> rentals = Arrays.asList(rentalInfoDto1, rentalInfoDto2);
        when(rentalService.getRentalsByUser(1L)).thenReturn(rentals);

        mockMvc.perform(get("/api/rentals/user/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].userId").value(rentalInfoDto1.getUserId()))
                .andExpect(jsonPath("$[1].userId").value(rentalInfoDto2.getUserId()));
    }

    @Test
    @WithMockUser(roles = {"MANAGER", "ADMIN"})
    void getRentalsByScooter_ShouldReturnListOfRentals_WhenScooterExists() throws Exception {
        List<RentalInfoDto> rentals = Arrays.asList(rentalInfoDto1, rentalInfoDto2);
        when(rentalService.getRentalsByScooter(1L)).thenReturn(rentals);

        mockMvc.perform(get("/api/rentals/scooter/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].userId").value(rentalInfoDto1.getUserId()))
                .andExpect(jsonPath("$[1].userId").value(rentalInfoDto2.getUserId()));
    }
}

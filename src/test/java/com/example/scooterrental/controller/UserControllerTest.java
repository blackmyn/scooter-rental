package com.example.scooterrental.controller;

import com.example.scooterrental.dto.UserDto;
import com.example.scooterrental.dto.UserProfileDto;
import com.example.scooterrental.exception.UserAlreadyExistsException;
import com.example.scooterrental.exception.UserNotFoundException;
import com.example.scooterrental.service.UserService;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserService userService;

    private UserDto userDto1;
    private UserProfileDto userProfileDto1;

    @BeforeEach
    void setUp() {
        userDto1 =
                new UserDto(
                        null,
                        "testuser",
                        "password",
                        "Test",
                        "User",
                        "test@gmail.com",
                        "3754412312313",
                        Set.of("ROLE_USER"));
        userProfileDto1 =
                new UserProfileDto(
                        1L,
                        "testuser",
                        "Test",
                        "User",
                        "test@gmail.com",
                        "3754412312313",
                        Set.of("ROLE_USER"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createUser_ShouldReturnCreatedUser_WhenUserIsValid() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto1);

        mockMvc.perform(
                        post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto1))
                                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(userDto1.getUsername()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createUser_ShouldReturnBadRequest_WhenUserIsInvalid() throws Exception {
        UserDto invalidUserDto = new UserDto();

        mockMvc.perform(
                        post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidUserDto))
                                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getUserById_ShouldReturnUser_WhenUserExists() throws Exception {
        when(userService.getUserById(1L)).thenReturn(userProfileDto1);

        mockMvc.perform(get("/api/users/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(userProfileDto1.getUsername()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getUserById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        when(userService.getUserById(1L)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/1").with(csrf())).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(
            username = "testuser",
            roles = {"USER"})
    void updateUser_ShouldReturnUpdatedUser_WhenUserIsValidAndAuthorized() throws Exception {

        when(userService.updateUser(anyLong(), any(UserDto.class))).thenReturn(userProfileDto1);
        mockMvc.perform(
                        put("/api/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDto1))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(userProfileDto1.getUsername()));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteUser_ShouldReturnNoContent_WhenUserExistsAndAuthorized() throws Exception {
        mockMvc.perform(delete("/api/users/1").with(csrf())).andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        List<UserProfileDto> userProfileDtos =
                Arrays.asList(
                        userProfileDto1,
                        new UserProfileDto(
                                2L,
                                "testuser2",
                                "Test2",
                                "User2",
                                "test2@gmail.com",
                                "0987654321",
                                Set.of("ROLE_USER")));
        when(userService.getAllUsers()).thenReturn(userProfileDtos);

        mockMvc.perform(get("/api/users").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username").value(userProfileDto1.getUsername()))
                .andExpect(jsonPath("$[1].username").value("testuser2"));
    }
}

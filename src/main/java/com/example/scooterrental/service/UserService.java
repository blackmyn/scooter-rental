package com.example.scooterrental.service;

import com.example.scooterrental.dto.UserDto;
import com.example.scooterrental.dto.UserProfileDto;
import com.example.scooterrental.exception.UserAlreadyExistsException;
import com.example.scooterrental.exception.UserNotFoundException;
import com.example.scooterrental.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto) throws UserAlreadyExistsException;

    UserProfileDto getUserById(Long id) throws UserNotFoundException;

    UserProfileDto updateUser(Long id, UserDto userDto) throws UserNotFoundException;

    void deleteUser(Long id) throws UserNotFoundException;

    List<UserProfileDto> getAllUsers();

    User findByUsername(String username);
}

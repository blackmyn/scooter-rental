package com.example.scooterrental.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.scooterrental.dto.UserDto;
import com.example.scooterrental.dto.UserProfileDto;
import com.example.scooterrental.exception.UserAlreadyExistsException;
import com.example.scooterrental.exception.UserNotFoundException;
import com.example.scooterrental.model.Role;
import com.example.scooterrental.model.User;
import com.example.scooterrental.repository.RoleRepository;
import com.example.scooterrental.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock private UserRepository userRepository;

    @Mock private RoleRepository roleRepository;

    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserServiceImpl userService;

    private User user1;
    private UserDto userDto1;
    private Role roleUser;
    private Role roleAdmin;

    @BeforeEach
    void setUp() {
        roleUser = new Role(1L, "ROLE_USER");
        roleAdmin = new Role(2L, "ROLE_ADMIN");

        user1 =
                new User(
                        1L,
                        "testuser",
                        "password",
                        "Test",
                        "User",
                        "test@gmail.com",
                        "37523333333",
                        Set.of(roleUser));
        userDto1 =
                new UserDto(
                        null,
                        "testuser",
                        "password",
                        "Test",
                        "User",
                        "test@gmail.com",
                        "375441233133",
                        Set.of("ROLE_USER"));
    }

    @Test
    void createUser_ShouldReturnUserDto_WhenUserIsCreated() throws UserAlreadyExistsException {
        when(userRepository.existsByUsername(userDto1.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto1.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userDto1.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(roleUser));
        when(userRepository.save(any(User.class))).thenReturn(user1);

        UserDto createdUser = userService.createUser(userDto1);

        assertNotNull(createdUser);
        assertEquals(user1.getUsername(), createdUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowUserAlreadyExistsException_WhenUsernameExists() {
        when(userRepository.existsByUsername(userDto1.getUsername())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userDto1));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowUserAlreadyExistsException_WhenEmailExists() {
        when(userRepository.existsByUsername(userDto1.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto1.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userDto1));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_ShouldReturnUserProfileDto_WhenUserExists() throws UserNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        UserProfileDto userProfileDto = userService.getUserById(1L);

        assertNotNull(userProfileDto);
        assertEquals(user1.getId(), userProfileDto.getId());
        assertEquals(user1.getUsername(), userProfileDto.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserProfileDto_WhenUserExists()
            throws UserNotFoundException, UserAlreadyExistsException {
        UserDto userDtoUpdate =
                new UserDto(
                        1L,
                        "newuser",
                        "newpassword",
                        "New",
                        "User",
                        "new@gmail.com",
                        "375446345314",
                        Set.of("ROLE_ADMIN"));
        User updatedUser =
                new User(
                        1L,
                        "newuser",
                        "encodedPassword",
                        "New",
                        "User",
                        "new@gmail.com",
                        "37544343714",
                        Set.of(roleAdmin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(roleAdmin));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserProfileDto updatedUserProfileDto = userService.updateUser(1L, userDtoUpdate);

        assertNotNull(updatedUserProfileDto);
        assertEquals(updatedUser.getUsername(), updatedUserProfileDto.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        UserDto userDtoUpdate =
                new UserDto(
                        1L,
                        "newuser",
                        "newpassword",
                        "New",
                        "User",
                        "new@gmail.com",
                        "37523123321",
                        Set.of("ROLE_ADMIN"));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, userDtoUpdate));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() throws UserNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(user1);
    }

    @Test
    void deleteUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserProfileDtos() {
        List<User> users =
                Arrays.asList(
                        user1,
                        new User(
                                2L,
                                "testuser2",
                                "password",
                                "Test2",
                                "User2",
                                "test2@gmail.com",
                                "3756454545",
                                Set.of(roleUser)));
        when(userRepository.findAll()).thenReturn(users);

        List<UserProfileDto> userProfileDtos = userService.getAllUsers();

        assertNotNull(userProfileDtos);
        assertEquals(2, userProfileDtos.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user1));

        User foundUser = userService.findByUsername("testuser");

        assertNotNull(foundUser);
        assertEquals(user1.getUsername(), foundUser.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void findByUsername_ShouldReturnNull_WhenUserDoesNotExist() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        User foundUser = userService.findByUsername("testuser");

        assertNull(foundUser);
        verify(userRepository, times(1)).findByUsername("testuser");
    }
}

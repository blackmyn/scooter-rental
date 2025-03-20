package com.example.scooterrental.service.impl;

import com.example.scooterrental.dto.UserDto;
import com.example.scooterrental.dto.UserProfileDto;
import com.example.scooterrental.exception.UserAlreadyExistsException;
import com.example.scooterrental.exception.UserNotFoundException;
import com.example.scooterrental.model.Role;
import com.example.scooterrental.model.User;
import com.example.scooterrental.repository.RoleRepository;
import com.example.scooterrental.repository.UserRepository;
import com.example.scooterrental.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) throws UserAlreadyExistsException {
        logger.info("Попытка создать нового пользователя с данными: {}", userDto);

        try {
            if (userRepository.existsByUsername(userDto.getUsername())) {
                logger.error(
                        "Пользователь с именем пользователя {} уже существует.",
                        userDto.getUsername());
                throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");
            }
            if (userRepository.existsByEmail(userDto.getEmail())) {
                logger.error("Пользователь с email {} уже существует.", userDto.getEmail());
                throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
            }

            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setEmail(userDto.getEmail());
            user.setPhoneNumber(userDto.getPhoneNumber());

            Role userRole =
                    roleRepository
                            .findByName("ROLE_USER")
                            .orElseThrow(
                                    () -> {
                                        logger.error("Роль ROLE_USER не найдена.");
                                        return new RuntimeException("Роль ROLE_USER не найдена");
                                    });

            user.setRoles(Collections.singleton(userRole));

            user = userRepository.save(user);

            userDto.setId(user.getId());

            logger.info("Пользователь успешно создан с ID: {}", user.getId());
            return userDto;
        } catch (Exception e) {
            logger.error("Ошибка при создании пользователя: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getUserById(Long id) throws UserNotFoundException {
        logger.info("Попытка получить пользователя с ID: {}", id);

        try {
            User user =
                    userRepository
                            .findById(id)
                            .orElseThrow(
                                    () -> {
                                        logger.warn("Пользователь с ID {} не найден.", id);
                                        return new UserNotFoundException(
                                                "Пользователь с ID " + id + " не найден");
                                    });

            UserProfileDto userProfileDto = convertToUserProfileDto(user);
            logger.info("Пользователь с ID {} успешно получен.", id);
            return userProfileDto;
        } catch (Exception e) {
            logger.error("Ошибка при получении пользователя с ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public UserProfileDto updateUser(Long id, UserDto userDto) throws UserNotFoundException {
        logger.info("Попытка обновить пользователя с ID: {}, данные: {}", id, userDto);

        try {
            User user =
                    userRepository
                            .findById(id)
                            .orElseThrow(
                                    () -> {
                                        logger.warn("Пользователь с ID {} не найден.", id);
                                        return new UserNotFoundException(
                                                "Пользователь с ID " + id + " не найден");
                                    });

            if (userDto.getUsername() != null) {
                if (!userDto.getUsername().equals(user.getUsername())
                        && userRepository.existsByUsername(userDto.getUsername())) {
                    logger.error("Имя пользователя {} уже занято.", userDto.getUsername());
                    throw new UserAlreadyExistsException(
                            "Имя пользователя " + userDto.getUsername() + " уже занято");
                }
                user.setUsername(userDto.getUsername());
            }
            if (userDto.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }
            if (userDto.getFirstName() != null) {
                user.setFirstName(userDto.getFirstName());
            }
            if (userDto.getLastName() != null) {
                user.setLastName(userDto.getLastName());
            }
            if (userDto.getEmail() != null) {
                if (!userDto.getEmail().equals(user.getEmail())
                        && userRepository.existsByEmail(userDto.getEmail())) {
                    logger.error("Email {} уже занят.", userDto.getEmail());
                    throw new UserAlreadyExistsException(
                            "Email " + userDto.getEmail() + " уже занят");
                }
                user.setEmail(userDto.getEmail());
            }
            if (userDto.getPhoneNumber() != null) {
                user.setPhoneNumber(userDto.getPhoneNumber());
            }

            if (userDto.getRoles() != null) {
                Set<Role> newRoles = new HashSet<>();
                for (String roleName : userDto.getRoles()) {
                    Role role =
                            roleRepository
                                    .findByName(roleName)
                                    .orElseThrow(
                                            () -> {
                                                logger.error("Роль {} не найдена.", roleName);
                                                return new RuntimeException(
                                                        "Роль " + roleName + " не найдена");
                                            });
                    newRoles.add(role);
                }
                user.setRoles(newRoles);
            }

            userRepository.save(user);
            logger.info("Пользователь с ID {} успешно обновлен.", id);
            return convertToUserProfileDto(user);
        } catch (Exception e) {
            logger.error("Ошибка при обновлении пользователя с ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) throws UserNotFoundException {
        logger.info("Попытка удалить пользователя с ID: {}", id);

        try {
            User user =
                    userRepository
                            .findById(id)
                            .orElseThrow(
                                    () -> {
                                        logger.warn("Пользователь с ID {} не найден.", id);
                                        return new UserNotFoundException(
                                                "Пользователь с ID " + id + " не найден");
                                    });
            userRepository.delete(user);
            logger.info("Пользователь с ID {} успешно удален.", id);
        } catch (Exception e) {
            logger.error("Ошибка при удалении пользователя с ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileDto> getAllUsers() {
        logger.info("Попытка получить всех пользователей.");

        try {
            List<User> users = userRepository.findAll();
            List<UserProfileDto> userProfileDtos =
                    users.stream().map(this::convertToUserProfileDto).collect(Collectors.toList());
            logger.info("Получено {} пользователей.", users.size());
            return userProfileDtos;

        } catch (Exception e) {
            logger.error("Ошибка при получении всех пользователей: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        logger.info("Попытка найти пользователя по имени пользователя: {}", username);
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                logger.info("Пользователь с именем пользователя {} успешно найден.", username);
            } else {
                logger.warn("Пользователь с именем пользователя {} не найден.", username);
            }
            return user;
        } catch (Exception e) {
            logger.error(
                    "Ошибка при поиске пользователя по имени пользователя {}: {}",
                    username,
                    e.getMessage(),
                    e);
            throw e;
        }
    }

    private UserProfileDto convertToUserProfileDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        return dto;
    }
}

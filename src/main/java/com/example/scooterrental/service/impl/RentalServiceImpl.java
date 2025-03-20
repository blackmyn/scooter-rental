package com.example.scooterrental.service.impl;

import com.example.scooterrental.dto.RentalDto;
import com.example.scooterrental.dto.RentalInfoDto;
import com.example.scooterrental.exception.RentalNotFoundException;
import com.example.scooterrental.exception.ScooterNotFoundException;
import com.example.scooterrental.exception.UserNotFoundException;
import com.example.scooterrental.model.Rental;
import com.example.scooterrental.model.Scooter;
import com.example.scooterrental.model.ScooterStatus;
import com.example.scooterrental.model.Tariff;
import com.example.scooterrental.model.User;
import com.example.scooterrental.repository.RentalRepository;
import com.example.scooterrental.repository.ScooterRepository;
import com.example.scooterrental.repository.TariffRepository;
import com.example.scooterrental.repository.UserRepository;
import com.example.scooterrental.service.RentalService;
import com.example.scooterrental.service.ScooterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentalServiceImpl implements RentalService {

    private static final Logger logger = LoggerFactory.getLogger(RentalServiceImpl.class);

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final ScooterRepository scooterRepository;
    private final ScooterService scooterService;
    private final TariffRepository tariffRepository;

    @Autowired
    public RentalServiceImpl(
            RentalRepository rentalRepository,
            UserRepository userRepository,
            ScooterRepository scooterRepository,
            ScooterService scooterService,
            TariffRepository tariffRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.scooterRepository = scooterRepository;
        this.scooterService = scooterService;
        this.tariffRepository = tariffRepository;
    }

    @Override
    @Transactional
    public RentalDto createRental(RentalDto rentalDto)
            throws UserNotFoundException, ScooterNotFoundException {
        logger.info("Попытка создать запись аренды: {}", rentalDto);
        try {
            User user =
                    userRepository
                            .findById(rentalDto.getUserId())
                            .orElseThrow(
                                    () -> {
                                        logger.warn(
                                                "Пользователь с ID {} не найден.",
                                                rentalDto.getUserId());
                                        return new UserNotFoundException(
                                                "Пользователь с ID "
                                                        + rentalDto.getUserId()
                                                        + " не найден");
                                    });
            Scooter scooter =
                    scooterRepository
                            .findById(rentalDto.getScooterId())
                            .orElseThrow(
                                    () -> {
                                        logger.warn(
                                                "Самокат с ID {} не найден.",
                                                rentalDto.getScooterId());
                                        return new ScooterNotFoundException(
                                                "Самокат с ID "
                                                        + rentalDto.getScooterId()
                                                        + " не найден");
                                    });

            if (scooter.getStatus() != ScooterStatus.AVAILABLE) {
                logger.error("Самокат с ID {} недоступен для аренды.", scooter.getId());
                throw new IllegalArgumentException("Самокат недоступен для аренды");
            }

            Tariff tariff =
                    tariffRepository
                            .findById(rentalDto.getTariffId())
                            .orElseThrow(() -> new IllegalArgumentException("Тариф не найден"));

            Rental rental = new Rental();
            rental.setUser(user);
            rental.setScooter(scooter);
            rental.setStartTime(rentalDto.getStartTime());
            rental.setStartMileage(scooter.getMileage());
            rental.setTariff(tariff);

            scooterService.updateScooterStatus(scooter.getId(), ScooterStatus.IN_USE);

            rental = rentalRepository.save(rental);
            rentalDto.setId(rental.getId());
            logger.info("Запись аренды успешно создана с ID: {}", rental.getId());
            return rentalDto;
        } catch (Exception e) {
            logger.error("Ошибка при создании записи аренды: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RentalInfoDto getRentalById(Long id) throws RentalNotFoundException {
        logger.info("Попытка получить запись аренды с ID: {}", id);
        try {
            Rental rental =
                    rentalRepository
                            .findById(id)
                            .orElseThrow(
                                    () -> {
                                        logger.warn("Аренда с ID {} не найдена.", id);
                                        return new RentalNotFoundException(
                                                "Аренда с ID " + id + " не найдена");
                                    });
            RentalInfoDto rentalInfoDto = convertToRentalInfoDto(rental);
            logger.info("Запись аренды с ID {} успешно получена.", id);
            return rentalInfoDto;
        } catch (Exception e) {
            logger.error("Ошибка при получении записи аренды с ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public RentalDto endRental(Long id) throws RentalNotFoundException, ScooterNotFoundException {
        logger.info("Попытка завершить аренду с ID: {}", id);
        try {
            Rental rental =
                    rentalRepository
                            .findById(id)
                            .orElseThrow(
                                    () -> {
                                        logger.warn("Аренда с ID {} не найдена.", id);
                                        return new RentalNotFoundException(
                                                "Аренда с ID " + id + " не найдена");
                                    });

            if (rental.getEndTime() != null) {
                logger.error("Аренда с ID {} уже завершена.", id);
                throw new IllegalArgumentException("Аренда уже завершена");
            }

            rental.setEndTime(LocalDateTime.now());
            Scooter scooter = rental.getScooter();
            rental.setEndMileage(scooter.getMileage());

            double cost = calculateCost(rental);
            rental.setTotalCost(cost);

            scooterService.updateScooterStatus(scooter.getId(), ScooterStatus.AVAILABLE);
            rentalRepository.save(rental);

            RentalDto rentalDto = new RentalDto();
            rentalDto.setId(rental.getId());
            rentalDto.setUserId(rental.getUser().getId());
            rentalDto.setScooterId(rental.getScooter().getId());
            rentalDto.setStartTime(rental.getStartTime());
            rentalDto.setEndTime(rental.getEndTime());
            rentalDto.setStartMileage(rental.getStartMileage());
            rentalDto.setEndMileage(rental.getEndMileage());
            rentalDto.setTotalCost(rental.getTotalCost());
            rentalDto.setTariffId(rental.getTariff().getId());

            logger.info("Аренда с ID {} успешно завершена.", id);
            return rentalDto;
        } catch (Exception e) {
            logger.error("Ошибка при завершении аренды с ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    private double calculateCost(Rental rental) {
        Duration duration = Duration.between(rental.getStartTime(), rental.getEndTime());
        double hours = duration.toMinutes() / 60.0;
        double cost = 0;
        Tariff tariff = rental.getTariff();

        if (tariff.getIsSubscription()) {
            cost = tariff.getSubscriptionPrice();
        } else {
            cost = hours * tariff.getPricePerHour();
        }
        //скидка
        if (tariff.getDiscount() != null && tariff.getDiscount() > 0) {
            cost = cost * (1 - tariff.getDiscount());
        }
        return cost;
    }

    @Override
    public List<RentalInfoDto> getAllRentals() {
        logger.info("Попытка получить все записи аренды.");
        try {
            List<RentalInfoDto> rentals =
                    rentalRepository.findAll().stream()
                            .map(this::convertToRentalInfoDto)
                            .collect(Collectors.toList());
            logger.info("Получено {} записей аренды.", rentals.size());
            return rentals;
        } catch (Exception e) {
            logger.error("Ошибка при получении всех записей аренды: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalInfoDto> getRentalsByUser(Long userId) {
        logger.info("Попытка получить записи аренды для пользователя с ID: {}", userId);
        try {
            List<RentalInfoDto> rentals =
                    rentalRepository.findByUserId(userId).stream()
                            .map(this::convertToRentalInfoDto)
                            .collect(Collectors.toList());
            logger.info(
                    "Получено {} записей аренды для пользователя с ID {}.", rentals.size(), userId);
            return rentals;
        } catch (Exception e) {
            logger.error(
                    "Ошибка при получении записей аренды для пользователя с ID {}: {}",
                    userId,
                    e.getMessage(),
                    e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalInfoDto> getRentalsByScooter(Long scooterId) {
        logger.info("Попытка получить записи аренды для самоката с ID: {}", scooterId);
        try {
            List<RentalInfoDto> rentals =
                    rentalRepository.findByScooterId(scooterId).stream()
                            .map(this::convertToRentalInfoDto)
                            .collect(Collectors.toList());
            logger.info(
                    "Получено {} записей аренды для самоката с ID {}.", rentals.size(), scooterId);
            return rentals;
        } catch (Exception e) {
            logger.error(
                    "Ошибка при получении записей аренды для самоката с ID {}: {}",
                    scooterId,
                    e.getMessage(),
                    e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalInfoDto> getRentalHistoryByScooter(Long scooterId) {
        logger.info("Попытка получить историю аренды для самоката с ID: {}", scooterId);
        try {
            List<RentalInfoDto> rentals =
                    rentalRepository.findByScooterId(scooterId).stream()
                            .map(this::convertToRentalInfoDto)
                            .collect(Collectors.toList());
            logger.info(
                    "Получено {} записей истории аренды для самоката с ID {}.",
                    rentals.size(),
                    scooterId);
            return rentals;
        } catch (Exception e) {
            logger.error(
                    "Ошибка при получении истории аренды для самоката с ID {}: {}",
                    scooterId,
                    e.getMessage(),
                    e);
            throw e;
        }
    }

    private RentalInfoDto convertToRentalInfoDto(Rental rental) {
        RentalInfoDto dto = new RentalInfoDto();
        dto.setId(rental.getId());
        dto.setUserId(rental.getUser().getId());
        dto.setUserUsername(rental.getUser().getUsername());
        dto.setScooterId(rental.getScooter().getId());
        dto.setScooterModel(rental.getScooter().getModel());
        dto.setStartTime(rental.getStartTime());
        dto.setEndTime(rental.getEndTime());
        dto.setStartMileage(rental.getStartMileage());
        dto.setEndMileage(rental.getEndMileage());
        dto.setTotalCost(rental.getTotalCost());
        if (rental.getTariff() != null) {
            dto.setTariffId(rental.getTariff().getId());
            dto.setTariffName(rental.getTariff().getName());
        }
        return dto;
    }
}

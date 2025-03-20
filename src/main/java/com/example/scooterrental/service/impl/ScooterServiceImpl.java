package com.example.scooterrental.service.impl;

import com.example.scooterrental.dto.ScooterDto;
import com.example.scooterrental.dto.ScooterInfoDto;
import com.example.scooterrental.exception.RentalPointNotFoundException;
import com.example.scooterrental.exception.ScooterNotFoundException;
import com.example.scooterrental.exception.TariffNotFoundException;
import com.example.scooterrental.model.RentalPoint;
import com.example.scooterrental.model.Scooter;
import com.example.scooterrental.model.ScooterStatus;
import com.example.scooterrental.model.Tariff;
import com.example.scooterrental.repository.RentalPointRepository;
import com.example.scooterrental.repository.ScooterRepository;
import com.example.scooterrental.repository.TariffRepository;
import com.example.scooterrental.service.ScooterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScooterServiceImpl implements ScooterService {

    private static final Logger logger = LoggerFactory.getLogger(ScooterServiceImpl.class);

    private final ScooterRepository scooterRepository;
    private final RentalPointRepository rentalPointRepository;
    private final TariffRepository tariffRepository;

    @Autowired
    public ScooterServiceImpl(
            ScooterRepository scooterRepository,
            RentalPointRepository rentalPointRepository,
            TariffRepository tariffRepository) {
        this.scooterRepository = scooterRepository;
        this.rentalPointRepository = rentalPointRepository;
        this.tariffRepository = tariffRepository;
    }

    @Override
    @Transactional
    public ScooterDto createScooter(ScooterDto scooterDto)
            throws RentalPointNotFoundException, TariffNotFoundException {
        logger.info("Попытка создать новый самокат с данными: {}", scooterDto);
        try {
            if (scooterRepository.existsBySerialNumber(scooterDto.getSerialNumber())) {
                logger.error(
                        "Самокат с серийным номером {} уже существует.",
                        scooterDto.getSerialNumber());
                throw new IllegalArgumentException(
                        "Самокат с таким серийным номером уже существует");
            }
            Scooter scooter = new Scooter();
            scooter.setModel(scooterDto.getModel());
            scooter.setSerialNumber(scooterDto.getSerialNumber());
            scooter.setStatus(scooterDto.getStatus());
            scooter.setChargeLevel(scooterDto.getChargeLevel());
            scooter.setMileage(scooterDto.getMileage());

            if (scooterDto.getRentalPointId() != null) {
                RentalPoint rentalPoint =
                        rentalPointRepository
                                .findById(scooterDto.getRentalPointId())
                                .orElseThrow(
                                        () -> {
                                            logger.error(
                                                    "Точка проката с ID {} не найдена.",
                                                    scooterDto.getRentalPointId());
                                            return new RentalPointNotFoundException(
                                                    "Точка проката с ID "
                                                            + scooterDto.getRentalPointId()
                                                            + " не найдена");
                                        });
                scooter.setRentalPoint(rentalPoint);
            }
            if (scooterDto.getTariffId() != null) {
                Tariff tariff =
                        tariffRepository
                                .findById(scooterDto.getTariffId())
                                .orElseThrow(
                                        () -> {
                                            logger.error(
                                                    "Тариф с ID {} не найден.",
                                                    scooterDto.getTariffId());
                                            return new TariffNotFoundException(
                                                    "Тариф с ID "
                                                            + scooterDto.getTariffId()
                                                            + " не найден");
                                        });
                scooter.setTariff(tariff);
            }

            scooter = scooterRepository.save(scooter);
            scooterDto.setId(scooter.getId());

            logger.info("Самокат успешно создан с ID: {}", scooter.getId());
            return scooterDto;
        } catch (Exception e) {
            logger.error("Ошибка при создании самоката: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ScooterInfoDto getScooterById(Long id) throws ScooterNotFoundException {
        logger.info("Попытка получить самокат с ID: {}", id);
        try {
            Scooter scooter =
                    scooterRepository
                            .findById(id)
                            .orElseThrow(
                                    () -> {
                                        logger.warn("Самокат с ID {} не найден.", id);
                                        return new ScooterNotFoundException(
                                                "Самокат с ID " + id + " не найден");
                                    });
            ScooterInfoDto scooterInfoDto = convertToScooterInfoDto(scooter);
            logger.info("Самокат с ID {} успешно получен.", id);
            return scooterInfoDto;

        } catch (Exception e) {
            logger.error("Ошибка при получении самоката с ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ScooterDto updateScooter(Long id, ScooterDto scooterDto)
            throws ScooterNotFoundException, RentalPointNotFoundException, TariffNotFoundException {
        logger.info("Попытка обновить самокат с ID: {}, данные: {}", id, scooterDto);
        try {
            Scooter scooter =
                    scooterRepository
                            .findById(id)
                            .orElseThrow(
                                    () -> {
                                        logger.warn("Самокат с ID {} не найден.", id);
                                        return new ScooterNotFoundException(
                                                "Самокат с ID " + id + " не найден");
                                    });

            if (scooterDto.getModel() != null) {
                scooter.setModel(scooterDto.getModel());
            }
            if (scooterDto.getSerialNumber() != null) {
                if (!scooterDto.getSerialNumber().equals(scooter.getSerialNumber())
                        && scooterRepository.existsBySerialNumber(scooterDto.getSerialNumber())) {
                    logger.error("Серийный номер {} уже занят.", scooterDto.getSerialNumber());
                    throw new IllegalArgumentException(
                            "Серийный номер " + scooterDto.getSerialNumber() + " уже занят");
                }
                scooter.setSerialNumber(scooterDto.getSerialNumber());
            }
            if (scooterDto.getStatus() != null) {
                scooter.setStatus(scooterDto.getStatus());
            }
            if (scooterDto.getChargeLevel() != null) {
                scooter.setChargeLevel(scooterDto.getChargeLevel());
            }
            if (scooterDto.getMileage() != null) {
                scooter.setMileage(scooterDto.getMileage());
            }

            if (scooterDto.getRentalPointId() != null) {
                RentalPoint rentalPoint =
                        rentalPointRepository
                                .findById(scooterDto.getRentalPointId())
                                .orElseThrow(
                                        () -> {
                                            logger.error(
                                                    "Точка проката с ID {} не найдена.",
                                                    scooterDto.getRentalPointId());
                                            return new RentalPointNotFoundException(
                                                    "Точка проката с ID "
                                                            + scooterDto.getRentalPointId()
                                                            + " не найдена");
                                        });
                scooter.setRentalPoint(rentalPoint);
            }

            if (scooterDto.getTariffId() != null) {
                Tariff tariff =
                        tariffRepository
                                .findById(scooterDto.getTariffId())
                                .orElseThrow(
                                        () -> {
                                            logger.error(
                                                    "Тариф с ID {} не найден.",
                                                    scooterDto.getTariffId());
                                            return new TariffNotFoundException(
                                                    "Тариф с ID "
                                                            + scooterDto.getTariffId()
                                                            + " не найден");
                                        });
                scooter.setTariff(tariff);
            }

            scooterRepository.save(scooter);
            logger.info("Самокат с ID {} успешно обновлен.", id);
            return scooterDto;
        } catch (Exception e) {
            logger.error("Ошибка при обновлении самоката с ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteScooter(Long id) throws ScooterNotFoundException {
        logger.info("Попытка удалить самокат с ID: {}", id);
        try {
            Scooter scooter =
                    scooterRepository
                            .findById(id)
                            .orElseThrow(
                                    () -> {
                                        logger.warn("Самокат с ID {} не найден.", id);
                                        return new ScooterNotFoundException(
                                                "Самокат с ID " + id + " не найден");
                                    });
            scooterRepository.delete(scooter);
            logger.info("Самокат с ID {} успешно удален.", id);
        } catch (Exception e) {
            logger.error("Ошибка при удалении самоката с ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScooterInfoDto> getAllScooters() {
        logger.info("Попытка получить все самокаты.");
        try {
            List<ScooterInfoDto> scooters =
                    scooterRepository.findAll().stream()
                            .map(this::convertToScooterInfoDto)
                            .collect(Collectors.toList());
            logger.info("Получено {} самокатов.", scooters.size());
            return scooters;
        } catch (Exception e) {
            logger.error("Ошибка при получении всех самокатов: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScooterInfoDto> getScootersByRentalPoint(Long rentalPointId)
            throws RentalPointNotFoundException {
        logger.info("Попытка получить самокаты для точки проката с ID: {}", rentalPointId);
        try {
            if (!rentalPointRepository.existsById(rentalPointId)) {
                logger.warn("Точка проката с ID {} не найдена.", rentalPointId);
                throw new RentalPointNotFoundException(
                        "Точка проката с ID " + rentalPointId + " не найдена");
            }
            List<ScooterInfoDto> scooters =
                    scooterRepository.findByRentalPointId(rentalPointId).stream()
                            .map(this::convertToScooterInfoDto)
                            .collect(Collectors.toList());
            logger.info(
                    "Получено {} самокатов для точки проката с ID {}.",
                    scooters.size(),
                    rentalPointId);
            return scooters;
        } catch (Exception e) {
            logger.error(
                    "Ошибка при получении самокатов для точки проката с ID {}: {}",
                    rentalPointId,
                    e.getMessage(),
                    e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void updateScooterStatus(Long scooterId, ScooterStatus newStatus)
            throws ScooterNotFoundException {
        logger.info("Попытка обновить статус самоката с ID: {} на {}", scooterId, newStatus);
        try {
            Scooter scooter =
                    scooterRepository
                            .findById(scooterId)
                            .orElseThrow(
                                    () -> {
                                        logger.warn("Самокат с ID {} не найден.", scooterId);
                                        return new ScooterNotFoundException(
                                                "Самокат с ID " + scooterId + " не найден");
                                    });
            scooter.setStatus(newStatus);
            scooterRepository.save(scooter);
            logger.info("Статус самоката с ID {} успешно обновлен на {}", scooterId, newStatus);
        } catch (Exception e) {
            logger.error(
                    "Ошибка при обновлении статуса самоката с ID {}: {}",
                    scooterId,
                    e.getMessage(),
                    e);
            throw e;
        }
    }

    private ScooterInfoDto convertToScooterInfoDto(Scooter scooter) {
        ScooterInfoDto dto = new ScooterInfoDto();
        dto.setId(scooter.getId());
        dto.setModel(scooter.getModel());
        dto.setSerialNumber(scooter.getSerialNumber());
        dto.setStatus(scooter.getStatus());
        dto.setChargeLevel(scooter.getChargeLevel());
        dto.setMileage(scooter.getMileage());
        if (scooter.getRentalPoint() != null) {
            dto.setRentalPointId(scooter.getRentalPoint().getId());
            dto.setRentalPointName(scooter.getRentalPoint().getName());
        }
        if (scooter.getTariff() != null) {
            dto.setTariffId(scooter.getTariff().getId());
            dto.setTariffName(scooter.getTariff().getName());
        }
        return dto;
    }
}

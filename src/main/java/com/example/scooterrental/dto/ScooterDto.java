package com.example.scooterrental.dto;

import com.example.scooterrental.model.ScooterStatus;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScooterDto {

    private Long id;

    @NotBlank(message = "Модель не может быть пустой")
    @Size(max = 100, message = "Модель не может превышать 100 символов")
    private String model;

    @NotBlank(message = "Серийный номер не может быть пустым")
    @Size(max = 50, message = "Серийный номер не может превышать 50 символов")
    private String serialNumber;

    @NotNull(message = "Статус не может быть null")
    private ScooterStatus status;

    @NotNull(message = "Уровень заряда не может быть null")
    @Min(value = 0, message = "Уровень заряда не может быть отрицательным")
    @Max(value = 100, message = "Уровень заряда не может превышать 100")
    private Integer chargeLevel;

    @PositiveOrZero(message = "Пробег не может быть отрицательным")
    private Double mileage;

    private Long rentalPointId;

    private Long tariffId;
}

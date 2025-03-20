package com.example.scooterrental.dto;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalDto {
    private Long id;

    @NotNull(message = "ID пользователя не может быть null")
    private Long userId;

    @NotNull(message = "ID самоката не может быть null")
    private Long scooterId;

    @NotNull(message = "Время начала аренды не может быть null")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Double startMileage;
    private Double endMileage;

    private Double totalCost;

    private Long tariffId;
}

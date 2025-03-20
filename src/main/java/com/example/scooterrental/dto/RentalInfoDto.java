package com.example.scooterrental.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalInfoDto {
    private Long id;
    private Long userId;
    private String userUsername;
    private Long scooterId;
    private String scooterModel;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double startMileage;
    private Double endMileage;
    private Double totalCost;
    private Long tariffId;
    private String tariffName;
}

package com.example.scooterrental.dto;

import com.example.scooterrental.model.ScooterStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScooterInfoDto {
    private Long id;
    private String model;
    private String serialNumber;
    private ScooterStatus status;
    private Integer chargeLevel;
    private Double mileage;
    private Long rentalPointId;
    private String rentalPointName;
    private Long tariffId;
    private String tariffName;
}

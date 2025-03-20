package com.example.scooterrental.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalPointInfoDto {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Long parentPointId;
    private List<RentalPointInfoDto> childPoints;
    private List<ScooterInfoDto> scooters;
}

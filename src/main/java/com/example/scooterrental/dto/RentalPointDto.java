package com.example.scooterrental.dto;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalPointDto {

    private Long id;

    @NotBlank(message = "Название точки проката не может быть пустым")
    @Size(max = 100, message = "Название точки проката не может превышать 100 символов")
    private String name;

    @NotBlank(message = "Адрес не может быть пустым")
    @Size(max = 255, message = "Адрес не может превышать 255 символов")
    private String address;

    @NotNull(message = "Широта не может быть null")
    private Double latitude;

    @NotNull(message = "Долгота не может быть null")
    private Double longitude;

    private Long parentPointId;
}

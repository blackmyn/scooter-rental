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
public class TariffDto {

    private Long id;

    @NotBlank(message = "Название тарифа не может быть пустым")
    @Size(max = 50, message = "Название тарифа не может превышать 50 символов")
    private String name;

    @Size(max = 255, message = "Описание тарифа не может превышать 255 символов")
    private String description;

    @PositiveOrZero(message = "Цена за час не может быть отрицательной")
    private Double pricePerHour;

    @PositiveOrZero(message = "Цена абонемента не может быть отрицательной")
    private Double subscriptionPrice;

    @PositiveOrZero(message = "Скидка не может быть отрицательной")
    private Double discount;

    @NotNull(message = "Поле 'isSubscription' не может быть null")
    private Boolean isSubscription;
}

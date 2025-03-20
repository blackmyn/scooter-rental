package com.example.scooterrental.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tariffs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "price_per_hour")
    private Double pricePerHour;

    @Column(name = "subscription_price")
    private Double subscriptionPrice;

    @Column(name = "discount")
    private Double discount;

    @Column(name = "is_subscription", nullable = false)
    private Boolean isSubscription;
}

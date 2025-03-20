package com.example.scooterrental.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "rental_points")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @OneToMany(
            mappedBy = "rentalPoint",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Scooter> scooters;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_point_id")
    private RentalPoint parentPoint;

    @OneToMany(mappedBy = "parentPoint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RentalPoint> childPoints;
}

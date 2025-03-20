package com.example.scooterrental.repository;

import com.example.scooterrental.model.Scooter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScooterRepository extends JpaRepository<Scooter, Long> {
    List<Scooter> findByRentalPointId(Long rentalPointId);

    boolean existsBySerialNumber(String serialNumber);
}

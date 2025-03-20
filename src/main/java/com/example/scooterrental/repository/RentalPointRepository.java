package com.example.scooterrental.repository;

import com.example.scooterrental.model.RentalPoint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalPointRepository extends JpaRepository<RentalPoint, Long> {
    @Query("SELECT rp FROM RentalPoint rp WHERE rp.parentPoint IS NULL")
    List<RentalPoint> findRootRentalPoints();

    @Query("SELECT rp FROM RentalPoint rp WHERE rp.parentPoint.id = :parentId")
    List<RentalPoint> findByParentPointId(@Param("parentId") Long parentId);

    Optional<RentalPoint> findByIdAndParentPointIsNull(Long id);
}

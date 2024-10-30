package com.example.deepsea.repository;

import com.example.deepsea.entity.VesselStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VesselStatisticsRepository extends JpaRepository<VesselStatistics, Long> {
}

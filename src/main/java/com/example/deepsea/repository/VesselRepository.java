package com.example.deepsea.repository;

import com.example.deepsea.entity.VesselData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VesselRepository extends JpaRepository<VesselData, Long> {
    Optional<List<VesselData>> findAllByVesselCode(Long vesselCode, Pageable pageable);

    Optional<List<VesselData>> findByIsValid(Boolean isValid);

    Page<VesselData> findAllByIsValid(Boolean isValid, Pageable pageable);
}

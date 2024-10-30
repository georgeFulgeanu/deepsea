package com.example.deepsea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VesselStatistics {

    public VesselStatistics(Integer id, Long vesselCode, LocalDateTime dateOfStatistic, Long recordsWithNullValues, Long recordsWithInvalidValues) {
        this.id = id;
        this.vesselCode = vesselCode;
        this.dateOfStatistic = dateOfStatistic;
        this.recordsWithNullValues = recordsWithNullValues;
        this.recordsWithInvalidValues = recordsWithInvalidValues;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private Long vesselCode;

    private LocalDateTime dateOfStatistic;

    private Long recordsWithNullValues;
    private Long recordsWithInvalidValues;

    private Double meanDeviationOfSpeed;
    private Double standardDeviationOfSpeed;
    private Double compliancePercentage;
}

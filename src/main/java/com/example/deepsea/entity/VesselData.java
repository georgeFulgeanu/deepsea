package com.example.deepsea.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(indexes = @Index(columnList = "localDateTime", name = "vessel_data_localDateTime_idx"))
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VesselData {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private Long vesselCode;
    @Column(nullable = false)
    private LocalDateTime localDateTime;
    private Double latitude;
    private Double longitude;
    private Double power;
    private Double fuelConsumption;
    private Double predictedFuelConsumption;
    private Double differenceBetweenPredictedAndActualFuelConsumption;
    private Double actualSpeed;
    private Double proposedSpeed;
    private Double differenceBetweenActualAndProposedSpeed;

    private Boolean isValid = true;

}

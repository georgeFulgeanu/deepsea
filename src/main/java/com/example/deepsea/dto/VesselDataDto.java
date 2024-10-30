package com.example.deepsea.dto;


import java.time.LocalDateTime;

public record VesselDataDto(Long vesselCode,
                            LocalDateTime localDateTime,
                            Double latitude,
                            Double longitude,
                            Double power,
                            Double fuelConsumption,
                            Double predictedFuelConsumption,
                            Double differenceBetweenPredictedAndActualFuelConsumption,
                            Double actualSpeed,
                            Double proposedSpeed,
                            Double differenceBetweenActualAndProposedSpeed) {}

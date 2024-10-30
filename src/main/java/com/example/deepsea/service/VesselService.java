package com.example.deepsea.service;

import com.example.deepsea.dto.VesselDataDto;
import com.example.deepsea.exception.VesselNotFoundException;
import com.example.deepsea.repository.VesselRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class VesselService {

    private final VesselRepository vesselRepository;

    public List<VesselDataDto> getByVesselCode(Long vesselCode, Pageable pageable) {
        return vesselRepository.findAllByVesselCode(vesselCode, pageable)
                .orElseThrow(() -> new VesselNotFoundException(vesselCode))
                .stream()
//                to be replaced with mapper
                .map(vesselData -> new VesselDataDto(vesselData.getVesselCode(),
                        vesselData.getLocalDateTime(),
                        vesselData.getLatitude(),
                        vesselData.getLongitude(),
                        vesselData.getPower(),
                        vesselData.getFuelConsumption(),
                        vesselData.getPredictedFuelConsumption(),
                        vesselData.getDifferenceBetweenPredictedAndActualFuelConsumption(),
                        vesselData.getActualSpeed(),
                        vesselData.getProposedSpeed(),
                        vesselData.getDifferenceBetweenActualAndProposedSpeed()
                ))
                .toList();
    }
}


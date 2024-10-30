package com.example.deepsea.controller;

import com.example.deepsea.dto.VesselDataDto;
import com.example.deepsea.service.VesselService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VesselController {

    private final VesselService vesselService;

    @GetMapping("/vessels/{vesselCode}")
    public List<VesselDataDto> getVessel(@PathVariable Long vesselCode,
                                         @PageableDefault(value = 100, page = 0) Pageable pageable) {
        return vesselService.getByVesselCode(vesselCode, pageable);
    }
}

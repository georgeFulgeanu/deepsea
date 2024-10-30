package com.example.deepsea.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

@Data
@AllArgsConstructor
public class StatisticsBean {
    private DescriptiveStatistics statistics;
    private long complianceCount;
    private long totalRecords;
}

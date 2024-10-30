package com.example.deepsea.service;

import com.example.deepsea.dto.StatisticsBean;
import com.example.deepsea.entity.VesselData;
import com.example.deepsea.entity.VesselStatistics;
import com.example.deepsea.repository.VesselRepository;
import com.example.deepsea.repository.VesselStatisticsRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@Component
@Slf4j
public class VesselDataSeeder {

    private final VesselRepository vesselRepository;
    private final VesselStatisticsRepository vesselStatisticsRepository;
    private final ResourceLoader resourceLoader;
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void readVesselData(String filePath) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + filePath);
        try (Stream<String> lines = Files.lines(resource.getFile().toPath())) {
            lines.skip(1).forEach(line -> {
                // Process each line here
//                validate input
                String[] fields = line.split(",");
                if (fields.length != 9) {
                    log.error(" Invalid number of fields for line: {}", line);
                    return;
                }

//                TODO: to be replaced by mapper
                VesselData vesselData = VesselData.builder()
                        .vesselCode(Long.parseLong(Objects.requireNonNull(extractStringFieldValue(fields[0]))))
                        .localDateTime(LocalDateTime.parse(Objects.requireNonNull(extractStringFieldValue(fields[1])), formatter))
                        .latitude(extractDoubleFieldValue(fields[2]))
                        .longitude(extractDoubleFieldValue(fields[3]))
                        .power(extractDoubleFieldValue(fields[4]))
                        .fuelConsumption(extractDoubleFieldValue(fields[5]))
                        .actualSpeed(extractDoubleFieldValue(fields[6]))
                        .proposedSpeed(extractDoubleFieldValue(fields[7]))
                        .predictedFuelConsumption(extractDoubleFieldValue(fields[8]))
                        .isValid(true)
                        .build();
//                validate data, TODO: to be replaced by validator class
                if (vesselData.getLatitude() == null || vesselData.getLongitude() == null ||
                        vesselData.getProposedSpeed() == null || vesselData.getActualSpeed() == null ||
                        vesselData.getPredictedFuelConsumption() == null || vesselData.getFuelConsumption() == null ||
                        vesselData.getPower() == null) {

                    vesselData.setIsValid(false);
                } else if (vesselData.getPower() < 0 ||
                        vesselData.getProposedSpeed() < 0 || vesselData.getActualSpeed() < 0 ||
                        vesselData.getPredictedFuelConsumption() < 0 || vesselData.getFuelConsumption() < 0 ||
                        vesselData.getLongitude() > 180 || vesselData.getLongitude() < -180 ||
                        vesselData.getLatitude() > 90 || vesselData.getLatitude() < -90) {
                    vesselData.setIsValid(false);
                }

//                new metric calculation, TODO: to be replaced by enhancer class
                if (vesselData.getIsValid()) {
                    vesselData.setDifferenceBetweenPredictedAndActualFuelConsumption(vesselData.getPredictedFuelConsumption() - vesselData.getFuelConsumption());
                    vesselData.setDifferenceBetweenActualAndProposedSpeed(vesselData.getActualSpeed() - vesselData.getProposedSpeed());
                }
                vesselRepository.save(vesselData);
                System.out.println(line);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String extractStringFieldValue(String field) {
        if (field.isEmpty() || field.isBlank() || field.equalsIgnoreCase("null")) {
            return null;
        }
        return field.substring(1, field.length() - 1);
    }

    private static Double extractDoubleFieldValue(String field) {
        if (field.isEmpty() || field.isBlank() || field.equalsIgnoreCase("null")) {
            return null;
        }
        return Double.parseDouble(field.substring(1, field.length() - 1));
    }

    public void processVesselStatistics() {
        computeInvalidRecordsStatistics();
        computeValidRecordsStatistics();


    }

    private void computeValidRecordsStatistics() {
        int page = 0;
        int pageSize = 100;
        Page<VesselData> validRecords;
        Map<Long, StatisticsBean> statisticsMap = new ConcurrentHashMap<>();
        do {
            validRecords = vesselRepository.findAllByIsValid(Boolean.TRUE, PageRequest.of(page, pageSize));
            for (VesselData vesselData : validRecords.getContent()) {
                statisticsMap.computeIfAbsent(vesselData.getVesselCode(), key -> new StatisticsBean(new DescriptiveStatistics(), 0, 0));
                double difference = Math.abs(vesselData.getProposedSpeed() - vesselData.getActualSpeed());

                statisticsMap.get(vesselData.getVesselCode()).getStatistics().addValue(difference);
                if (Math.abs(difference / vesselData.getProposedSpeed()) <= 0.1) {
                    statisticsMap.get(vesselData.getVesselCode()).setComplianceCount(statisticsMap.get(vesselData.getVesselCode()).getComplianceCount() + 1);
                }
                statisticsMap.get(vesselData.getVesselCode()).setTotalRecords(statisticsMap.get(vesselData.getVesselCode()).getTotalRecords() + 1);
            }
            page++;
        } while (validRecords.hasNext());
//        Calculate statistics
        statisticsMap.forEach((key, value) -> {
            VesselStatistics vesselStatistics = new VesselStatistics();
            vesselStatistics.setMeanDeviationOfSpeed(value.getStatistics().getMean());
            vesselStatistics.setStandardDeviationOfSpeed(value.getStatistics().getStandardDeviation());
            vesselStatistics.setCompliancePercentage(value.getComplianceCount() * 100.0 / value.getTotalRecords());
            vesselStatistics.setVesselCode(key);
            log.info(" Valid record statistics for vesselCode: {}", key);
            log.info("Mean Deviation: {}", vesselStatistics.getMeanDeviationOfSpeed());
            log.info("Standard Deviation: {}", vesselStatistics.getStandardDeviationOfSpeed());
            log.info("Compliance Percentage: {} %", vesselStatistics.getCompliancePercentage());
        });
    }

    private void computeInvalidRecordsStatistics() {
        List<VesselData> invalidRecords = vesselRepository.findByIsValid(Boolean.FALSE)
                .orElse(List.of());
        if (invalidRecords.isEmpty()) {
            log.warn(" No invalid records found today");
            return;
        }
        Map<Long, VesselStatistics> vesselStatisticsMap = new ConcurrentHashMap<>();
        invalidRecords.forEach(record -> {
            vesselStatisticsMap.computeIfAbsent(record.getVesselCode(), key -> new VesselStatistics(null, key, LocalDateTime.now(), 0L, 0L));
            if (record.getProposedSpeed() == null || record.getActualSpeed() == null ||
                    record.getPredictedFuelConsumption() == null || record.getFuelConsumption() == null ||
                    record.getPower() == null ||
                    record.getLatitude() == null || record.getLongitude() == null) {

                VesselStatistics vesselStatistics = vesselStatisticsMap.get(record.getVesselCode());
                vesselStatistics.setRecordsWithNullValues(vesselStatistics.getRecordsWithNullValues() + 1);
            } else if (record.getProposedSpeed() < 0 || record.getActualSpeed() < 0 ||
                    record.getFuelConsumption() < 0 || record.getPredictedFuelConsumption() < 0 ||
                    record.getPower() < 0) {

                VesselStatistics vesselStatistics = vesselStatisticsMap.get(record.getVesselCode());
                vesselStatistics.setRecordsWithInvalidValues(vesselStatistics.getRecordsWithInvalidValues() + 1);
            }
        });
        vesselStatisticsRepository.saveAll(vesselStatisticsMap.values());
    }
}

package com.example.deepsea;

import com.example.deepsea.service.VesselDataSeeder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeepseaApplication implements CommandLineRunner {

    @Autowired
    VesselDataSeeder vesselDataSeeder;

    public static void main(String[] args) {
        SpringApplication.run(DeepseaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        vesselDataSeeder.readVesselData("vessel_data.csv");
//        vesselDataSeeder.processVesselStatistics();
    }

}

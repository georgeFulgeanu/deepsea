package com.example.deepsea.exception;

public class VesselNotFoundException extends RuntimeException {
    public VesselNotFoundException(Long vesselCode) {
        super("Vessel not found with code: " + vesselCode);
    }
}

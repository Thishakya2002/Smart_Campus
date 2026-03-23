/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus;

/**
 *
 * @author tk
 */
import com.smartcampus.exceptions.BadRequestException;
import com.smartcampus.exceptions.ForbiddenException;
import com.smartcampus.exceptions.ResourceNotFoundException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response getAllReadings() {
        Sensor sensor = DataStore.sensors.get(sensorId);

        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor with id '" + sensorId + "' not found.");
        }

        List<SensorReading> readings = DataStore.sensorReadings.get(sensorId);
        if (readings == null) {
            readings = new ArrayList<>();
        }

        return Response.ok(readings).build();
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.sensors.get(sensorId);

        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor with id '" + sensorId + "' not found.");
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new ForbiddenException(
                    "Sensor is under maintenance and cannot accept new readings."
            );
        }

        if (reading == null) {
            throw new BadRequestException("Reading payload is required.");
        }

        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            throw new BadRequestException("Reading id is required.");
        }

        List<SensorReading> readings = DataStore.sensorReadings.get(sensorId);
        if (readings == null) {
            readings = new ArrayList<>();
            DataStore.sensorReadings.put(sensorId, readings);
        }

        readings.add(reading);

        // required side-effect
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED)
                .entity(reading)
                .build();
    }
}
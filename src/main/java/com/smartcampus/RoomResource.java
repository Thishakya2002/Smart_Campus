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
import com.smartcampus.exceptions.ConflictException;
import com.smartcampus.exceptions.ResourceNotFoundException;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<>(DataStore.rooms.values());
    }

    @POST
    public Response createRoom(Room room) {
        if (room == null) {
            throw new BadRequestException("Room payload is required.");
        }

        if (room.getId() == null || room.getId().trim().isEmpty()) {
            throw new BadRequestException("Room id is required.");
        }

        if (room.getName() == null || room.getName().trim().isEmpty()) {
            throw new BadRequestException("Room name is required.");
        }

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        DataStore.rooms.put(room.getId(), room);

        return Response.status(Response.Status.CREATED)
                .entity(room)
                .build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);

        if (room == null) {
            throw new ResourceNotFoundException("Room with id '" + roomId + "' not found.");
        }

        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);

        if (room == null) {
            throw new ResourceNotFoundException("Room with id '" + roomId + "' not found.");
        }

        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new ConflictException("Room cannot be deleted because it still has sensors assigned.");
        }

        DataStore.rooms.remove(roomId);

        return Response.ok("{\"message\":\"Room deleted successfully\"}").build();
    }

    @GET
    @Path("/{roomId}/sensors")
    public Response getSensorsByRoom(@PathParam("roomId") String roomId) {

        Room room = DataStore.rooms.get(roomId);

        if (room == null) {
            throw new ResourceNotFoundException("Room with id '" + roomId + "' not found.");
        }

        List<Sensor> sensors = new ArrayList<>();

        if (room.getSensorIds() != null) {
            for (String sensorId : room.getSensorIds()) {
                Sensor sensor = DataStore.sensors.get(sensorId);
                if (sensor != null) {
                    sensors.add(sensor);
                }
            }
        }

        return Response.ok(sensors).build();
    }

    @POST
    @Path("/{roomId}/sensors")
    public Response addSensorToRoom(@PathParam("roomId") String roomId, Sensor sensor) {

        Room room = DataStore.rooms.get(roomId);

        if (room == null) {
            throw new ResourceNotFoundException("Room with id '" + roomId + "' not found.");
        }

        if (sensor == null) {
            throw new BadRequestException("Sensor payload is required.");
        }

        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            throw new BadRequestException("Sensor id is required.");
        }

        sensor.setRoomId(roomId);

        DataStore.sensors.put(sensor.getId(), sensor);

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        if (!room.getSensorIds().contains(sensor.getId())) {
            room.getSensorIds().add(sensor.getId());
        }

        return Response.status(Response.Status.CREATED)
                .entity(sensor)
                .build();
    }

    @DELETE
    @Path("/{roomId}/sensors/{sensorId}")
    public Response removeSensorFromRoom(
            @PathParam("roomId") String roomId,
            @PathParam("sensorId") String sensorId) {

        Room room = DataStore.rooms.get(roomId);

        if (room == null) {
            throw new ResourceNotFoundException("Room with id '" + roomId + "' not found.");
        }

        if (room.getSensorIds() == null || !room.getSensorIds().contains(sensorId)) {
            throw new ResourceNotFoundException("Sensor with id '" + sensorId + "' not found in this room.");
        }

        room.getSensorIds().remove(sensorId);
        DataStore.sensors.remove(sensorId);

        return Response.ok("{\"message\":\"Sensor removed from room\"}").build();
    }
}
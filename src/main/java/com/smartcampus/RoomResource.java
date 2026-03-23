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
}
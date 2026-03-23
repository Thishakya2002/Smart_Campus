/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.mappers;

/**
 *
 * @author tk
 */
import com.smartcampus.exceptions.ConflictException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ConflictMapper implements ExceptionMapper<ConflictException> {

    @Override
    public Response toResponse(ConflictException ex) {
        return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                .build();
    }
}

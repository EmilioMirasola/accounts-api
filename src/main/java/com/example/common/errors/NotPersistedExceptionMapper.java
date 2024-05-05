package com.example.common.errors;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotPersistedExceptionMapper implements ExceptionMapper<NotPersistedException> {

    @Override
    public Response toResponse(NotPersistedException exception) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(exception.getMessage())
                .build();
    }
}

package com.provys.provysobject.generator.api;

import com.provys.provysobject.generator.EntityGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/entity")
public class EntityGeneratorEndpoint {

    @Inject
    EntityGenerator entityGenerator;

    @GET
    @Path("/interface/{entityNm : [a-zA-Z][a-zA-Z_0-9]*}")
    @Produces("text/plain")
    @Operation(
            summary = "Generate Interface",
            description = "Generate interface, representing instance of object of given type",
            responses = {
                    @ApiResponse(
                            description = "Class source",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            implementation = String.class,
                                            maxLength = 30
                                    )))})
    public Response getInterface(@PathParam("entityNm") String entityNm) {
        return Response.ok(entityGenerator.generateInterface(entityNm)).build();
    }
}

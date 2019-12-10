package com.provys.provysobject.generator.restapi;

import com.provys.provysobject.generator.EntityGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("/entity")
public class EntityGeneratorEndpoint {

    @Inject
    EntityGenerator entityGenerator;

    @GET
    @Path("/{entityNm : [a-zA-Z][a-zA-Z_0-9]*}/geninterface")
    @Produces("text/plain")
    @Operation(
            summary = "Generate GenInterface",
            description = "Generate generated ancestor of interface for accessing objects of given type, containing " +
                    "attribute getters",
            responses = {
                    @ApiResponse(
                            description = "Class source",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            implementation = String.class,
                                            maxLength = 30
                                    )))})
    public Response getGenInterface(@PathParam("entityNm") String entityNm,
                                    @QueryParam("friendEntities") @Nullable String friendEntities) {
        return Response.ok(entityGenerator.generateGenInterface(entityNm, friendEntities)).build();
    }

    @GET
    @Path("/{entityNm : [a-zA-Z][a-zA-Z_0-9]*}/interface")
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
    public Response getInterface(@PathParam("entityNm") String entityNm,
                                    @QueryParam("friendEntities") @Nullable String friendEntities) {
        return Response.ok(entityGenerator.generateInterface(entityNm, friendEntities)).build();
    }

    @GET
    @Path("/{entityNm : [a-zA-Z][a-zA-Z_0-9]*}/genproxy")
    @Produces("text/plain")
    @Operation(
            summary = "Generate GenProxy",
            description = "Generate generated proxy ancestor, implementing interface of given object in scope defined" +
                    " by generated interface",
            responses = {
                    @ApiResponse(
                            description = "Class source",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            implementation = String.class,
                                            maxLength = 30
                                    )))})
    public Response getGenProxy(@PathParam("entityNm") String entityNm,
                                @QueryParam("friendEntities") @Nullable String friendEntities) {
        return Response.ok(entityGenerator.generateGenProxy(entityNm, friendEntities)).build();
    }

    @GET
    @Path("/{entityNm : [a-zA-Z][a-zA-Z_0-9]*}/proxy")
    @Produces("text/plain")
    @Operation(
            summary = "Generate Proxy",
            description = "Generate proxy - simple envelope on generated proxy file. Used only for initialisation," +
                    " later not regenerated",
            responses = {
                    @ApiResponse(
                            description = "Class source",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            implementation = String.class,
                                            maxLength = 30
                                    )))})
    public Response getProxy(@PathParam("entityNm") String entityNm,
                             @QueryParam("friendEntities") @Nullable String friendEntities) {
        return Response.ok(entityGenerator.generateProxy(entityNm, friendEntities)).build();
    }

    @GET
    @Path("/{entityNm : [a-zA-Z][a-zA-Z_0-9]*}/value")
    @Produces("text/plain")
    @Operation(
            summary = "Generate Value",
            description = "Generate value class - object holding all object data",
            responses = {
                    @ApiResponse(
                            description = "Class source",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            implementation = String.class,
                                            maxLength = 30
                                    )))})
    public Response getValue(@PathParam("entityNm") String entityNm,
                                @QueryParam("friendEntities") @Nullable String friendEntities) {
        return Response.ok(entityGenerator.generateValue(entityNm, friendEntities)).build();
    }

    @GET
    @Path("/{entityNm : [a-zA-Z][a-zA-Z_0-9]*}/value")
    @Produces("text/plain")
    @Operation(
            summary = "Generate Loader Interface",
            description = "Generate loader interface - interface defining requirements for loader for given entity." +
                    " Only used to initialize file if one doesn't exist, is not refreshed later",
            responses = {
                    @ApiResponse(
                            description = "Class source",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            implementation = String.class,
                                            maxLength = 30
                                    )))})
    public Response getLoaderInterface(@PathParam("entityNm") String entityNm,
                                @QueryParam("friendEntities") @Nullable String friendEntities) {
        return Response.ok(entityGenerator.generateLoaderInterface(entityNm, friendEntities)).build();
    }

    @GET
    @Path("/{entityNm : [a-zA-Z][a-zA-Z_0-9]*}/value")
    @Produces("text/plain")
    @Operation(
            summary = "Generate Loader Base class",
            description = "Generate loader base class - common ancestor for loaders for given entity.",
            responses = {
                    @ApiResponse(
                            description = "Class source",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            implementation = String.class,
                                            maxLength = 30
                                    )))})
    public Response getLoaderBase(@PathParam("entityNm") String entityNm,
                                       @QueryParam("friendEntities") @Nullable String friendEntities) {
        return Response.ok(entityGenerator.generateLoaderBase(entityNm, friendEntities)).build();
    }

    @GET
    @Path("/{entityNm : [a-zA-Z][a-zA-Z_0-9]*}/dbloader")
    @Produces("text/plain")
    @Operation(
            summary = "Generate DbLoader class",
            description = "Generate database loader class - at the moment, does not generate load by methods.",
            responses = {
                    @ApiResponse(
                            description = "Class source",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            implementation = String.class,
                                            maxLength = 30
                                    )))})
    public Response getDbLoader(@PathParam("entityNm") String entityNm,
                                  @QueryParam("friendEntities") @Nullable String friendEntities) {
        return Response.ok(entityGenerator.generateDbLoader(entityNm, friendEntities)).build();
    }

    @GET
    @Path("/{entityNm : [a-zA-Z][a-zA-Z_0-9]*}/dbloadrunner")
    @Produces("text/plain")
    @Operation(
            summary = "Generate DbLoadRunner class",
            description = "Generate database load runner class - class that actually fetches data from database and " +
                    "converts them to value objects.",
            responses = {
                    @ApiResponse(
                            description = "Class source",
                            content = @Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(
                                            implementation = String.class,
                                            maxLength = 30
                                    )))})
    public Response getDbLoadRunner(@PathParam("entityNm") String entityNm,
                                @QueryParam("friendEntities") @Nullable String friendEntities) {
        return Response.ok(entityGenerator.generateDbLoadRunner(entityNm, friendEntities)).build();
    }
}

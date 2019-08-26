package com.provys.provysobject.generator.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
@OpenAPIDefinition(
        info = @Info(
                title = "Generator for ProvysObject based classes",
                version = "1.0",
                description = "Contains methods for geenrating classes, needed to implement access to objects from" +
                        " Provys database, described as entities in its metadata catalogue"
        ),
        servers = {@Server(url = "/api")}) // needed because swagger does not read path from appplication...
public class ProvysObjectGenApplication extends Application {
}

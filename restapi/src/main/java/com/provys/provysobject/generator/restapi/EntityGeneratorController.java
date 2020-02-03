package com.provys.provysobject.generator.restapi;

import com.provys.provysobject.generator.ModuleGenerator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@RestController
@RequestMapping(path = "/entity", produces = MediaType.TEXT_PLAIN_VALUE)
public class EntityGeneratorController {

    @Nonnull
    private final ModuleGenerator moduleGenerator;

    @Autowired
    public EntityGeneratorController(ModuleGenerator moduleGenerator) {
        this.moduleGenerator = Objects.requireNonNull(moduleGenerator);
    }

    @GetMapping("/{module:[a-zA-Z][a-zA-Z_0-9]*}/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/geninterface")
    @ApiOperation(value = "Generate GenInterface",
            notes = "Generate generated ancestor of interface for accessing objects of given type, containing " +
                    "attribute getters")
    public String getGenInterface(@PathVariable String module,
                                  @PathVariable("entityNm") String entityNm,
                                  @RequestParam(value = "package", required = false)
                                      @ApiParam("Package sources should be placed in; defaults to" +
                                              "com.provys.<module>")
                                      @Nullable String packageName,
                                  @RequestParam(value = "attrs", required = false)
                                      @ApiParam("Comma separated list of attributes to be generated; empty means all " +
                                              "column attributes will be generated") @Nullable String attrs,
                                  @RequestParam(value = "friendEntities", required = false)
                                      @Nullable String friendEntities) {
        return moduleGenerator.generateGenInterface(module, packageName, entityNm, attrs, friendEntities).toString();
    }

    @GetMapping("/{module:[a-zA-Z][a-zA-Z_0-9]*}/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/interface")
    @ApiOperation(value = "Generate Interface",
            notes = "Generate interface, representing instance of object of given type")
    public String getInterface(@PathVariable String module,
                               @PathVariable("entityNm") String entityNm,
                               @RequestParam(value = "package", required = false)
                                   @ApiParam("Package sources should be placed in; defaults to" +
                                           "com.provys.<module>")
                                   @Nullable String packageName,
                               @RequestParam(value = "attrs", required = false)
                                   @ApiParam("Comma separated list of attributes to be generated; empty means all " +
                                           "column attributes will be generated") @Nullable String attrs,
                               @RequestParam(value = "friendEntities", required = false)
                               @Nullable String friendEntities) {
        return moduleGenerator.generateInterface(module, packageName, entityNm, attrs, friendEntities).toString();
    }

    @GetMapping("/{module:[a-zA-Z][a-zA-Z_0-9]*}/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/meta")
    @ApiOperation(value = "Generate Meta class",
            notes = "Generate meta class, providing statical meta-information for type")
    public String getMeta(@PathVariable String module,
                          @PathVariable("entityNm") String entityNm,
                          @RequestParam(value = "package", required = false)
                              @ApiParam("Package sources should be placed in; defaults to" +
                                      "com.provys.<module>")
                              @Nullable String packageName,
                          @RequestParam(value = "attrs", required = false)
                              @ApiParam("Comma separated list of attributes to be generated; empty means all " +
                                      "column attributes will be generated") @Nullable String attrs,
                          @RequestParam(value = "friendEntities", required = false)
                          @Nullable String friendEntities) {
        return moduleGenerator.generateMeta(module, packageName, entityNm, attrs, friendEntities).toString();
    }

    @GetMapping("/{module:[a-zA-Z][a-zA-Z_0-9]*}/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/genproxy")
    @ApiOperation(value = "Generate GenProxy",
            notes = "Generate generated proxy ancestor, implementing interface of given object in scope defined" +
                    " by generated interface")
    public String getGenProxy(@PathVariable String module,
                              @PathVariable("entityNm") String entityNm,
                              @RequestParam(value = "package", required = false)
                                  @ApiParam("Package sources should be placed in; defaults to" +
                                          "com.provys.<module>")
                                  @Nullable String packageName,
                              @RequestParam(value = "attrs", required = false)
                                  @ApiParam("Comma separated list of attributes to be generated; empty means all " +
                                          "column attributes will be generated") @Nullable String attrs,
                              @RequestParam(value = "friendEntities", required = false)
                              @Nullable String friendEntities) {
        return moduleGenerator.generateGenProxy(module, packageName, entityNm, attrs, friendEntities).toString();
    }

    @GetMapping("/{module:[a-zA-Z][a-zA-Z_0-9]*}/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/proxy")
    @ApiOperation(value = "Generate Proxy",
            notes = "Generate proxy - simple envelope on generated proxy file. Used only for initialisation," +
                    " later not regenerated")
    public String getProxy(@PathVariable String module,
                           @PathVariable("entityNm") String entityNm,
                           @RequestParam(value = "package", required = false)
                               @ApiParam("Package sources should be placed in; defaults to" +
                                       "com.provys.<module>")
                               @Nullable String packageName,
                           @RequestParam(value = "attrs", required = false)
                               @ApiParam("Comma separated list of attributes to be generated; empty means all " +
                                       "column attributes will be generated") @Nullable String attrs,
                           @RequestParam(value = "friendEntities", required = false)
                           @Nullable String friendEntities) {
        return moduleGenerator.generateProxy(module, packageName, entityNm, attrs, friendEntities).toString();
    }

    @GetMapping("/{module:[a-zA-Z][a-zA-Z_0-9]*}/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/proxyserializationconverter")
    @ApiOperation(value = "Generate Proxy Serialization Converter",
            notes = "Generate Jackson converter used for proxy serialization via value class")
    public String getProxySerializationConverter(@PathVariable String module,
                                                 @PathVariable("entityNm") String entityNm,
                                                 @RequestParam(value = "package", required = false)
                                                     @ApiParam("Package sources should be placed in; defaults to" +
                                                     "com.provys.<module>")
                                                     @Nullable String packageName,
                                                 @RequestParam(value = "attrs", required = false)
                                                     @ApiParam("Comma separated list of attributes to be generated; " +
                                                             "empty means all column attributes will be generated")
                                                     @Nullable String attrs,
                                                 @RequestParam(value = "friendEntities", required = false)
                                                 @Nullable String friendEntities) {
        return moduleGenerator.generateProxySerializationConverter(module, packageName, entityNm, attrs, friendEntities)
                .toString();
    }

    @GetMapping("/{module:[a-zA-Z][a-zA-Z_0-9]*}/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/value")
    @ApiOperation(value = "Generate Value",
            notes = "Generate value class - object holding all object data")
    public String getValue(@PathVariable String module,
                           @PathVariable("entityNm") String entityNm,
                           @RequestParam(value = "package", required = false)
                               @ApiParam("Package sources should be placed in; defaults to" +
                                       "com.provys.<module>")
                               @Nullable String packageName,
                           @RequestParam(value = "attrs", required = false)
                               @ApiParam("Comma separated list of attributes to be generated; empty means all " +
                                       "column attributes will be generated") @Nullable String attrs,
                           @RequestParam(value = "friendEntities", required = false)
                           @Nullable String friendEntities) {
        return moduleGenerator.generateValue(module, packageName, entityNm, attrs, friendEntities).toString();
    }

    @GetMapping("/{module:[a-zA-Z][a-zA-Z_0-9]*}/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/valuebuilder")
    @ApiOperation(value = "Generate Value Builder",
            notes = "Generate value builder class - object allowing creation or modification of object value")
    public String getValueBuilder(@PathVariable String module,
                                  @PathVariable("entityNm") String entityNm,
                                  @RequestParam(value = "package", required = false)
                                      @ApiParam("Package sources should be placed in; defaults to" +
                                              "com.provys.<module>")
                                      @Nullable String packageName,
                                  @RequestParam(value = "attrs", required = false)
                                      @ApiParam("Comma separated list of attributes to be generated; empty means all " +
                                              "column attributes will be generated") @Nullable String attrs,
                                  @RequestParam(value = "friendEntities", required = false)
                                  @Nullable String friendEntities) {
        return moduleGenerator.generateValueBuilder(module, packageName, entityNm, attrs, friendEntities).toString();
    }

    @GetMapping("/{module:[a-zA-Z][a-zA-Z_0-9]*}/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/valuebuilderserializer")
    @ApiOperation(value = "Generate Value Builder Serializer",
            notes = "Generate Jackson serializer for value builder class - ensures that serialization contains" +
                    " fields with upd flag set")
    public String getValueBuilderSerializer(@PathVariable String module,
                                            @PathVariable("entityNm") String entityNm,
                                            @RequestParam(value = "package", required = false)
                                                @ApiParam("Package sources should be placed in; defaults to" +
                                                        "com.provys.<module>")
                                                @Nullable String packageName,
                                            @RequestParam(value = "attrs", required = false)
                                                @ApiParam("Comma separated list of attributes to be generated; empty " +
                                                        "means all column attributes will be generated")
                                                @Nullable String attrs,
                                            @RequestParam(value = "friendEntities", required = false)
                                            @Nullable String friendEntities) {
        return moduleGenerator.generateValueBuilderSerializer(module, packageName, entityNm, attrs, friendEntities).toString();
    }

    @GetMapping("/{module:[a-zA-Z][a-zA-Z_0-9]*}/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/loader")
    @ApiOperation(value = "Generate Loader Interface",
            notes = "Generate loader interface - interface defining requirements for loader for given entity." +
                    " Only used to initialize file if one doesn't exist, is not refreshed later")
    public String getLoaderInterface(@PathVariable String module,
                                     @PathVariable("entityNm") String entityNm,
                                     @RequestParam(value = "package", required = false)
                                         @ApiParam("Package sources should be placed in; defaults to" +
                                                 "com.provys.<module>")
                                         @Nullable String packageName,
                                     @RequestParam(value = "attrs", required = false)
                                         @ApiParam("Comma separated list of attributes to be generated; empty means " +
                                                 "all column attributes will be generated") @Nullable String attrs,
                                     @RequestParam(value = "friendEntities", required = false)
                                     @Nullable String friendEntities) {
        return moduleGenerator.generateLoaderInterface(module, packageName, entityNm, attrs, friendEntities).toString();
    }

    @GetMapping("/{module:[a-zA-Z][a-zA-Z_0-9]*}/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/loaderbase")
    @ApiOperation(value = "Generate Loader Base class",
            notes = "Generate loader base class - common ancestor for loaders for given entity.")
    public String getLoaderBase(@PathVariable String module,
                                @PathVariable("entityNm") String entityNm,
                                @RequestParam(value = "package", required = false)
                                    @ApiParam("Package sources should be placed in; defaults to" +
                                            "com.provys.<module>")
                                    @Nullable String packageName,
                                @RequestParam(value = "attrs", required = false)
                                    @ApiParam("Comma separated list of attributes to be generated; empty means all " +
                                            "column attributes will be generated") @Nullable String attrs,
                                @RequestParam(value = "friendEntities", required = false)
                                @Nullable String friendEntities) {
        return moduleGenerator.generateLoaderBase(module, packageName, entityNm, attrs, friendEntities).toString();
    }

    @GetMapping("/{module:[a-zA-Z][a-zA-Z_0-9]*}/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/dbloader")
    @ApiOperation(value = "Generate DbLoader class",
            notes = "Generate database loader class - at the moment, does not generate load by methods.")
    public String getDbLoader(@PathVariable String module,
                              @PathVariable("entityNm") String entityNm,
                              @RequestParam(value = "package", required = false)
                                  @ApiParam("Package sources should be placed in; defaults to" +
                                          "com.provys.<module>")
                                  @Nullable String packageName,
                              @RequestParam(value = "attrs", required = false)
                                  @ApiParam("Comma separated list of attributes to be generated; empty means all " +
                                          "column attributes will be generated") @Nullable String attrs,
                              @RequestParam(value = "friendEntities", required = false)
                              @Nullable String friendEntities) {
        return moduleGenerator.generateDbLoader(module, packageName, entityNm, attrs, friendEntities).toString();
    }

    @GetMapping("/{module:[a-zA-Z][a-zA-Z_0-9]*}/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/dbloadrunner")
    @ApiOperation(value = "Generate DbLoadRunner class",
            notes = "Generate database load runner class - class that actually fetches data from database and " +
                    "converts them to value objects.")
    public String getDbLoadRunner(@PathVariable String module,
                                  @PathVariable("entityNm") String entityNm,
                                  @RequestParam(value = "package", required = false)
                                      @ApiParam("Package sources should be placed in; defaults to" +
                                              "com.provys.<module>")
                                      @Nullable String packageName,
                                  @RequestParam(value = "attrs", required = false)
                                      @ApiParam("Comma separated list of attributes to be generated; empty means all " +
                                              "column attributes will be generated") @Nullable String attrs,
                                  @RequestParam(value = "friendEntities", required = false)
                                  @Nullable String friendEntities) {
        return moduleGenerator.generateDbLoadRunner(module, packageName, entityNm, attrs, friendEntities).toString();
    }
}

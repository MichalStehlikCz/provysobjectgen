package com.provys.provysobject.generator.restapi;

import com.provys.provysobject.generator.EntityGenerator;
import io.swagger.annotations.ApiOperation;
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
    private final EntityGenerator entityGenerator;

    @Autowired
    public EntityGeneratorController(EntityGenerator entityGenerator) {
        this.entityGenerator = Objects.requireNonNull(entityGenerator);
    }

    @GetMapping("/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/geninterface")
    @ApiOperation(value = "Generate GenInterface",
            notes = "Generate generated ancestor of interface for accessing objects of given type, containing " +
                    "attribute getters")
    public String getGenInterface(@PathVariable("entityNm") String entityNm,
                                  @RequestParam(value = "friendEntities", required = false)
                                  @Nullable String friendEntities) {
        return entityGenerator.generateGenInterface(entityNm, friendEntities);
    }

    @GetMapping("/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/interface")
    @ApiOperation(value = "Generate Interface",
            notes = "Generate interface, representing instance of object of given type")
    public String getInterface(@PathVariable("entityNm") String entityNm,
                               @RequestParam(value = "friendEntities", required = false)
                               @Nullable String friendEntities) {
        return entityGenerator.generateInterface(entityNm, friendEntities);
    }

    @GetMapping("/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/meta")
    @ApiOperation(value = "Generate Meta class",
            notes = "Generate meta class, providing statical meta-information for type")
    public String getMeta(@PathVariable("entityNm") String entityNm,
                          @RequestParam(value = "friendEntities", required = false)
                          @Nullable String friendEntities) {
        return entityGenerator.generateMeta(entityNm, friendEntities);
    }

    @GetMapping("/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/genproxy")
    @ApiOperation(value = "Generate GenProxy",
            notes = "Generate generated proxy ancestor, implementing interface of given object in scope defined" +
                    " by generated interface")
    public String getGenProxy(@PathVariable("entityNm") String entityNm,
                              @RequestParam(value = "friendEntities", required = false)
                              @Nullable String friendEntities) {
        return entityGenerator.generateGenProxy(entityNm, friendEntities);
    }

    @GetMapping("/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/proxy")
    @ApiOperation(value = "Generate Proxy",
            notes = "Generate proxy - simple envelope on generated proxy file. Used only for initialisation," +
                    " later not regenerated")
    public String getProxy(@PathVariable("entityNm") String entityNm,
                           @RequestParam(value = "friendEntities", required = false)
                           @Nullable String friendEntities) {
        return entityGenerator.generateProxy(entityNm, friendEntities);
    }

    @GetMapping("/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/proxyserializationconverter")
    @ApiOperation(value = "Generate Proxy Serialization Converter",
            notes = "Generate Jackson converter used for proxy serialization via value class")
    public String getProxySerializationConverter(@PathVariable("entityNm") String entityNm,
                                                 @RequestParam(value = "friendEntities", required = false)
                                                 @Nullable String friendEntities) {
        return entityGenerator.generateProxySerializationConverter(entityNm, friendEntities);
    }

    @GetMapping("/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/value")
    @ApiOperation(value = "Generate Value",
            notes = "Generate value class - object holding all object data")
    public String getValue(@PathVariable("entityNm") String entityNm,
                           @RequestParam(value = "friendEntities", required = false)
                           @Nullable String friendEntities) {
        return entityGenerator.generateValue(entityNm, friendEntities);
    }

    @GetMapping("/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/valuebuilder")
    @ApiOperation(value = "Generate Value Builder",
            notes = "Generate value builder class - object allowing creation or modification of object value")
    public String getValueBuilder(@PathVariable("entityNm") String entityNm,
                                  @RequestParam(value = "friendEntities", required = false)
                                  @Nullable String friendEntities) {
        return entityGenerator.generateValueBuilder(entityNm, friendEntities);
    }

    @GetMapping("/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/valuebuilderserializer")
    @ApiOperation(value = "Generate Value Builder Serializer",
            notes = "Generate Jackson serializer for value builder class - ensures that serialization contains" +
                    " fields with upd flag set")
    public String getValueBuilderSerializer(@PathVariable("entityNm") String entityNm,
                                            @RequestParam(value = "friendEntities", required = false)
                                            @Nullable String friendEntities) {
        return entityGenerator.generateValueBuilderSerializer(entityNm, friendEntities);
    }

    @GetMapping("/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/loader")
    @ApiOperation(value = "Generate Loader Interface",
            notes = "Generate loader interface - interface defining requirements for loader for given entity." +
                    " Only used to initialize file if one doesn't exist, is not refreshed later")
    public String getLoaderInterface(@PathVariable("entityNm") String entityNm,
                                     @RequestParam(value = "friendEntities", required = false)
                                     @Nullable String friendEntities) {
        return entityGenerator.generateLoaderInterface(entityNm, friendEntities);
    }

    @GetMapping("/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/loaderbase")
    @ApiOperation(value = "Generate Loader Base class",
            notes = "Generate loader base class - common ancestor for loaders for given entity.")
    public String getLoaderBase(@PathVariable("entityNm") String entityNm,
                                @RequestParam(value = "friendEntities", required = false)
                                @Nullable String friendEntities) {
        return entityGenerator.generateLoaderBase(entityNm, friendEntities);
    }

    @GetMapping("/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/dbloader")
    @ApiOperation(value = "Generate DbLoader class",
            notes = "Generate database loader class - at the moment, does not generate load by methods.")
    public String getDbLoader(@PathVariable("entityNm") String entityNm,
                              @RequestParam(value = "friendEntities", required = false)
                              @Nullable String friendEntities) {
        return entityGenerator.generateDbLoader(entityNm, friendEntities);
    }

    @GetMapping("/{entityNm:[a-zA-Z][a-zA-Z_0-9]*}/dbloadrunner")
    @ApiOperation(value = "Generate DbLoadRunner class",
            notes = "Generate database load runner class - class that actually fetches data from database and " +
                    "converts them to value objects.")
    public String getDbLoadRunner(@PathVariable("entityNm") String entityNm,
                                  @RequestParam(value = "friendEntities", required = false)
                                  @Nullable String friendEntities) {
        return entityGenerator.generateDbLoadRunner(entityNm, friendEntities);
    }
}

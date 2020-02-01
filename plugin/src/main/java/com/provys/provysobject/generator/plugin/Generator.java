package com.provys.provysobject.generator.plugin;

import com.provys.common.exception.RegularException;
import com.provys.provysobject.generator.EntityGenerator;
import com.provys.provysobject.generator.ModuleGenerator;
import com.squareup.javapoet.JavaFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = "com.provys")
@ConfigurationPropertiesScan(basePackages = "com.provys")
public class Generator implements CommandLineRunner {

    @Nonnull
    private final GenerateMojo mojo;
    @Nonnull
    private final ModuleGenerator moduleGenerator;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") // mojo is added to context in Java code
    @Autowired
    public Generator(GenerateMojo mojo, ModuleGenerator moduleGenerator) {
        this.mojo = Objects.requireNonNull(mojo);
        this.moduleGenerator = Objects.requireNonNull(moduleGenerator);
    }

    private static void writeIfMissing(JavaFile sourceFile, Path path) throws IOException {
        if (!Files.exists(sourceFile.getPath(path))) {
            sourceFile.writeToPath(path);
        }
    }

    private Path prepareDirectory(Path module) {
        var path = module.resolve("src").resolve("main").resolve("java");
        if (Files.notExists(path)) {
            try {
                Files.createDirectories(module);
            } catch (IOException e) {
                throw new RegularException("PROVYSOBJECTGEN_FAILED_T_CREATE_MODULE_DIR",
                        "Failed to create module directory " + path.toString(), e);
            }
        } else {
            if (!Files.isDirectory(module)) {
                throw new RegularException("PROVYSOBJECTGEN_TARGET_NOT_DIRECTORY", "Target module is not directory");
            }
        }
        return path;
    }

    private void writeApiModule(EntityGenerator entityGenerator, Path apiModule) {
        var path = prepareDirectory(apiModule);
        try {
            entityGenerator.generateGenInterface().writeTo(path);
            writeIfMissing(entityGenerator.generateInterface(), path);
        } catch (IOException e) {
            throw new RegularException("PROVYSOBJECTGEN_CANNOT_WRITE_API", "Cannot write api source files", e);
        }
    }

    private void writeImplModule(EntityGenerator entityGenerator, Path implModule) {
        var path = prepareDirectory(implModule);
        try {
            entityGenerator.generateGenProxy().writeToPath(path);
            entityGenerator.generateValue().writeTo(path);

        } catch (IOException e) {
            throw new RegularException("PROVYSOBJECTGEN_CANNOT_WRITE_IMPL", "Cannot write impl source files", e);
        }
    }

    private void writeDbLoaderModule(EntityGenerator entityGenerator, Path dbLoaderModule) {
        var path = prepareDirectory(dbLoaderModule);
        try {
            var dbLoader = entityGenerator.generateDbLoader();
            if (!Files.exists(dbLoader.getPath(path))) {
                dbLoader.writeToPath(path);
            }
            entityGenerator.generateDbLoadRunner().writeToPath(path);
        } catch (IOException e) {
            throw new RegularException("PROVYSOBJECTGEN_CANNOT_WRITE_DBLOADER", "Cannot write dbloader source files", e);
        }
    }

    @Override
    public void run(String... args) {
        var friendEntities = mojo.getEntities().stream().map(entity -> entity.nameNm).collect(Collectors.toList());
        // run
        for (var entity : mojo.getEntities()) {
            mojo.getLog().info("Generate sources for " + entity.nameNm);
            var entityGenerator = moduleGenerator.getEntityGenerator(mojo.getModule(), entity.nameNm, entity.attrs,
                    friendEntities);
            mojo.getApiModule().ifPresent(apiModule -> writeApiModule(entityGenerator, apiModule));
            mojo.getImplModule().ifPresent(implModule -> writeImplModule(entityGenerator, implModule));
            mojo.getDbLoaderModule().ifPresent(dbLoaderModule -> writeDbLoaderModule(entityGenerator, dbLoaderModule));
        }
        mojo.getLog().info("Finished generating " + mojo.getModule());
    }
}

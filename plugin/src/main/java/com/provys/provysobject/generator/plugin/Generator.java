package com.provys.provysobject.generator.plugin;

import com.provys.common.exception.RegularException;
import com.provys.provysobject.generator.EntityGenerator;
import com.provys.provysobject.generator.ModuleGenerator;
import com.squareup.javapoet.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
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

    private void prepareDirectory(Path module) {
        if (Files.notExists(module)) {
            try {
                Files.createDirectories(module);
            } catch (IOException e) {
                throw new RegularException("PROVYSOBJECTGEN_FAILED_T_CREATE_MODULE_DIR",
                        "Failed to create module directory", e);
            }
        } else {
            if (!Files.isDirectory(module)) {
                throw new RegularException("PROVYSOBJECTGEN_TARGET_NOT_DIRECTORY", "Target module is not directory");
            }
        }
//        Path outputDirectory = directory;
//        if (!this.packageName.isEmpty()) {
//            String[] var4 = this.packageName.split("\\.");
//            int var5 = var4.length;
//
//            for(int var6 = 0; var6 < var5; ++var6) {
//                String packageComponent = var4[var6];
//                outputDirectory = outputDirectory.resolve(packageComponent);
//            }
//
//            Files.createDirectories(outputDirectory);
        }

        Path outputPath = outputDirectory.resolve(this.typeSpec.name + ".java");
        OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(outputPath), charset);
    }

    private void writeApiModule(EntityGenerator entityGenerator, File apiModule) {
        prepareDirectory(apiModule.toPath());
        try {
            entityGenerator.generateGenInterface().writeTo(
                    Path.of(apiModule, "src", "main", "java"));
        } catch (IOException e) {
            throw new RegularException("PROVYSOBJECTGEN_CANNOT_WRITE_API", "Cannot write api source files", e);
        }
    }

    private void writeImplModule(EntityGenerator entityGenerator, String implModule) {
        try {
            entityGenerator.generateValue().writeTo(
                    Path.of(implModule, "src", "main", "java"));
        } catch (IOException e) {
            throw new RegularException("PROVYSOBJECTGEN_CANNOT_WRITE_IMPL", "Cannot write impl source files", e);
        }
    }

    private void writeDbLoaderModule(EntityGenerator entityGenerator, String dbLoaderModule) {
        try {
//            entityGenerator.generateDbLoader().writeTo(
//                    Path.of(dbLoaderModule, "src", "main", "java"));
            entityGenerator.generateDbLoadRunner().writeTo(
                    Path.of(dbLoaderModule, "src", "main", "java"));
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
            mojo.getImplModule().ifPresent(dbLoaderModule -> writeDbLoaderModule(entityGenerator, dbLoaderModule));
        }
        mojo.getLog().info("Finished generating " + mojo.getModule());
    }
}

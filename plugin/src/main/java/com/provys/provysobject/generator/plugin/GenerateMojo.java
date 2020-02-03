package com.provys.provysobject.generator.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Mojo(name = "generate", inheritByDefault = false, aggregator = true)
public class GenerateMojo extends AbstractMojo {

    @Parameter
    @Nullable
    private Provysdb provysdb;

    @SuppressWarnings("NotNullFieldNotInitialized") // initialized via mojo parameter injection
    @Parameter(required = true)
    @Nonnull
    private String basePackage;

    @SuppressWarnings("NotNullFieldNotInitialized") // initialized via mojo parameter injection
    @Parameter(required = true)
    @Nonnull
    private String module;

    @Parameter(name = "package")
    @Nullable
    private String packageName;

    @SuppressWarnings("NotNullFieldNotInitialized") // initialized via mojo parameter injection
    @Parameter(required = true)
    @Nonnull
    private List<Entity> entities;

    @Parameter
    @Nullable
    private File apiModule;

    @Parameter
    @Nullable
    private File implModule;

    @Parameter
    @Nullable
    private File dbLoaderModule;

    /**
     * @return value of field module
     */
    @Nonnull
    public String getModule() {
        return module;
    }

    /**
     * @return value of field packageName
     */
    @Nullable
    public String getPackageName() {
        return packageName;
    }

    /**
     * @return value of field entities
     */
    @Nonnull
    public List<Entity> getEntities() {
        return entities;
    }

    /**
     * @return value of field basePackage
     */
    @Nonnull
    public String getBasePackage() {
        return basePackage;
    }

    /**
     * @return value of field apiModule
     */
    @Nonnull
    public Optional<Path> getApiModule() {
        return Optional.ofNullable(apiModule).map(File::toPath);
    }

    /**
     * @return value of field implModule
     */
    @Nonnull
    public Optional<Path> getImplModule() {
        return Optional.ofNullable(implModule).map(File::toPath);
    }

    /**
     * @return value of field dbLoaderModule
     */
    @Nonnull
    public Optional<Path> getDbLoaderModule() {
        return Optional.ofNullable(dbLoaderModule).map(File::toPath);
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        var properties = new HashMap<String, Object>();
        if (provysdb != null) {
            if (provysdb.url != null) {
                properties.put("provysdb.url", provysdb.url);
            }
            if (provysdb.user != null) {
                properties.put("provysdb.user", provysdb.user);
            }
            if (provysdb.pwd != null) {
                properties.put("provysdb.pwd", provysdb.pwd);
            }
        }
        if (entities.isEmpty()) {
            throw new MojoFailureException("Cannot generate - no entity specified");
        }
        // register this mojo for injection
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getBeanFactory().registerSingleton("classGenerateMojoImpl", this);
        context.refresh();
        // and build spring container and run it
        new SpringApplicationBuilder(Generator.class)
                .properties(properties)
                .registerShutdownHook(true)
                .parent(context)
                .run();
    }
}

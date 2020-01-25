package com.provys.provysobject.generator.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.HashMap;
import java.util.List;

@Mojo(name = "generate", inheritByDefault = false, aggregator = true)
public class GenerateMojo extends AbstractMojo {

    @Parameter
    private Provysdb provysdb;

    @Parameter(required = true)
    private String basePackage;

    @Parameter(required = true)
    private String module;

    @Parameter(required = true)
    private List<Entity> entities;

    @Parameter
    private String apiModule;

    @Parameter
    private String implModule;

    @Parameter
    private String dbLoaderModule;

    /**
     * @return value of field provysdb
     */
    public Provysdb getProvysdb() {
        return provysdb;
    }

    /**
     * @return value of field module
     */
    public String getModule() {
        return module;
    }

    /**
     * @return value of field entities
     */
    public List<Entity> getEntities() {
        return entities;
    }

    /**
     * @return value of field basePackage
     */
    public String getBasePackage() {
        return basePackage;
    }

    /**
     * @return value of field apiModule
     */
    public String getApiModule() {
        return apiModule;
    }

    /**
     * @return value of field implModule
     */
    public String getImplModule() {
        return implModule;
    }

    /**
     * @return value of field dbLoaderModule
     */
    public String getDbLoaderModule() {
        return dbLoaderModule;
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
        // register this mojo for insertion
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getBeanFactory().registerSingleton("classGenerateMojoImpl", this);
        context.refresh();
        new SpringApplicationBuilder(Generator.class)
                .properties(properties)
                .registerShutdownHook(true)
                .parent(context)
                .run();
    }
}

package com.provys.provysobject.generator.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

@Mojo(name = "generate")
public class GenerateMojo extends AbstractMojo {
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        SeContainer container = SeContainerInitializer.newInstance()
                .addProperty("org.jboss.weld.se.archive.isolation", false).initialize();
        Generator generator = container.select(Generator.class).get();
        generator.setMojo(this)
                .run();
    }
}

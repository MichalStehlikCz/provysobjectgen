package com.provys.provysobject.generator.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.springframework.boot.SpringApplication;

@Mojo(name = "generate")
public class GenerateMojo extends AbstractMojo {

    public static GenerateMojo INSTANCE;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String[] args = new String[0];
        SpringApplication.run(Generator.class, args);
    }
}

package com.provys.provysobject.generator.plugin;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.Objects;

@SpringBootApplication(scanBasePackages = "com.provys")
@ConfigurationPropertiesScan(basePackages = "com.provys")
public class Generator implements CommandLineRunner {

    private GenerateMojo mojo;

    /**
     * @return value of field mojo
     */
    public GenerateMojo getMojo() {
        return mojo;
    }

    /**
     * Set value of field mojo
     *
     * @param mojo is new value to be set
     * @return self to enable chaining
     */
    public Generator setMojo(GenerateMojo mojo) {
        this.mojo = mojo;
        return this;
    }

    @Override
    public void run(String... args) throws Exception {
        mojo = Objects.requireNonNull(GenerateMojo.INSTANCE);
        // run
        mojo.getLog().info("Hello world");
    }
}

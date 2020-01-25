package com.provys.provysobject.generator.plugin;

import com.provys.provysobject.generator.EntityGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = "com.provys")
@ConfigurationPropertiesScan(basePackages = "com.provys")
public class Generator implements CommandLineRunner {

    @Nonnull
    private final GenerateMojo mojo;
    @Nonnull
    private final EntityGenerator entityGenerator;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") // bean is manually added to context
    @Autowired
    public Generator(GenerateMojo mojo, EntityGenerator entityGenerator) {
        this.mojo = Objects.requireNonNull(mojo);
        this.entityGenerator = Objects.requireNonNull(entityGenerator);
    }

    @Override
    public void run(String... args) throws Exception {
        var friendEntities = mojo.getEntities().stream().map(entity -> entity.nameNm).collect(Collectors.toList());
        // run
        if (mojo.getApiModule() != null) {
            for (var entity : mojo.getEntities()) {
                entityGenerator.getGenInterface(entity.nameNm, friendEntities).writeTo(
                        Path.of(mojo.getApiModule(), "src", "main", "java"));
            }
        }
        mojo.getLog().info("Hello world");
    }
}

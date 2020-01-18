package com.provys.provysobject.generator.spring;

import com.provys.catalogue.api.CatalogueRepository;
import com.provys.provysobject.generator.impl.DefaultEntityGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Factory class for injecting EntityGenerator via Spring DI
 */
@Configuration
public class EntityGeneratorFactory {

    @Bean
    public DefaultEntityGenerator getEntityGenerator(CatalogueRepository catalogueRepository) {
        return new DefaultEntityGenerator(catalogueRepository);
    }
}

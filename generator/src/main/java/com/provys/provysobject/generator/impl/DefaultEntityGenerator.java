package com.provys.provysobject.generator.impl;

import com.provys.catalogue.api.*;
import com.provys.provysobject.generator.EntityGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class DefaultEntityGenerator implements EntityGenerator {

    @Nonnull
    private final CatalogueRepository catalogueRepository;

    @SuppressWarnings("CdiInjectionPointsInspection")
    @Inject
    DefaultEntityGenerator(CatalogueRepository catalogueRepository) {
        this.catalogueRepository = Objects.requireNonNull(catalogueRepository);
    }

    private List<String> parseFriendEntities(@Nullable String friendEntities) {
        if (friendEntities == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(friendEntities.split(",")).map(String::strip).collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public String generateGenInterface(String entityNm, @Nullable String friendEntities) {
        var entity = new GeneratorEntity(catalogueRepository,
                catalogueRepository.getEntityManager().getByNameNm(entityNm), "Catalogue",
                parseFriendEntities(friendEntities));
        return entity.generateGenInterface().toString();
    }

    @Nonnull
    @Override
    public String generateInterface(String entityNm, @Nullable String friendEntities) {
        var entity = new GeneratorEntity(catalogueRepository,
                catalogueRepository.getEntityManager().getByNameNm(entityNm), "Catalogue",
                parseFriendEntities(friendEntities));
        return entity.generateInterface().toString();
    }

    @Nonnull
    @Override
    public String generateMeta(String entityNm, @Nullable String friendEntities) {
        var entity = new GeneratorEntity(catalogueRepository,
                catalogueRepository.getEntityManager().getByNameNm(entityNm), "Catalogue",
                parseFriendEntities(friendEntities));
        return entity.generateMeta().toString();
    }

    @Nonnull
    @Override
    public String generateGenProxy(String entityNm, @Nullable String friendEntities) {
        var entity = new GeneratorEntity(catalogueRepository,
                catalogueRepository.getEntityManager().getByNameNm(entityNm), "Catalogue",
                parseFriendEntities(friendEntities));
        return entity.generateGenProxy().toString();
    }

    @Nonnull
    @Override
    public String generateProxy(String entityNm, @Nullable String friendEntities) {
        var entity = new GeneratorEntity(catalogueRepository,
                catalogueRepository.getEntityManager().getByNameNm(entityNm), "Catalogue",
                parseFriendEntities(friendEntities));
        return entity.generateProxy().toString();
    }

    @Nonnull
    @Override
    public String generateProxySerializationConverter(String entityNm, @Nullable String friendEntities) {
        var entity = new GeneratorEntity(catalogueRepository,
                catalogueRepository.getEntityManager().getByNameNm(entityNm), "Catalogue",
                parseFriendEntities(friendEntities));
        return entity.generateProxySerializationConverter().toString();
    }

    @Nonnull
    @Override
    public String generateValue(String entityNm, @Nullable String friendEntities) {
        var entity = new GeneratorEntity(catalogueRepository,
                catalogueRepository.getEntityManager().getByNameNm(entityNm), "Catalogue",
                parseFriendEntities(friendEntities));
        return entity.generateValue().toString();
    }

    @Nonnull
    @Override
    public String generateValueBuilder(String entityNm, @Nullable String friendEntities) {
        var entity = new GeneratorEntity(catalogueRepository,
                catalogueRepository.getEntityManager().getByNameNm(entityNm), "Catalogue",
                parseFriendEntities(friendEntities));
        return entity.generateValueBuilder().toString();
    }

    @Nonnull
    @Override
    public String generateValueBuilderSerializer(String entityNm, @Nullable String friendEntities) {
        var entity = new GeneratorEntity(catalogueRepository,
                catalogueRepository.getEntityManager().getByNameNm(entityNm), "Catalogue",
                parseFriendEntities(friendEntities));
        return entity.generateValueBuilderSerializer().toString();
    }

    @Nonnull
    @Override
    public String generateLoaderInterface(String entityNm, @Nullable String friendEntities) {
        var entity = new GeneratorEntity(catalogueRepository,
                catalogueRepository.getEntityManager().getByNameNm(entityNm), "Catalogue",
                parseFriendEntities(friendEntities));
        return entity.generateLoaderInterface().toString();
    }

    @Nonnull
    @Override
    public String generateLoaderBase(String entityNm, @Nullable String friendEntities) {
        var entity = new GeneratorEntity(catalogueRepository,
                catalogueRepository.getEntityManager().getByNameNm(entityNm), "Catalogue",
                parseFriendEntities(friendEntities));
        return entity.generateLoaderBase().toString();
    }

    @Nonnull
    @Override
    public String generateDbLoader(String entityNm, @Nullable String friendEntities) {
        var entity = new GeneratorEntity(catalogueRepository,
                catalogueRepository.getEntityManager().getByNameNm(entityNm), "Catalogue",
                parseFriendEntities(friendEntities));
        return entity.generateDbLoader().toString();
    }

    @Nonnull
    @Override
    public String generateDbLoadRunner(String entityNm, @Nullable String friendEntities) {
        var entity = new GeneratorEntity(catalogueRepository,
                catalogueRepository.getEntityManager().getByNameNm(entityNm), "Catalogue",
                parseFriendEntities(friendEntities));
        return entity.generateDbLoadRunner().toString();
    }
}

package com.provys.provysobject.generator.impl;

import com.provys.catalogue.api.*;
import com.provys.provysobject.generator.EntityGenerator;
import com.provys.provysobject.generator.ModuleGenerator;
import com.squareup.javapoet.JavaFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DefaultModuleGenerator implements ModuleGenerator {

    @Nonnull
    private final CatalogueRepository catalogueRepository;

    public DefaultModuleGenerator(CatalogueRepository catalogueRepository) {
        this.catalogueRepository = Objects.requireNonNull(catalogueRepository);
    }

    @Nonnull
    @Override
    public EntityGenerator getEntityGenerator(String module, String packageName, String entityNm,
                                              @Nullable List<String> attrs, List<String> friendEntities) {
        return new DefaultEntityGenerator(catalogueRepository,
                catalogueRepository.getEntityManager().getByNameNm(entityNm), module, packageName, friendEntities);
    }

    @Nullable
    private List<String> parseAttrs(@Nullable String attrs) {
        if (attrs == null) {
            return null;
        }
        return Arrays.stream(attrs.split(",")).map(String::strip).collect(Collectors.toList());
    }

    private List<String> parseFriendEntities(@Nullable String friendEntities) {
        if (friendEntities == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(friendEntities.split(",")).map(String::strip).collect(Collectors.toList());
    }

    @Nonnull
    private EntityGenerator getEntityGenerator(String module, String packageName, String entityNm,
                                               @Nullable String attrs, @Nullable String friendEntities) {
        return getEntityGenerator(module, packageName, entityNm, parseAttrs(attrs),
                parseFriendEntities(friendEntities));
    }

    @Nonnull
    @Override
    public JavaFile generateGenInterface(String module, String packageName, String entityNm, @Nullable String attrs,
                                         @Nullable String friendEntities) {
        return getEntityGenerator(module, packageName, entityNm, attrs, friendEntities).generateGenInterface();
    }

    @Nonnull
    @Override
    public JavaFile generateInterface(String module, String packageName, String entityNm, @Nullable String attrs,
                                      @Nullable String friendEntities) {
        return getEntityGenerator(module, packageName, entityNm, attrs, friendEntities).generateInterface();
    }

    @Nonnull
    @Override
    public JavaFile generateMeta(String module, String packageName, String entityNm, @Nullable String attrs,
                                 @Nullable String friendEntities) {
        return getEntityGenerator(module, packageName, entityNm, attrs, friendEntities).generateMeta();
    }

    @Nonnull
    @Override
    public JavaFile generateGenProxy(String module, String packageName, String entityNm, @Nullable String attrs,
                                     @Nullable String friendEntities) {
        return getEntityGenerator(module, packageName, entityNm, attrs, friendEntities).generateGenProxy();
    }

    @Nonnull
    @Override
    public JavaFile generateProxy(String module, String packageName, String entityNm, @Nullable String attrs,
                                  @Nullable String friendEntities) {
        return getEntityGenerator(module, packageName, entityNm, attrs, friendEntities).generateProxy();
    }

    @Nonnull
    @Override
    public JavaFile generateProxySerializationConverter(String module, String packageName, String entityNm,
                                                        @Nullable String attrs, @Nullable String friendEntities) {
        return getEntityGenerator(module, packageName, entityNm, attrs, friendEntities).generateProxySerializationConverter();
    }

    @Nonnull
    @Override
    public JavaFile generateValue(String module, String packageName, String entityNm, @Nullable String attrs,
                                  @Nullable String friendEntities) {
        return getEntityGenerator(module, packageName, entityNm, attrs, friendEntities).generateValue();
    }

    @Nonnull
    @Override
    public JavaFile generateValueBuilder(String module, String packageName, String entityNm, @Nullable String attrs,
                                         @Nullable String friendEntities) {
        return getEntityGenerator(module, packageName, entityNm, attrs, friendEntities).generateValueBuilder();
    }

    @Nonnull
    @Override
    public JavaFile generateValueBuilderSerializer(String module, String packageName, String entityNm,
                                                   @Nullable String attrs, @Nullable String friendEntities) {
        return getEntityGenerator(module, packageName, entityNm, attrs, friendEntities).generateValueBuilderSerializer();
    }

    @Nonnull
    @Override
    public JavaFile generateLoaderInterface(String module, String packageName, String entityNm, @Nullable String attrs,
                                            @Nullable String friendEntities) {
        return getEntityGenerator(module, packageName, entityNm, attrs, friendEntities).generateLoaderInterface();
    }

    @Nonnull
    @Override
    public JavaFile generateLoaderBase(String module, String packageName, String entityNm, @Nullable String attrs,
                                       @Nullable String friendEntities) {
        return getEntityGenerator(module, packageName, entityNm, attrs, friendEntities).generateLoaderBase();
    }

    @Nonnull
    @Override
    public JavaFile generateDbLoader(String module, @Nullable String packageName, String entityNm,
                                     @Nullable String attrs, @Nullable String friendEntities) {
        return getEntityGenerator(module, packageName, entityNm, attrs, friendEntities).generateDbLoader();
    }

    @Nonnull
    @Override
    public JavaFile generateDbLoadRunner(String module, @Nullable String packageName, String entityNm,
                                         @Nullable String attrs, @Nullable String friendEntities) {
        return getEntityGenerator(module, packageName, entityNm, attrs, friendEntities).generateDbLoadRunner();
    }
}

package com.provys.provysobject.generator;

import com.squareup.javapoet.JavaFile;

import javax.annotation.Nonnull;

public interface EntityGenerator {
    @Nonnull
    JavaFile generateGenInterface();

    @Nonnull
    JavaFile generateInterface();

    @Nonnull
    JavaFile generateMeta();

    @Nonnull
    JavaFile generateGenProxy();

    @Nonnull
    JavaFile generateProxy();

    @Nonnull
    JavaFile generateProxySerializationConverter();

    @Nonnull
    JavaFile generateValue();

    @Nonnull
    JavaFile generateValueBuilder();

    @Nonnull
    JavaFile generateValueBuilderSerializer();

    @Nonnull
    JavaFile generateLoaderInterface();

    @Nonnull
    JavaFile generateLoaderBase();

    @Nonnull
    JavaFile generateDbLoader();

    @Nonnull
    JavaFile generateDbLoadRunner();
}

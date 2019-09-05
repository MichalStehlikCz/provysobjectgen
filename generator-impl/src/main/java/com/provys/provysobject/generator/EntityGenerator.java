package com.provys.provysobject.generator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EntityGenerator {

    /**
     * Generate generated ancestor of interface for accessing instances of given object
     *
     * @param entityNm is internal name of entity class should be based on
     * @return source code for given class
     */
    @Nonnull
    String generateGenInterface(String entityNm, @Nullable String friendEntities);

    /**
     * Generate interface for accessing instances of given object
     *
     * @param entityNm is internal name of entity class should be based on
     * @return source code for given class
     */
    @Nonnull
    String generateInterface(String entityNm, @Nullable String friendEntities);

    /**
     * Generate generated proxy ancestor, implementing interface of given object in scope defined by generated interface
     *
     * @param entityNm is internal name of entity class should be based on
     * @return source code for given class
     */
    @Nonnull
    String generateGenProxy(String entityNm, @Nullable String friendEntities);

    /**
     * Generate proxy - simple envelope on generated proxy file. Used only for initialisation, later not regenerated
     *
     * @param entityNm is internal name of entity class should be based on
     * @return source code for given class
     */
    @Nonnull
    String generateProxy(String entityNm, @Nullable String friendEntities);

    /**
     * Generate value class - whole value class is generated, potential logic is implemented in proxy
     *
     * @param entityNm is internal name of entity class should be based on
     * @return source code for given class
     */
    @Nonnull
    String generateValue(String entityNm, @Nullable String friendEntities);

    /**
     * Generate loader interface - generated template for loader interface, that is later manually modified
     *
     * @param entityNm is internal name of entity class should be based on
     * @return source code for given class
     */
    @Nonnull
    String generateLoaderInterface(String entityNm, @Nullable String friendEntities);

    /**
     * Generate loader base - generated template for loader base class, that is later manually modified
     *
     * @param entityNm is internal name of entity class should be based on
     * @return source code for given class
     */
    @Nonnull
    String generateLoaderBase(String entityNm, @Nullable String friendEntities);
}

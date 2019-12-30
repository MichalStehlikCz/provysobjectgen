package com.provys.provysobject.generator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EntityGenerator {

    /**
     * Generate generated ancestor of interface for accessing instances of given object
     *
     * @param entityNm is internal name of entity class should be based on
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    String generateGenInterface(String entityNm, @Nullable String friendEntities);

    /**
     * Generate interface for accessing instances of given object
     *
     * @param entityNm is internal name of entity class should be based on
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    String generateInterface(String entityNm, @Nullable String friendEntities);

    /**
     * Generate class with meta-information
     *
     * @param entityNm is internal name of entity class should be based on
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    String generateMeta(String entityNm, @Nullable String friendEntities);

    /**
     * Generate generated proxy ancestor, implementing interface of given object in scope defined by generated interface
     *
     * @param entityNm is internal name of entity class should be based on
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    String generateGenProxy(String entityNm, @Nullable String friendEntities);

    /**
     * Generate proxy - simple envelope on generated proxy file. Used only for initialisation, later not regenerated
     *
     * @param entityNm is internal name of entity class should be based on
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    String generateProxy(String entityNm, @Nullable String friendEntities);

    /**
     * Generate Jackson converter for proxy serialization via value class
     *
     * @param entityNm is internal name of entity class should be based on
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    String generateProxySerializationConverter(String entityNm, @Nullable String friendEntities);

    /**
     * Generate value class - whole value class is generated, potential logic is implemented in proxy
     *
     * @param entityNm is internal name of entity class should be based on
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    String generateValue(String entityNm, @Nullable String friendEntities);

    /**
     * Generate value builder class - whole value class is generated
     *
     * @param entityNm is internal name of entity class should be based on
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    String generateValueBuilder(String entityNm, @Nullable String friendEntities);

    /**
     * Generate jackson serializer for value builder class
     *
     * @param entityNm is internal name of entity class should be based on
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    String generateValueBuilderSerializer(String entityNm, @Nullable String friendEntities);

    /**
     * Generate loader interface - generated template for loader interface, that is later manually modified
     *
     * @param entityNm is internal name of entity class should be based on
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    String generateLoaderInterface(String entityNm, @Nullable String friendEntities);

    /**
     * Generate loader base - generated template for loader base class, that is later manually modified
     *
     * @param entityNm is internal name of entity class should be based on
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    String generateLoaderBase(String entityNm, @Nullable String friendEntities);

    /**
     * Generate database loader - at the moment, does not generate load by methods
     *
     * @param entityNm is internal name of entity class should be based on
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    String generateDbLoader(String entityNm, @Nullable String friendEntities);

    /**
     * Generate database load runner - class that actually fetches data from database and converts them to value objects
     *
     * @param entityNm is internal name of entity class should be based on
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    String generateDbLoadRunner(String entityNm, @Nullable String friendEntities);
}

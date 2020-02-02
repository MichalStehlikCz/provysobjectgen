package com.provys.provysobject.generator;

import com.squareup.javapoet.JavaFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface ModuleGenerator {

    /**
     * Retrieve entity generator for given module / entity / ...
     *
     * @param module is module we generate; repository is prefixed with this module
     * @param packageName is full name of root of package sources will be placed in; subpackages are created based on
     *                   convention (impl, dbloader), api is placed in root package
     * @param entityNm is internal name of entity class should be based on
     * @param attrs is list of attributes that should be used; if empty, all C attributes will be generated
     * @param friendEntities is list of internal names of entities under the same repository; generator generates
     *                      getters returning object instead of just Id for foreign keys of these types
     * @return generator usable for all sources for given parametrisation
     */
    @Nonnull
    EntityGenerator getEntityGenerator(String module, String packageName, String entityNm, @Nullable List<String> attrs,
                                       List<String> friendEntities);

    /**
     * Generate generated ancestor of interface for accessing instances of given object
     *
     * @param module is module we generate; repository is prefixed with this module
     * @param packageName is full name of root of package sources will be placed in; subpackages are created based on
     *                   convention (impl, dbloader), api is placed in root package
     * @param entityNm is internal name of entity class should be based on
     * @param attrs is comma separated list of attributes that should be used; if empty, all C attributes will be
     *             generated
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source file for given class
     */
    @Nonnull
    JavaFile generateGenInterface(String module, String packageName, String entityNm, @Nullable String attrs,
                                  @Nullable String friendEntities);

    /**
     * Generate interface for accessing instances of given object
     *
     * @param module is module we generate; repository is prefixed with this module
     * @param packageName is full name of root of package sources will be placed in; subpackages are created based on
     *                   convention (impl, dbloader), api is placed in root package
     * @param entityNm is internal name of entity class should be based on
     * @param attrs is comma separated list of attributes that should be used; if empty, all C attributes will be
     *             generated
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    JavaFile generateInterface(String module, String packageName, String entityNm, @Nullable String attrs,
                               @Nullable String friendEntities);

    /**
     * Generate class with meta-information
     *
     * @param module is module we generate; repository is prefixed with this module
     * @param packageName is full name of root of package sources will be placed in; subpackages are created based on
     *                   convention (impl, dbloader), api is placed in root package
     * @param entityNm is internal name of entity class should be based on
     * @param attrs is comma separated list of attributes that should be used; if empty, all C attributes will be
     *             generated
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    JavaFile generateMeta(String module, String packageName, String entityNm, @Nullable String attrs,
                          @Nullable String friendEntities);

    /**
     * Generate generated proxy ancestor, implementing interface of given object in scope defined by generated interface
     *
     * @param module is module we generate; repository is prefixed with this module
     * @param packageName is full name of root of package sources will be placed in; subpackages are created based on
     *                   convention (impl, dbloader), api is placed in root package
     * @param entityNm is internal name of entity class should be based on
     * @param attrs is comma separated list of attributes that should be used; if empty, all C attributes will be
     *             generated
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    JavaFile generateGenProxy(String module, String packageName, String entityNm, @Nullable String attrs,
                              @Nullable String friendEntities);

    /**
     * Generate proxy - simple envelope on generated proxy file. Used only for initialisation, later not regenerated
     *
     * @param module is module we generate; repository is prefixed with this module
     * @param packageName is full name of root of package sources will be placed in; subpackages are created based on
     *                   convention (impl, dbloader), api is placed in root package
     * @param entityNm is internal name of entity class should be based on
     * @param attrs is comma separated list of attributes that should be used; if empty, all C attributes will be
     *             generated
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    JavaFile generateProxy(String module, String packageName, String entityNm, @Nullable String attrs,
                           @Nullable String friendEntities);

    /**
     * Generate Jackson converter for proxy serialization via value class
     *
     * @param module is module we generate; repository is prefixed with this module
     * @param packageName is full name of root of package sources will be placed in; subpackages are created based on
     *                   convention (impl, dbloader), api is placed in root package
     * @param entityNm is internal name of entity class should be based on
     * @param attrs is comma separated list of attributes that should be used; if empty, all C attributes will be
     *             generated
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    JavaFile generateProxySerializationConverter(String module, String packageName, String entityNm,
                                                 @Nullable String attrs, @Nullable String friendEntities);

    /**
     * Generate value class - whole value class is generated, potential logic is implemented in proxy
     *
     * @param module is module we generate; repository is prefixed with this module
     * @param packageName is full name of root of package sources will be placed in; subpackages are created based on
     *                   convention (impl, dbloader), api is placed in root package
     * @param entityNm is internal name of entity class should be based on
     * @param attrs is comma separated list of attributes that should be used; if empty, all C attributes will be
     *             generated
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    JavaFile generateValue(String module, String packageName, String entityNm, @Nullable String attrs,
                           @Nullable String friendEntities);

    /**
     * Generate value builder class - whole value class is generated
     *
     * @param module is module we generate; repository is prefixed with this module
     * @param packageName is full name of root of package sources will be placed in; subpackages are created based on
     *                   convention (impl, dbloader), api is placed in root package
     * @param entityNm is internal name of entity class should be based on
     * @param attrs is comma separated list of attributes that should be used; if empty, all C attributes will be
     *             generated
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    JavaFile generateValueBuilder(String module, String packageName, String entityNm, @Nullable String attrs,
                                  @Nullable String friendEntities);

    /**
     * Generate jackson serializer for value builder class
     *
     * @param module is module we generate; repository is prefixed with this module
     * @param packageName is full name of root of package sources will be placed in; subpackages are created based on
     *                   convention (impl, dbloader), api is placed in root package
     * @param entityNm is internal name of entity class should be based on
     * @param attrs is comma separated list of attributes that should be used; if empty, all C attributes will be
     *             generated
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    JavaFile generateValueBuilderSerializer(String module, String packageName, String entityNm, @Nullable String attrs,
                                            @Nullable String friendEntities);

    /**
     * Generate loader interface - generated template for loader interface, that is later manually modified
     *
     * @param module is module we generate; repository is prefixed with this module
     * @param packageName is full name of root of package sources will be placed in; subpackages are created based on
     *                   convention (impl, dbloader), api is placed in root package
     * @param entityNm is internal name of entity class should be based on
     * @param attrs is comma separated list of attributes that should be used; if empty, all C attributes will be
     *             generated
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    JavaFile generateLoaderInterface(String module, String packageName, String entityNm, @Nullable String attrs,
                                     @Nullable String friendEntities);

    /**
     * Generate loader base - generated template for loader base class, that is later manually modified
     *
     * @param module is module we generate; repository is prefixed with this module
     * @param packageName is full name of root of package sources will be placed in; subpackages are created based on
     *                   convention (impl, dbloader), api is placed in root package
     * @param entityNm is internal name of entity class should be based on
     * @param attrs is comma separated list of attributes that should be used; if empty, all C attributes will be
     *             generated
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    JavaFile generateLoaderBase(String module, String packageName, String entityNm, @Nullable String attrs,
                                @Nullable String friendEntities);

    /**
     * Generate database loader - at the moment, does not generate load by methods
     *
     * @param module is module we generate; repository is prefixed with this module
     * @param packageName is full name of root of package sources will be placed in; subpackages are created based on
     *                   convention (impl, dbloader), api is placed in root package
     * @param entityNm is internal name of entity class should be based on
     * @param attrs is comma separated list of attributes that should be used; if empty, all C attributes will be
     *             generated
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    JavaFile generateDbLoader(String module, String packageName, String entityNm, @Nullable String attrs,
                              @Nullable String friendEntities);

    /**
     * Generate database load runner - class that actually fetches data from database and converts them to value objects
     *
     * @param module is module we generate; repository is prefixed with this module
     * @param packageName is full name of root of package sources will be placed in; subpackages are created based on
     *                   convention (impl, dbloader), api is placed in root package
     * @param entityNm is internal name of entity class should be based on
     * @param attrs is comma separated list of attributes that should be used; if empty, all C attributes will be
     *             generated
     * @param friendEntities is comma separated list of internal names of entities under the same repository; generator
     *                      generates getters returning object instead of just Id for foreign keys of these types
     * @return source code for given class
     */
    @Nonnull
    JavaFile generateDbLoadRunner(String module, String packageName, String entityNm, @Nullable String attrs,
                                  @Nullable String friendEntities);
}

package com.provys.provysobject.generator.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.provys.catalogue.api.*;
import com.provys.common.datatype.DtUid;
import com.provys.common.exception.InternalException;
import com.provys.common.exception.RegularException;
import com.provys.provysdb.dbcontext.DbResultSet;
import com.provys.provysdb.dbcontext.DbRowMapper;
import com.provys.provysdb.dbsqlbuilder.DbSql;
import com.provys.provysdb.dbsqlbuilder.SqlAdmin;
import com.provys.provysdb.sqlbuilder.*;
import com.provys.provysobject.ProvysNmObject;
import com.provys.provysobject.ProvysObject;
import com.provys.provysobject.generator.EntityGenerator;
import com.provys.provysobject.impl.*;
import com.squareup.javapoet.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import javax.lang.model.element.Modifier;
import javax.xml.bind.annotation.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;

@SuppressWarnings("squid:S1192") // we do not mind repeating string fragments...
class DefaultEntityGenerator implements EntityGenerator {

    @Nonnull
    private final CatalogueRepository catalogueRepository;
    @Nonnull
    private final Entity entity;
    @Nonnull
    private final Set<String> friendEntities;
    @Nonnull
    private final String packageNameApi;
    @Nonnull
    private final String packageNameImpl;
    @Nonnull
    private final String packageNameImplGen;
    @Nonnull
    private final String packageNameDbLoader;
    @Nonnull
    private final ClassName moduleRepositoryName;
    @Nonnull
    private final ClassName managerName;
    @Nonnull
    private final ClassName managerImplName;
    @Nonnull
    private final ClassName genInterfaceName;
    @Nonnull
    private final ClassName interfaceName;
    @Nonnull
    private final ClassName metaName;
    @Nonnull
    private final ClassName genProxyName;
    @Nonnull
    private final ClassName proxyName;
    @Nonnull
    private final ClassName proxySerializationConverter;
    @Nonnull
    private final ClassName valueName;
    @Nonnull
    private final ClassName valueBuilderName;
    @Nonnull
    private final ClassName valueBuilderSerializerName;
    @Nonnull
    private final ClassName loaderInterfaceName;
    @Nonnull
    private final ClassName loaderBaseName;
    @Nonnull
    private final ClassName dbLoaderName;
    @Nonnull
    private final ClassName dbLoadRunnerName;
    @Nonnull
    private final List<GeneratorAttr> cAttrs;
    @Nonnull
    private final AnnotationSpec generatedAnnotation = AnnotationSpec
            .builder(Generated.class)
            .addMember("value", "\"com.provys.provysobject.generator.impl.GeneratorEntity\"")
            .build();

    @Nonnull
    private static String getPackageName(String module, @Nullable String packageName) {
        if ((packageName != null) && (!packageName.isBlank())) {
            return packageName.toLowerCase(Locale.ENGLISH);
        }
        return "com.provys." + module.toLowerCase(Locale.ENGLISH);
    }

    DefaultEntityGenerator(CatalogueRepository catalogueRepository, Entity entity, String module,
                           @Nullable String packageName, Collection<String> friendEntities) {
        this.catalogueRepository = Objects.requireNonNull(catalogueRepository);
        this.entity = Objects.requireNonNull(entity);
        this.friendEntities = new HashSet<>(friendEntities);
        this.friendEntities.add(entity.getNameNm()); // we are always friends with ourselves
        packageName = packageName.toLowerCase(Locale.ENGLISH);
        this.packageNameApi = packageName;
        this.packageNameImpl = packageName + ".impl";
        this.packageNameImplGen = packageNameImpl;
        this.packageNameDbLoader = packageName + ".dbloader";
        var entityName = getcProperName();
        this.moduleRepositoryName = ClassName.get(packageNameApi,
                Character.toLowerCase(module.charAt(0)) + module.substring(1).toLowerCase(Locale.ENGLISH));
        this.managerName = ClassName.get(packageNameApi, entityName + "Manager");
        this.managerImplName = ClassName.get(packageNameImpl, entityName + "ManagerImpl");
        this.genInterfaceName =  ClassName.get(packageNameApi, "Gen" + entityName);
        this.interfaceName = ClassName.get(packageNameApi, entityName);
        this.metaName = ClassName.get(packageNameApi, entityName + "Meta");
        this.genProxyName = ClassName.get(packageNameImplGen, "Gen" + entityName + "Proxy");
        this.proxyName = ClassName.get(packageNameImpl, entityName + "Proxy");
        this.proxySerializationConverter = ClassName.get(packageNameImpl, "Gen" + entityName +
                "ProxySerializationConverter");
        this.valueName = ClassName.get(packageNameImplGen, "Gen" + entityName + "Value");
        this.valueBuilderName = ClassName.get(packageNameImplGen, "Gen" + entityName + "ValueBuilder");
        this.valueBuilderSerializerName = ClassName.get(packageNameImplGen, "Gen" + entityName +
                "ValueBuilderSerializer");
        this.loaderInterfaceName = ClassName.get(packageNameImpl, entityName + "Loader");
        this.loaderBaseName = ClassName.get(packageNameImpl, entityName + "LoaderBase");
        this.dbLoaderName = ClassName.get(packageNameDbLoader, entityName + "DbLoader");
        this.dbLoadRunnerName = ClassName.get(packageNameDbLoader, entityName + "DbLoadRunner");
        this.cAttrs = buildCAttrs();
    }

    /**
     * Get (c)olumn attributes in entity, excluding key (as it is always treated separately)
     *
     * @return collection of column attributes for generators
     */
    private List<GeneratorAttr> buildCAttrs() {
        return entity.getAttrs().stream()
                .filter(attr -> (attr.getAttrType() == 'C'))
                .map(attr -> new GeneratorAttr(this, attr))
                .filter(attr -> (!attr.isKey()))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * @return value of field catalogueRepository
     */
    @Nonnull
    CatalogueRepository getCatalogueRepository() {
        return catalogueRepository;
    }

    @Nonnull
    public Entity getEntity() {
        return entity;
    }

    @Nonnull
    public Set<String> getFriendEntities() {
        return Collections.unmodifiableSet(friendEntities);
    }

    @Nonnull
    public Optional<String> getTableNm() {
        return entity.getTableNm();
    }

    @Nonnull
    public String getKeyNm() {
        return entity.getKeyNm()
                .orElseThrow(() -> new RegularException("PROVYSOBJECTGEN_ENTITY_HAS_NP_KEY",
                        "Cannot generate repository for entity without key (" + getNameNm() + ")"));
    }

    @Nonnull
    public String getNameNm() {
        return entity.getNameNm();
    }

    @Nonnull
    String getPackageNameApi() {
        return packageNameApi;
    }

    @Nonnull
    String getPackageNameImpl() {
        return packageNameImpl;
    }

    @Nonnull
    String getcProperName() {
        return entity.getcProperName();
    }

    private boolean hasNmAttr() {
        for (var attr : entity.getAttrs()) {
            if (attr.getNameNm().equals("NAME_NM")) {
                return attr.getKeyOrd().filter(keyord -> (keyord == 1)).isPresent();
            }
        }
        return false;
    }

    @Nonnull
    private String getPropertyOrderValue() {
        var builder = new StringBuilder()
                .append("{\"id\"");
        for (var attr : cAttrs) {
            builder.append(", \"").append(attr.getFieldName()).append('"');
        }
        return builder
                .append("}")
                .toString();
    }

    @Nonnull
    private Collection<MethodSpec> getGenInterfaceGetters() {
        var result = new ArrayList<MethodSpec>(cAttrs.size() * 2);
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                if (attr.useObjectReference()) {
                    result.add(
                            attr.getRefGetterBuilder()
                                    .addJavadoc("@return $L (object referenced by attribute $L)\n", attr.getName(),
                                            attr.getNameNm())
                                    .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                                    .build()
                    );
                }
                result.add(
                        attr.getGetterBuilder()
                                .addJavadoc("@return $L $L(attribute $L)\n",
                                        attr.getName(), attr.useObjectReference() ? "Id " : "", attr.getNameNm())
                                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                                .build()
                );
            }
        }
        return result;
    }

    @Override
    @Nonnull
    public JavaFile generateGenInterface() {
        return JavaFile.builder(packageNameApi,
                TypeSpec.interfaceBuilder(genInterfaceName)
                        .addAnnotation(generatedAnnotation)
                        .addSuperinterface(hasNmAttr() ? ProvysNmObject.class : ProvysObject.class)
                        .addMethods(getGenInterfaceGetters())
                        .build())
                .skipJavaLangImports(true)
                .indent("    ")
                .build();
    }

    @Override
    @Nonnull
    public JavaFile generateInterface() {
        return JavaFile.builder(packageNameApi,
                TypeSpec.interfaceBuilder(interfaceName)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(genInterfaceName)
                        .build())
                .skipJavaLangImports(true)
                .indent("    ")
                .build();
    }

    List<FieldSpec> getMetaFields() {
        var result = new ArrayList<FieldSpec>(cAttrs.size());
        for (var attr : cAttrs) {
            result.add(FieldSpec
                    .builder(
                            ParameterizedTypeName.get(SqlColumnT.class,
                                    attr.getDomain().getImplementingClass(true)),
                            "COL_" + attr.getNameNm())
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$T.column(TABLE_ALIAS, name($S), $T.class)", SqlFactory.class,
                            attr.getNameNm().toLowerCase(Locale.ENGLISH), attr.getDomain()
                                    .getImplementingClass(true))
                    .build());
        }
        return result;
    }

    @Override
    @Nonnull
    public JavaFile generateMeta() {
        return JavaFile.builder(packageNameApi,
                TypeSpec.classBuilder(metaName)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addField(FieldSpec
                                .builder(SqlIdentifier.class, "TABLE")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                .initializer("$T.name($S)", SqlFactory.class, getTableNm()
                                        .orElseThrow(() -> new InternalException(
                                                "Cannot generate metainformation - table not set for entity "
                                                        + getNameNm())).toLowerCase(Locale.ENGLISH))
                                .build())
                        .addField(FieldSpec
                                .builder(SqlTableAlias.class, "TABLE_ALIAS")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                .initializer("$T.tableAlias($S)", SqlFactory.class, "al"
                                        + getNameNm().toLowerCase(Locale.ENGLISH))
                                .build())
                        .addField(FieldSpec
                                .builder(SqlFrom.class, "FROM_CLAUSE")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                .initializer("$T.from(TABLE, TABLE_ALIAS)", SqlFactory.class)
                                .build())
                        .addField(FieldSpec
                                .builder(ParameterizedTypeName.get(SqlColumnT.class, DtUid.class),
                                        "COL_" + getKeyNm())
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                .initializer("$T.column(TABLE_ALIAS, $T.name($S), $T.class)", SqlFactory.class,
                                        SqlFactory.class, getKeyNm().toLowerCase(Locale.ENGLISH), DtUid.class)
                                .build())
                        .addFields(getMetaFields())
                        .addMethod(MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PRIVATE)
                                .build())
                        .build())
                .addStaticImport(SqlFactory.class, "*")
                .skipJavaLangImports(true)
                .indent("    ")
                .build();
    }

    @Nonnull
    private MethodSpec getGenProxyConstructor() {
        return MethodSpec.constructorBuilder()
                .addParameter(managerImplName, "manager")
                .addParameter(DtUid.class, "id")
                .addStatement("super(manager, id)")
                .build();
    }

    @Nonnull
    private CodeBlock getGenProxyRefGetterStatement(GeneratorAttr attr) {
        var result = CodeBlock.builder();
        if (attr.isMandatory()) {
            result.addStatement("return getManager().getRepository().get$LManager()." +
                            "getById($L())",
                    catalogueRepository.getEntityManager().getByNameNm(attr.getSubdomainNm().orElseThrow())
                            .getcProperName(), attr.getGetterName());
        } else {
            result.addStatement("return $L().map(id -> getManager().getRepository().get$LManager()." +
                            "getById(id))", attr.getGetterName(),
                    catalogueRepository.getEntityManager().getByNameNm(attr.getSubdomainNm().orElseThrow())
                            .getcProperName());
        }
        return result.build();
    }

    @Nonnull
    private Collection<MethodSpec> getGenProxyGetters() {
        var result = new ArrayList<MethodSpec>(cAttrs.size() * 2);
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                if (attr.useObjectReference()) {
                    result.add(attr.getRefGetterBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addCode(getGenProxyRefGetterStatement(attr))
                            .build());
                }
                result.add(attr.getGetterBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return validateValueObject().$L()", attr.getGetterName())
                        .build());
            }
        }
        return result;
    }

    @Override
    @Nonnull
    public JavaFile generateGenProxy() {
        return JavaFile.builder(packageNameImplGen,
                TypeSpec.classBuilder(genProxyName)
                        .addAnnotation(generatedAnnotation)
                        .addModifiers(Modifier.ABSTRACT)
                        .superclass(ParameterizedTypeName.get(
                                hasNmAttr() ? ClassName.get(ProvysNmObjectProxyImpl.class) :
                                        ClassName.get(ProvysObjectProxyImpl.class),
                                interfaceName, valueName, proxyName, managerImplName))
                        .addMethod(getGenProxyConstructor())
                        .addMethods(getGenProxyGetters())
                        .build())
                .skipJavaLangImports(true)
                .indent("    ")
                .build();
    }

    @Nonnull
    private MethodSpec getProxyConstructor() {
        return MethodSpec.constructorBuilder()
                .addParameter(managerImplName, "manager")
                .addParameter(DtUid.class, "id")
                .addStatement("super(manager, id)")
                .build();
    }

    @Nonnull
    private MethodSpec getProxySelf() {
        return MethodSpec.methodBuilder("self")
                .addAnnotation(Nonnull.class)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .returns(proxyName)
                .addStatement("return this")
                .build();
    }

    @Nonnull
    private MethodSpec getProxySelfAsObject() {
        return MethodSpec.methodBuilder("selfAsObject")
                .addAnnotation(Nonnull.class)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(interfaceName)
                .addStatement("return this")
                .build();
    }

    @Override
    @Nonnull
    public JavaFile generateProxy() {
        return JavaFile.builder(packageNameImpl,
                TypeSpec.classBuilder(proxyName)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(AnnotationSpec
                                .builder(JsonSerialize.class)
                                .addMember("converter", "$T.class", proxySerializationConverter)
                                .build())
                        .addAnnotation(AnnotationSpec
                                .builder(XmlRootElement.class)
                                .addMember("name", "$S", getNameNm())
                                .build())
                        .superclass(genProxyName)
                        .addSuperinterface(interfaceName)
                        .addMethod(getProxyConstructor())
                        .addMethod(getProxySelf())
                        .addMethod(getProxySelfAsObject())
                        .build())
                .skipJavaLangImports(true)
                .indent("    ")
                .build();
    }

    @Override
    @Nonnull
    public JavaFile generateProxySerializationConverter() {
        return JavaFile.builder(packageNameImpl,
                TypeSpec.classBuilder(proxySerializationConverter)
                        .superclass(ParameterizedTypeName.get(ClassName.get(StdConverter.class), proxyName, valueName))
                        .addMethod(MethodSpec
                                .methodBuilder("convert")
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(proxyName, "proxy")
                                .returns(valueName)
                                .addStatement("return proxy.validateValueObject()")
                                .build())
                        .build())
                .skipJavaLangImports(true)
                .indent("    ")
                .build();
    }

    @Nonnull
    private Collection<FieldSpec> getValueFields() {
        var result = new ArrayList<FieldSpec>(cAttrs.size());
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                result.add(attr.getFieldSpec());
            }
        }
        return result;
    }

    @Nonnull
    private MethodSpec getValueConstructor() {
        MethodSpec.Builder constructorBuilder = MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec
                        .builder(JsonCreator.class).build())
                .addParameter(ParameterSpec
                        .builder(DtUid.class, "id")
                        .addAnnotation(AnnotationSpec
                                .builder(JsonProperty.class)
                                .addMember("value", "$S", getKeyNm())
                                .build())
                        .build());
        for (var attr : cAttrs) {
            ParameterSpec.Builder parameterBuilder = ParameterSpec
                    .builder(attr.getFieldTypeName(), attr.getFieldName())
                    .addAnnotation(AnnotationSpec
                            .builder(JsonProperty.class)
                            .addMember("value", "$S", attr.getNameNm())
                            .build());
            if (!attr.isMandatory()) {
                parameterBuilder.addAnnotation(Nullable.class);
            }
            constructorBuilder.addParameter(parameterBuilder.build());
        }
        constructorBuilder.addStatement("super(id" + (hasNmAttr() ? ", nameNm" : "") + ")");
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                if (!attr.isMandatory() || attr.getDomain().getImplementingClass(false).isPrimitive()) {
                    constructorBuilder.addStatement("this.$1L = $1L", attr.getFieldName());
                } else {
                    constructorBuilder.addStatement("this.$1L = $2T.requireNonNull($1L)", attr.getFieldName(),
                            Objects.class);
                }
            }
        }
        return constructorBuilder.build();
    }

    @Nonnull
    private Collection<MethodSpec> getValueGetters() {
        var result = new ArrayList<MethodSpec>(cAttrs.size() * 2);
        // needed to add proper element name for JAXB serialisation
        result.add(MethodSpec
                .methodBuilder("getId")
                .returns(DtUid.class)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec
                        .builder(XmlElement.class)
                        .addMember("name", "$S", getKeyNm())
                        .build())
                .addAnnotation(Nonnull.class)
                .addAnnotation(Override.class)
                .addStatement("return super.getId()")
                .build());
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                result.add(attr.getGetterBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement(attr.isMandatory() ? "return $1L" : "return Optional.ofNullable($1L)",
                                attr.getFieldName())
                        .build()
                );
            }
        }
        return result;
    }

    private void addValueAttrEquality(CodeBlock.Builder eqStatement, GeneratorAttr attr) {
        if (attr.isMandatory()) {
            if (attr.getDomain().getImplementingClass(false).isPrimitive()) {
                eqStatement.add("$1L == that.$1L", attr.getFieldName());
            } else {
                eqStatement.add("$1L.equals(that.$1L)",
                        (hasNmAttr() && attr.getNameNm().equals("NAME_NM")) ?
                                "getNameNm()" : attr.getFieldName());
            }
        } else {
            eqStatement.add("$1T.equals($2L, that.$2L)", Objects.class, attr.getFieldName());
        }
    }

    @Nonnull
    private CodeBlock getValueEqStatement() {
        var eqStatement = CodeBlock.builder().add("return ");
        boolean first = true;
        for (var attr : cAttrs) {
            if (first) {
                first = false;
            } else {
                eqStatement.add(" &&\n        ");
            }
            addValueAttrEquality(eqStatement, attr);
        }
        return eqStatement.add(";\n").build();
    }

    @Nonnull
    private MethodSpec getValueEquals() {
        return MethodSpec.methodBuilder("equals")
                .addAnnotation(Override.class)
                .addAnnotation(
                        AnnotationSpec.builder(SuppressWarnings.class)
                                .addMember("value", "\"squid:S1206\"")
                                .build())
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addParameter(ParameterSpec
                        .builder(Object.class, "o")
                        .addAnnotation(Nullable.class)
                        .build())
                .addStatement("if (this == o) return true")
                .addStatement("if (!(o instanceof $T)) return false", valueName)
                .addStatement("if (!super.equals(o)) return false")
                .addStatement("$1T that = ($1T) o", valueName)
                .addCode(getValueEqStatement())
                .build();
    }

    @Override
    @Nonnull
    public JavaFile generateValue() {
        return JavaFile.builder(packageNameImplGen,
                TypeSpec.classBuilder(valueName)
                        .addAnnotation(generatedAnnotation)
                        .addAnnotation(AnnotationSpec
                                .builder(SuppressWarnings.class)
                                .addMember("value", "$S", "ValidExternallyBoundObject")
                                .build())
                        .addAnnotation(AnnotationSpec
                                .builder(XmlAccessorType.class)
                                .addMember("value", "$T.NONE", XmlAccessType.class)
                                .build())
                        .addAnnotation(AnnotationSpec
                                .builder(XmlType.class)
                                .addMember("propOrder", getPropertyOrderValue())
                                .build())
                        .addAnnotation(AnnotationSpec
                                .builder(XmlRootElement.class)
                                .addMember("name", "$S", getNameNm())
                                .build())
                        .addModifiers(Modifier.PUBLIC)
                        .superclass(hasNmAttr() ? ProvysNmObjectValue.class : ProvysObjectValue.class)
                        .addFields(getValueFields())
                        .addMethod(getValueConstructor())
                        .addMethods(getValueGetters())
                        .addMethod(getValueEquals())
                        .build())
                .skipJavaLangImports(true)
                .indent("    ")
                .build();
    }

    @Nonnull
    private Collection<FieldSpec> getValueBuilderFields() {
        var result = new ArrayList<FieldSpec>(cAttrs.size());
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                result.add(attr.getBuilderFieldSpec());
                result.add(attr.getBuilderUpdFieldSpec());
            }
        }
        return result;
    }

    @Nonnull
    private CodeBlock getValueBuilderValueConstructorBody() {
        var result = CodeBlock.builder()
                .addStatement("super(value)");
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                if (attr.isMandatory()) {
                    result.addStatement("$L(value.$L())", attr.getSetterName(), attr.getGetterName());
                } else {
                    result.addStatement("$L(value.$L().orElse(null))", attr.getSetterName(), attr.getGetterName());
                }
            }
        }
        return result.build();
    }

    @Nonnull
    private CodeBlock getValueBuilderBuilderConstructorBody() {
        var result = CodeBlock.builder()
                .addStatement("super(value)");
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                result.addStatement("this.$L = value.$L", attr.getJavaName(), attr.getJavaName())
                        .addStatement("this.upd$L = value.upd$L", attr.getInitCapJavaName(), attr.getInitCapJavaName());
            }
        }
        return result.build();
    }

    @Nonnull
    private Collection<MethodSpec> getValueBuilderConstructors() {
        var result = new ArrayList<MethodSpec>(3);
        result.add(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build());
        result.add(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(valueName, "value")
                .addCode(getValueBuilderValueConstructorBody())
                .build());
        result.add(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(valueBuilderName, "value")
                .addCode(getValueBuilderBuilderConstructorBody())
                .build());
        return result;
    }

    @Nonnull
    private CodeBlock getValueBuilderUpdSetterCode(GeneratorAttr attr) {
        var result = CodeBlock.builder();
        if (attr.isMandatory()) {
            result.beginControlFlow("if (!this.$L && $L)", attr.getUpdFieldName(), attr.getUpdFieldName())
                    .addStatement("throw new $T(\"Cannot directly set update flag $L; set value instead\")",
                            InternalException.class, attr.getUpdFieldName())
                    .endControlFlow();
        }
        result.addStatement("this.$L = $L", attr.getUpdFieldName(), attr.getUpdFieldName())
                .beginControlFlow("if (!$L)", attr.getUpdFieldName())
                .addStatement("this.$L = null", attr.getJavaName())
                .endControlFlow();
        return result.build();
    }

    @Nonnull
    private Collection<MethodSpec> getValueBuilderGetters() {
        var result = new ArrayList<MethodSpec>(cAttrs.size() * 2);
        result.add(MethodSpec
                .methodBuilder("getId")
                .addModifiers(Modifier.PUBLIC)
                .returns(DtUid.class)
                .addAnnotation(AnnotationSpec
                        .builder(XmlElement.class)
                        .addMember("name", "$S", getKeyNm())
                        .build())
                .addAnnotation(Override.class)
                .addAnnotation(Nullable.class)
                .addStatement("return super.getId()")
                .build());
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                result.add(MethodSpec.methodBuilder(attr.getGetterName())
                        .returns(attr.getBuilderFieldTypeName())
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(AnnotationSpec
                                .builder(XmlElement.class)
                                .addMember("name", '"' + attr.getNameNm() + '"')
                                .build())
                        .addAnnotation(Nullable.class)
                        .addStatement("return $L", attr.getJavaName())
                        .build()
                );
                result.add(MethodSpec.methodBuilder(attr.getSetterName())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(valueBuilderName)
                        .addParameter(attr.getBuilderSetterParam())
                        .addStatement(attr.isMandatory() ? "this.$L = Objects.requireNonNull($L)" : "this.$L = $L",
                                attr.getJavaName(), attr.getJavaName())
                        .addStatement("this.$L = true", attr.getUpdFieldName())
                        .addStatement("return self()")
                        .build()
                );
                result.add(MethodSpec.methodBuilder(attr.getUpdGetterName())
                        .returns(boolean.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return $L", attr.getUpdFieldName())
                        .build()
                );
                result.add(MethodSpec.methodBuilder(attr.getUpdSetterName())
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(boolean.class, attr.getUpdFieldName())
                        .addCode(getValueBuilderUpdSetterCode(attr))
                        .build()
                );
            }
        }
        return result;
    }

    @Nonnull
    private MethodSpec getValueBuilderSelf() {
        return MethodSpec.methodBuilder("self")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addAnnotation(Nonnull.class)
                .returns(valueBuilderName)
                .addStatement("return this")
                .build();
    }

    @Nonnull
    private MethodSpec getValueBuilderCopy() {
        return MethodSpec.methodBuilder("copy")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addAnnotation(Nonnull.class)
                .returns(valueBuilderName)
                .addStatement("return new $T(this)", valueBuilderName)
                .build();
    }

    @Nonnull
    private CodeBlock getValueBuilderBuildCode() {
        var result = CodeBlock.builder()
                .add("return new $T($T.requireNonNull(getId(), \"$L must be specified for build\")\n",
                        valueName, Objects.class, getKeyNm());
        for (var attr : cAttrs) {
            if (attr.isMandatory()) {
                result.add(", $T.requireNonNull($L(), \"$L must be specified for build\")\n",
                        Objects.class, attr.getGetterName(), attr.getNameNm());
            } else {
                result.add(", $L()\n", attr.getGetterName());
            }
        }
        result.add(");\n");
        return result.build();
    }

    @Nonnull
    private MethodSpec getValueBuilderBuild() {
        return MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addAnnotation(Nonnull.class)
                .returns(valueName)
                .addCode(getValueBuilderBuildCode())
                .build();
    }

    @Nonnull
    private CodeBlock getValueBuilderEqStatement() {
        var eqStatement = CodeBlock.builder().add("return ");
        boolean first = true;
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                if (first) {
                    first = false;
                } else {
                    eqStatement.add(" &&\n        ");
                }
                eqStatement.add("$1T.equals($2L, that.$2L)", Objects.class, attr.getFieldName());
                eqStatement.add(" &&\n        ($1L == that.$1L)", attr.getUpdFieldName());
            }
        }
        return eqStatement.add(";\n").build();
    }

    @Nonnull
    private MethodSpec getValueBuilderEquals() {
        return MethodSpec.methodBuilder("equals")
                .addAnnotation(Override.class)
                .addAnnotation(
                        AnnotationSpec.builder(SuppressWarnings.class)
                                .addMember("value", "\"squid:S1206\"")
                                .build())
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addParameter(ParameterSpec
                        .builder(Object.class, "o")
                        .addAnnotation(Nullable.class)
                        .build())
                .addStatement("if (this == o) return true")
                .addStatement("if (!(o instanceof $T)) return false", valueBuilderName)
                .addStatement("if (!super.equals(o)) return false")
                .addStatement("$1T that = ($1T) o", valueBuilderName)
                .addCode(getValueBuilderEqStatement())
                .build();
    }

    @Nonnull
    private CodeBlock getValueBuilderToStringCode() {
        var result = CodeBlock.builder()
                .add("return \"$LValueBuilder{\" +\n", getcProperName());
        boolean first = true;
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                if (first) {
                    first = false;
                    result.add("        \"  ");
                } else {
                    result.add("        \", ");
                }
                result.add("$L =", attr.getJavaName());
                if (attr.getDomain().getImplementingClass(true).equals(String.class)) {
                    result.add("'\" + $L + '\\'' +\n", attr.getFieldName());
                } else {
                    result.add("\" + $L +\n", attr.getFieldName());
                }
                result.add("        \", $L = \" + $L +\n", attr.getUpdFieldName(), attr.getUpdFieldName());
            }
        }
        result.add("        \"} \" + super.toString();\n");
        return result.build();
    }

    @Nonnull
    private MethodSpec getValueBuilderToString() {
        return MethodSpec.methodBuilder("toString")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addCode(getValueBuilderToStringCode())
                .build();
    }

    @Override
    @Nonnull
    public JavaFile generateValueBuilder() {
        return JavaFile.builder(packageNameImplGen,
                TypeSpec.classBuilder(valueBuilderName)
                        .addAnnotation(generatedAnnotation)
                        .addAnnotation(AnnotationSpec
                                .builder(XmlRootElement.class)
                                .addMember("name", '"' + getNameNm() + '"')
                                .build())
                        .addAnnotation(AnnotationSpec
                                .builder(XmlAccessorType.class)
                                .addMember("value", "$T.NONE", XmlAccessType.class)
                                .build())
                        .addAnnotation(AnnotationSpec
                                .builder(JsonSerialize.class)
                                .addMember("using", "$T.class", valueBuilderSerializerName)
                                .build())
                        .addAnnotation(AnnotationSpec
                                .builder(SuppressWarnings.class)
                                .addMember("value",
                                        "{\"WeakerAccess\", \"unused\", \"UnusedReturnValue\"}")
                                .build())
                        .addModifiers(Modifier.PUBLIC)
                        .superclass(ParameterizedTypeName.get(
                                ClassName.get(
                                        hasNmAttr() ? ProvysNmObjectValueBuilder.class : ProvysObjectValueBuilder.class),
                                valueBuilderName, valueName))
                        .addFields(getValueBuilderFields())
                        .addMethods(getValueBuilderConstructors())
                        .addMethods(getValueBuilderGetters())
                        .addMethod(getValueBuilderSelf())
                        .addMethod(getValueBuilderCopy())
                        .addMethod(getValueBuilderBuild())
                        .addMethod(getValueBuilderEquals())
                        .addMethod(getValueBuilderToString())
                        .build())
                .skipJavaLangImports(true)
                .indent("    ")
                .build();
    }

    private void appendValueBuilderSerializerSerializeField(CodeBlock.Builder builder, GeneratorAttr attr) {
        builder.addStatement("var value = builder.$L()", attr.getGetterName());
        builder.beginControlFlow("if (value == null)")
                .addStatement("generator.writeNullField($S)", attr.getNameNm())
                .nextControlFlow("else");
        if (attr.getDomain().getImplementingClass(true) == String.class) {
            builder.addStatement("generator.writeStringField($S, value)", attr.getNameNm());
        } else if ((attr.getDomain().getImplementingClass(true) == Byte.class) ||
                (attr.getDomain().getImplementingClass(true) == Short.class) ||
                (attr.getDomain().getImplementingClass(true) == Integer.class) ||
                (attr.getDomain().getImplementingClass(true) == Long.class) ||
                (attr.getDomain().getImplementingClass(true) == Float.class) ||
                (attr.getDomain().getImplementingClass(true) == Double.class) ||
                (attr.getDomain().getImplementingClass(true) == BigDecimal.class)) {
            builder.addStatement("generator.writeNumberField($S, value)", attr.getNameNm());
        } else if (attr.getDomain().getImplementingClass(true) == DtUid.class) {
            // overload of writeNumberField for DtUid is missing...
            builder.addStatement("generator.writeFieldName($S)", attr.getNameNm());
            builder.addStatement("generator.writeNumber(value.getValue())");
        } else if (attr.getDomain().getImplementingClass(true) == Boolean.class) {
            builder.addStatement("generator.writeBooleanField($S, value)", attr.getNameNm());
        } else {
            builder.addStatement("generator.writeObjectField($S, value)", attr.getNameNm());
        }
        builder.endControlFlow();
    }

    @Nonnull
    private CodeBlock getValueBuilderSerializerSerializeBody() {
        var result = CodeBlock.builder()
                .addStatement("generator.writeStartObject()")
                .beginControlFlow("if (builder.getId() != null)")
                .addStatement("generator.writeFieldName($S)", getKeyNm())
                .addStatement("generator.writeNumber(builder.getId().getValue())")
                .endControlFlow();
        if (hasNmAttr()) {
            // internal name does not have corresponding upd flag...
            result.beginControlFlow("if (builder.getNameNm() != null)")
                    .addStatement("generator.writeStringField(\"NAME_NM\", builder.getNameNm())")
                    .endControlFlow();
        }
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                result.beginControlFlow("if (builder.$L())", attr.getUpdGetterName());
                appendValueBuilderSerializerSerializeField(result, attr);
                result.endControlFlow();
            }
        }
        return result.addStatement("generator.writeEndObject()")
                .build();
    }

    @Nonnull
    private MethodSpec getValueBuilderSerializerSerialize() {
        return MethodSpec.methodBuilder("serialize")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addAnnotation(AnnotationSpec
                        .builder(SuppressWarnings.class)
                        .addMember("value", "$S", "squid:S3776")
                        .build())
                .addParameter(valueBuilderName, "builder")
                .addParameter(JsonGenerator.class, "generator")
                .addParameter(SerializerProvider.class, "serializerProvider")
                .addException(IOException.class)
                .addCode(getValueBuilderSerializerSerializeBody())
                .build();
    }

    @Override
    @Nonnull
    public JavaFile generateValueBuilderSerializer() {
        return JavaFile.builder(packageNameImplGen,
                TypeSpec.classBuilder(valueBuilderSerializerName)
                        .addAnnotation(generatedAnnotation)
                        .superclass(ParameterizedTypeName.get(
                                ClassName.get(JsonSerializer.class),
                                valueBuilderName))
                        .addMethod(getValueBuilderSerializerSerialize())
                        .build())
                .skipJavaLangImports(true)
                .indent("    ")
                .build();
    }

    @Override
    @Nonnull
    public JavaFile generateLoaderInterface() {
        return  JavaFile.builder(packageNameImpl,
                TypeSpec.interfaceBuilder(loaderInterfaceName)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(ParameterizedTypeName.get(
                                ClassName.get(hasNmAttr() ? ProvysNmObjectLoader.class : ProvysObjectLoader.class),
                                interfaceName, valueName, proxyName, managerImplName))
                        .build())
                .build();
    }

    @Override
    @Nonnull
    public JavaFile generateLoaderBase() {
        return  JavaFile.builder(packageNameImpl,
                TypeSpec.classBuilder(loaderBaseName)
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .superclass(ParameterizedTypeName.get(
                                ClassName.get(hasNmAttr() ? ProvysNmObjectLoaderImpl.class :
                                        ProvysObjectLoaderImpl.class),
                                interfaceName, valueName, proxyName, managerImplName))
                        .addSuperinterface(loaderInterfaceName)
                        .build())
                .build();
    }

    @Nonnull
    private MethodSpec getDbLoaderMethod(@Nullable GeneratorAttr attr) {
        return MethodSpec.methodBuilder("getLoadRunnerBy" +
                ((attr == null) ? "Id" : attr.getInitCapJavaName()))
                .addAnnotation(Nonnull.class)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .returns(dbLoadRunnerName)
                .addParameter(managerImplName, "manager")
                .addParameter((attr == null) ? DtUid.class : attr.getDomain().getImplementingClass(false),
                        (attr == null) ? "id" : attr.getJavaName())
                .addStatement(
                        "return new $LDbLoadRunner(manager, dbSql, dbSql.eq($T.COL_$L, dbSql.bind($S, $L)))",
                        getcProperName(), metaName, ((attr == null) ? getKeyNm() : attr.getNameNm()),
                        ((attr == null) ? getKeyNm().toLowerCase(Locale.ENGLISH) :
                                attr.getNameNm()).toLowerCase(Locale.ENGLISH),
                        (attr == null) ? "id" : attr.getJavaName())
                .build();
    }

    @Nonnull
    private MethodSpec getDbLoaderMethodAll() {
        return MethodSpec.methodBuilder("getLoadRunnerAll")
                .addAnnotation(Nonnull.class)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .returns(dbLoadRunnerName)
                .addParameter(managerImplName, "manager")
                .addStatement(
                        "return new $LDbLoadRunner(manager, dbSql, null)", getcProperName())
                .build();
    }

    @Nonnull
    private Collection<MethodSpec> getDbLoaderMethods() {
        var result = new ArrayList<MethodSpec>(5);
        for (var attr : cAttrs) {
            if (attr.getNameNm().equals("NAME_NM") && hasNmAttr()) {
                result.add(getDbLoaderMethod(attr));
            }
        }
        result.add(getDbLoaderMethod(null));
        result.add(getDbLoaderMethodAll());
        return result;
    }

    @Override
    @Nonnull
    public JavaFile generateDbLoader() {
        return  JavaFile.builder(packageNameDbLoader,
                TypeSpec.classBuilder(dbLoaderName)
                        .addModifiers(Modifier.PUBLIC)
                        .superclass(loaderBaseName)
                        .addField(FieldSpec
                                .builder(SqlAdmin.class,"dbSql")
                                .addAnnotation(Nonnull.class)
                                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                .build())
                        .addMethod(MethodSpec.constructorBuilder()
                                .addParameter(SqlAdmin.class,"dbSql")
                                .addStatement("this.dbSql = $T.requireNonNull(dbSql)", Objects.class)
                                .build())
                        .addMethods(getDbLoaderMethods())
                        .build())
                .skipJavaLangImports(true)
                .indent("    ")
                .build();
    }

    @Nonnull
    private CodeBlock getDbLoadRunnerSelectBody() {
        var result = CodeBlock.builder()
                .add("return dbSql.select()\n")
                .add("        .from($T.FROM_CLAUSE)\n", metaName)
                .add("        .column($T.COL_$L)\n", metaName, getKeyNm());
        for (var attr : cAttrs) {
            result.add("        .column($T.COL_$L)\n", metaName, attr.getNameNm());
        }
        result.add("        .where(condition)\n")
                .add("        .prepare()\n")
                .add("        .fetch(MAPPER);\n");
        return result.build();
    }

    @Nonnull
    private MethodSpec getDbLoadRunnerMap() {
        var result = MethodSpec
                .methodBuilder("map")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(DbResultSet.class, "dbResultSet")
                .addParameter(long.class, "line")
                .returns(valueName);
        result.addCode("return new $T()\n", valueBuilderName);
        result.addCode("    .setId(dbResultSet.getNonnullDtUid(1))\n");
        int col = 2;
        for (var attr : cAttrs) {
            result.addCode("    .$L(dbResultSet.get$L$L($L))\n",
                    attr.getSetterName(), attr.isMandatory() ? "Nonnull" : "Nullable",
                    attr.getDomain().getImplementingClass(true).getSimpleName(), col++);
        }
        result.addCode("    .build();\n");
        return result.build();
    }

    @Override
    @Nonnull
    public JavaFile generateDbLoadRunner() {
        return  JavaFile.builder(packageNameDbLoader,
                TypeSpec.classBuilder(dbLoadRunnerName)
                        .superclass(ParameterizedTypeName.get(
                                ClassName.get(ProvysObjectLoadRunner.class),
                                interfaceName, valueName, proxyName, managerImplName))
                        .addField(FieldSpec
                                .builder(dbLoadRunnerName.nestedClass(getcProperName() + "DbMapper"), "MAPPER")
                                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                                .initializer("new $LDbMapper()", getcProperName())
                                .build())
                        .addField(FieldSpec
                                .builder(DbSql.class, "dbSql")
                                .addAnnotation(Nonnull.class)
                                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                .build())
                        .addField(FieldSpec.builder(Condition.class,"condition")
                                .addAnnotation(Nullable.class)
                                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                .build())
                        .addMethod(MethodSpec.constructorBuilder()
                                .addParameter(managerImplName, "manager")
                                .addParameter(DbSql.class,"dbSql")
                                .addParameter(ParameterSpec
                                        .builder(Condition.class,"condition")
                                        .addAnnotation(Nullable.class)
                                        .build())
                                .addStatement("super(manager)")
                                .addStatement("this.dbSql = $T.requireNonNull(dbSql)", Objects.class)
                                .addStatement("this.condition = condition")
                                .build())
                        .addMethod(MethodSpec
                                .methodBuilder("select")
                                .addAnnotation(Nonnull.class)
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PROTECTED)
                                .returns(ParameterizedTypeName.get(ClassName.get(List.class), valueName))
                                .addCode(getDbLoadRunnerSelectBody())
                                .build()
                        )
                        .addType(TypeSpec
                                .classBuilder(getcProperName() + "DbMapper")
                                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                                .addSuperinterface(
                                        ParameterizedTypeName.get(ClassName.get(DbRowMapper.class), valueName))
                                .addMethod(getDbLoadRunnerMap())
                                .build())
                        .build())
                .skipJavaLangImports(true)
                .indent("    ")
                .build();
    }
}

package com.provys.provysobject.generator.impl;

import com.provys.catalogue.api.*;
import com.provys.common.exception.InternalException;
import com.provys.provysobject.ProvysNmObject;
import com.provys.provysobject.ProvysObject;
import com.provys.provysobject.impl.*;
import com.squareup.javapoet.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.lang.model.element.Modifier;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("squid:S1192") // we do not mind repeating string fragments...
class GeneratorEntity {

    private static final Logger LOG = LogManager.getLogger(GeneratorEntity.class);

    @Nonnull
    private final CatalogueRepository catalogueRepository;
    @Nonnull
    private final Entity entity;
    @Nonnull
    private final Set<String> friendEntities;
    @Nonnull
    private final String packageNameApi;
    @Nonnull
    private final String packageNameApiGen;
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
    private final ClassName genProxyName;
    @Nonnull
    private final ClassName proxyName;
    @Nonnull
    private final ClassName jsonbProxyAdapter;
    @Nonnull
    private final ClassName xmlProxyAdapter;
    @Nonnull
    private final ClassName valueName;
    @Nonnull
    private final ClassName jsonbValueAdapter;
    @Nonnull
    private final ClassName xmlValueAdapter;
    @Nonnull
    private final ClassName valueBuilderName;
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
            .addMember("date", '"' + LocalDateTime.now().toString() + '"')
            .build();

    GeneratorEntity(CatalogueRepository catalogueRepository, Entity entity, String module,
                    Collection<String> friendEntities) {
        this.catalogueRepository = Objects.requireNonNull(catalogueRepository);
        this.entity = Objects.requireNonNull(entity);
        this.friendEntities = new HashSet<>(friendEntities);
        this.friendEntities.add(entity.getNameNm()); // we are always friends with ourselves
        var packageName = "com.provys." + module.toLowerCase();
        this.packageNameApi = packageName + ".api";
        this.packageNameApiGen = packageNameApi;
        this.packageNameImpl = packageName + ".impl";
        this.packageNameImplGen = packageNameImpl;
        this.packageNameDbLoader = packageName + ".dbloader";
        var entityName = getCProperName();
        this.moduleRepositoryName = ClassName.get(packageNameApi,
                Character.toLowerCase(module.charAt(0)) + module.substring(1).toLowerCase());
        this.managerName = ClassName.get(packageNameApi, entityName + "Manager");
        this.managerImplName = ClassName.get(packageNameImpl, entityName + "ManagerImpl");
        this.genInterfaceName =  ClassName.get(packageNameApiGen, "Gen" + entityName);
        this.interfaceName =  ClassName.get(packageNameApi, entityName);
        this.genProxyName = ClassName.get(packageNameImplGen, "Gen" + entityName + "Proxy");
        this.proxyName = ClassName.get(packageNameImpl, entityName + "Proxy");
        this.jsonbProxyAdapter = ClassName.get(packageNameImpl, "Jsonb" + entityName + "ProxyAdapter");
        this.xmlProxyAdapter = ClassName.get(packageNameImpl, "Xml" + entityName + "ProxyAdapter");
        this.valueName = ClassName.get(packageNameImplGen, entityName + "Value");
        this.jsonbValueAdapter = ClassName.get(packageNameImpl, "Jsonb" + entityName + "ValueAdapter");
        this.xmlValueAdapter = ClassName.get(packageNameImpl, "Xml" + entityName + "ValueAdapter");
        this.valueBuilderName = ClassName.get(packageNameImplGen, entityName + "ValueBuilder");
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
                .filter(attr -> (attr.getAttrType() == AttrType.COLUMN))
                .map(attr -> new GeneratorAttr(this, attr))
                .filter(attr -> (!attr.isKey()))
                .sorted()
                .collect(Collectors.toList());
    }

    @Nonnull
    public CatalogueRepository getCatalogueRepository() {
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
    public String getName() {
        return entity.getName();
    }

    @Nonnull
    public Optional<String> getTable() {
        return entity.getTable();
    }

    @Nonnull
    public Optional<String> getView() {
        return entity.getView();
    }

    @Nonnull
    public Optional<Entity> getAncestor() {
        return entity.getAncestor();
    }

    @Nonnull
    public Optional<String> getNote() {
        return entity.getNote();
    }

    @Nonnull
    public Collection<Attr> getAttrs() {
        return entity.getAttrs();
    }

    @Nonnull
    public String getNameNm() {
        return entity.getNameNm();
    }

    @Nonnull
    public BigInteger getId() {
        return entity.getId();
    }

    @Nonnull
    String getCProperName() {
        return Character.toUpperCase(entity.getNameNm().charAt(0)) + entity.getNameNm().substring(1).toLowerCase();
    }

    private boolean hasNmAttr() {
        // temporary implementation until attribute has keyOrd property
        return (entity.getNameNm().equals("ENTITY") || entity.getNameNm().equals("DOMAIN"));
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

    @Nonnull
    JavaFile generateGenInterface() {
        return JavaFile.builder(packageNameApiGen,
                TypeSpec.interfaceBuilder(genInterfaceName)
                        .addAnnotation(generatedAnnotation)
                        .addSuperinterface(hasNmAttr() ? ProvysNmObject.class : ProvysObject.class)
                        .addMethods(getGenInterfaceGetters())
                        .build())
                .build();
    }

    @Nonnull
    JavaFile generateInterface() {
        return JavaFile.builder(packageNameApi,
                TypeSpec.interfaceBuilder(interfaceName)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(genInterfaceName)
                        .build())
                .build();
    }

    @Nonnull
    private MethodSpec getGenProxyConstructor() {
        return MethodSpec.constructorBuilder()
                .addParameter(managerImplName, "manager")
                .addParameter(BigInteger.class, "id")
                .addStatement("super(manager, id)")
                .build();
    }

    @Nonnull
    private CodeBlock getGenProxyRefGetterStatement(GeneratorAttr attr) {
        var result = CodeBlock.builder();
        if (attr.getMandatory()) {
            result.addStatement("return getManager().getRepository().get$LManager()." +
                            "getById($L())",
                    catalogueRepository.getEntityManager().getByNameNm(attr.getSubdomainNm().orElseThrow())
                            .getNameNm(), attr.getGetterName());
        } else {
            result.addStatement("return $L().map(id -> getManager().getRepository().get$LManager()." +
                            "getById(id))", attr.getGetterName(),
                    catalogueRepository.getEntityManager().getByNameNm(attr.getSubdomainNm().orElseThrow())
                            .getNameNm());
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
                            .addStatement(getGenProxyRefGetterStatement(attr))
                            .build()
                    );
                }
                result.add(attr.getGetterBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return validateValueObject().$L()", attr.getGetterName())
                        .build()
                );
            }
        }
        return result;
    }

    @Nonnull
    JavaFile generateGenProxy() {
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
                .build();
    }

    @Nonnull
    private MethodSpec getProxyConstructor() {
        return MethodSpec.constructorBuilder()
                .addParameter(managerImplName, "manager")
                .addParameter(BigInteger.class, "id")
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

    @Nonnull
    JavaFile generateProxy() {
        return JavaFile.builder(packageNameImpl,
                TypeSpec.classBuilder(proxyName)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(
                                AnnotationSpec.builder(JsonbTypeAdapter.class)
                                        .addMember("value", "$T.class", jsonbProxyAdapter)
                                        .build()
                        )
                        .addAnnotation(
                                AnnotationSpec.builder(XmlJavaTypeAdapter.class)
                                        .addMember("value", "$T.class", xmlProxyAdapter)
                                        .build()
                        )
                        .superclass(genProxyName)
                        .addSuperinterface(interfaceName)
                        .addMethod(getProxyConstructor())
                        .addMethod(getProxySelf())
                        .addMethod(getProxySelfAsObject())
                        .build())
                .build();
    }

    @Nonnull
    JavaFile generateJsonbProxyAdapter() {
        return  JavaFile.builder(packageNameImpl,
                TypeSpec.classBuilder(jsonbProxyAdapter)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(generatedAnnotation)
                        .addSuperinterface(ParameterizedTypeName.get(ClassName.get(JsonbAdapter.class),
                                proxyName, valueBuilderName))
                        .addField(FieldSpec.builder(Logger.class, "LOG")
                                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                                .initializer("$T.getLogger($T.class)", LogManager.class, jsonbProxyAdapter)
                                .build()
                        )
                        .addMethod(
                                MethodSpec.methodBuilder("adaptToJson")
                                        .addParameter(proxyName, "proxy")
                                        .addModifiers(Modifier.PUBLIC)
                                        .returns(valueBuilderName)
                                        .addAnnotation(Override.class)
                                        .addAnnotation(Nonnull.class)
                                        .addStatement("return new $L(proxy.validateValueObject())", valueBuilderName)
                                        .build()
                        )
                        .addMethod(
                                MethodSpec.methodBuilder("adaptFromJson")
                                        .addParameter(valueBuilderName, "builder")
                                        .addModifiers(Modifier.PUBLIC)
                                        .returns(proxyName)
                                        .addAnnotation(Override.class)
                                        .addAnnotation(Nonnull.class)
                                        .addStatement("throw new $T(LOG, \"Cannot deserialize $LProxy from JSON\")",
                                                InternalException.class, getCProperName())
                                        .build()
                        )
                        .build())
                .build();
    }

    @Nonnull
    JavaFile generateXmlProxyAdapter() {
        return  JavaFile.builder(packageNameImpl,
                TypeSpec.classBuilder(xmlProxyAdapter)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(generatedAnnotation)
                        .superclass(ParameterizedTypeName.get(ClassName.get(XmlAdapter.class),
                                valueBuilderName, proxyName))
                        .addField(FieldSpec.builder(Logger.class, "LOG")
                                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                                .initializer("$T.getLogger($T.class)", LogManager.class, xmlProxyAdapter)
                                .build()
                        )
                        .addMethod(
                                MethodSpec.methodBuilder("marshal")
                                        .addParameter(proxyName, "proxy")
                                        .addModifiers(Modifier.PUBLIC)
                                        .returns(valueBuilderName)
                                        .addAnnotation(Override.class)
                                        .addAnnotation(Nonnull.class)
                                        .addStatement("return new $L(proxy.validateValueObject())", valueBuilderName)
                                        .build()
                        )
                        .addMethod(
                                MethodSpec.methodBuilder("unmarshal")
                                        .addParameter(valueBuilderName, "builder")
                                        .addModifiers(Modifier.PUBLIC)
                                        .returns(proxyName)
                                        .addAnnotation(Override.class)
                                        .addAnnotation(Nonnull.class)
                                        .addStatement("throw new $T(LOG, \"Cannot deserialize $LProxy from JSON\")",
                                                InternalException.class, getCProperName())
                                        .build()
                        )
                        .build())
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
                .addParameter(BigInteger.class, "id");
        for (var attr : cAttrs) {
            ParameterSpec.Builder parameterBuilder = ParameterSpec.builder(attr.getFieldTypeName(), attr.getFieldName());
            if (!attr.getMandatory()) {
                parameterBuilder.addAnnotation(Nullable.class);
            }
            constructorBuilder.addParameter(parameterBuilder.build());
        }
        constructorBuilder.addStatement("super(id" + (hasNmAttr() ? ", nameNm" : "") + ")");
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                if (!attr.getMandatory() || attr.getDomain().getImplementingClass(false).isPrimitive()) {
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
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                result.add(attr.getGetterBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement(attr.getMandatory() ? "return $1L" : "return Optional.ofNullable($1L)",
                                attr.getFieldName())
                        .build()
                );
            }
        }
        return result;
    }

    private void addValueAttrEquality(CodeBlock.Builder eqStatement, GeneratorAttr attr) {
        if (attr.getMandatory()) {
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

    @Nonnull
    JavaFile generateValue() {
        return JavaFile.builder(packageNameImplGen,
                TypeSpec.classBuilder(valueName)
                        .addAnnotation(generatedAnnotation)
                        .addAnnotation(
                                AnnotationSpec.builder(JsonbTypeAdapter.class)
                                        .addMember("value", "$T.class", jsonbValueAdapter)
                                        .build()
                        )
                        .addAnnotation(
                                AnnotationSpec.builder(XmlJavaTypeAdapter.class)
                                        .addMember("value", "$T.class", xmlValueAdapter)
                                        .build()
                        )
                        .addModifiers(Modifier.PUBLIC)
                        .superclass(hasNmAttr() ? ProvysNmObjectValue.class : ProvysObjectValue.class)
                        .addFields(getValueFields())
                        .addMethod(getValueConstructor())
                        .addMethods(getValueGetters())
                        .addMethod(getValueEquals())
                        .build())
                .build();
    }

    @Nonnull
    JavaFile generateJsonbValueAdapter() {
        return  JavaFile.builder(packageNameImpl,
                TypeSpec.classBuilder(jsonbValueAdapter)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(generatedAnnotation)
                        .addSuperinterface(ParameterizedTypeName.get(ClassName.get(JsonbAdapter.class),
                                valueName, valueBuilderName))
                        .addMethod(
                                MethodSpec.methodBuilder("adaptToJson")
                                        .addParameter(valueName, "value")
                                        .addModifiers(Modifier.PUBLIC)
                                        .returns(valueBuilderName)
                                        .addAnnotation(Override.class)
                                        .addAnnotation(Nonnull.class)
                                        .addStatement("return new $L(value)", valueBuilderName)
                                        .build()
                        )
                        .addMethod(
                                MethodSpec.methodBuilder("adaptFromJson")
                                        .addParameter(valueBuilderName, "builder")
                                        .addModifiers(Modifier.PUBLIC)
                                        .returns(valueName)
                                        .addAnnotation(Override.class)
                                        .addAnnotation(Nonnull.class)
                                        .addStatement("return builder.build()")
                                        .build()
                        )
                        .build())
                .build();
    }

    @Nonnull
    JavaFile generateXmlValueAdapter() {
        return  JavaFile.builder(packageNameImpl,
                TypeSpec.classBuilder(xmlValueAdapter)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(generatedAnnotation)
                        .superclass(ParameterizedTypeName.get(ClassName.get(XmlAdapter.class),
                                valueBuilderName, valueName))
                        .addMethod(
                                MethodSpec.methodBuilder("marshal")
                                        .addParameter(valueName, "value")
                                        .addModifiers(Modifier.PUBLIC)
                                        .returns(valueBuilderName)
                                        .addAnnotation(Override.class)
                                        .addAnnotation(Nonnull.class)
                                        .addStatement("return new $L(value)", valueBuilderName)
                                        .build()
                        )
                        .addMethod(
                                MethodSpec.methodBuilder("unmarshal")
                                        .addParameter(valueBuilderName, "builder")
                                        .addModifiers(Modifier.PUBLIC)
                                        .returns(valueName)
                                        .addAnnotation(Override.class)
                                        .addAnnotation(Nonnull.class)
                                        .addStatement("return builder.build()")
                                        .build()
                        )
                        .build())
                .build();
    }

    @Nonnull
    private String getValueBuilderJsonbPropertyOrderValue() {
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
    private AnnotationSpec getValueBuilderJsonbPropertyOrderAnnotation() {
        return AnnotationSpec.builder(JsonbPropertyOrder.class)
                .addMember("value", getValueBuilderJsonbPropertyOrderValue())
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
                if (attr.getMandatory()) {
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
        if (attr.getMandatory()) {
            result.beginControlFlow("if (!this.$L && $L)", attr.getUpdFieldName(), attr.getUpdFieldName())
                    .addStatement("throw new $T(LOG, \"Cannot directly set update flag $L; set value instead\")",
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
                .returns(BigInteger.class)
                .addAnnotation(AnnotationSpec
                        .builder(JsonbProperty.class)
                        .addMember("value", "\"$L_ID\"", getNameNm())
                        .build())
                .addAnnotation(AnnotationSpec
                        .builder(XmlElement.class)
                        .addMember("name", "\"$L_ID\"", getNameNm())
                        .build())
                .addAnnotation(Override.class)
                .addAnnotation(Nullable.class)
                .addStatement("return super.getId()")
                .build());
        result.add(MethodSpec
                .methodBuilder("setId")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(BigInteger.class, "id")
                .returns(valueBuilderName)
                .addAnnotation(AnnotationSpec
                        .builder(JsonbProperty.class)
                        .addMember("value", "\"$L_ID\"", getNameNm())
                        .build())
                .addAnnotation(AnnotationSpec
                        .builder(XmlElement.class)
                        .addMember("name", "\"$L_ID\"", getNameNm())
                        .build())
                .addAnnotation(Override.class)
                .addAnnotation(Nonnull.class)
                .addStatement("return super.setId(id)")
                .build());
        if (hasNmAttr()) {
            // needed as JSON-B 1.0 ignores properties from superclasses in JsonbPropertyOrder annotation
            result.add(MethodSpec
                    .methodBuilder("getNameNm")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(String.class)
                    .addAnnotation(AnnotationSpec
                            .builder(JsonbProperty.class)
                            .addMember("value", "\"NAME_NM\"")
                            .build())
                    .addAnnotation(AnnotationSpec
                            .builder(XmlElement.class)
                            .addMember("name", "\"NAME_NM\"")
                            .build())
                    .addAnnotation(Override.class)
                    .addAnnotation(Nullable.class)
                    .addStatement("return super.getNameNm()")
                    .build());
            result.add(MethodSpec
                    .methodBuilder("setNameNm")
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(String.class, "nameNm")
                    .returns(valueBuilderName)
                    .addAnnotation(AnnotationSpec
                            .builder(JsonbProperty.class)
                            .addMember("value", "$S", "NAME_NM")
                            .build())
                    .addAnnotation(AnnotationSpec
                            .builder(XmlElement.class)
                            .addMember("name", "$S", "NAME_NM")
                            .build())
                    .addAnnotation(Override.class)
                    .addAnnotation(Nonnull.class)
                    .addStatement("return super.setNameNm(nameNm)")
                    .build());
        }
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                result.add(MethodSpec.methodBuilder(attr.getGetterName())
                        .returns(attr.getBuilderFieldTypeName())
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Nullable.class)
                        .addStatement("return $L", attr.getJavaName())
                        .build()
                );
                result.add(MethodSpec.methodBuilder(attr.getSetterName())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(valueBuilderName)
                        .addParameter(attr.getBuilderSetterParam())
                        .addStatement(attr.getMandatory() ? "this.$L = Objects.requireNonNull($L)" : "this.$L = $L",
                                attr.getJavaName(), attr.getJavaName())
                        .addStatement("this.$L = true", attr.getUpdFieldName())
                        .addStatement("return self()")
                        .build()
                );
                result.add(MethodSpec.methodBuilder(attr.getUpdGetterName())
                        .returns(boolean.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(JsonbTransient.class)
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
                .add("return new $T($T.requireNonNull(getId(), \"$L_ID must be specified for build\")\n",
                        valueName, Objects.class, getNameNm());
        for (var attr : cAttrs) {
            if (attr.getMandatory()) {
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
                .add("return \"$LValueBuilder{\" +\n", getCProperName());
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                result.add("        \", $L =", attr.getJavaName());
                if (attr.getDomain().getImplementingClass(true).equals(String.class)) {
                    result.add("'\" + $L + '\\'' +\n", attr.getFieldName());
                } else if (attr.useObjectReference()) {
                    result.add("\" + (($L == null) ? null : $L.getId()) +\n", attr.getFieldName());
                } else {
                    result.add("\" + $L +\n", attr.getFieldName());
                }
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

    @Nonnull
    JavaFile generateValueBuilder() {
        return JavaFile.builder(packageNameImplGen,
                TypeSpec.classBuilder(valueBuilderName)
                        .addAnnotation(generatedAnnotation)
                        .addAnnotation(getValueBuilderJsonbPropertyOrderAnnotation())
                        .addAnnotation(AnnotationSpec
                                .builder(XmlRootElement.class)
                                .addMember("name", '"' + getNameNm() + '"')
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
                        .addField(
                                FieldSpec.builder(Logger.class, "LOG")
                                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                                        .initializer("$T.getLogger($T.class)", LogManager.class, valueBuilderName)
                                        .build())
                        .addFields(getValueBuilderFields())
                        .addMethods(getValueBuilderConstructors())
                        .addMethods(getValueBuilderGetters())
                        .addMethod(getValueBuilderSelf())
                        .addMethod(getValueBuilderCopy())
                        .addMethod(getValueBuilderBuild())
                        .addMethod(getValueBuilderEquals())
                        .addMethod(getValueBuilderToString())
                        .build())
                .build();
    }

    @Nonnull
    JavaFile generateLoaderInterface() {
        return  JavaFile.builder(packageNameImpl,
                TypeSpec.interfaceBuilder(loaderInterfaceName)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(ParameterizedTypeName.get(
                                ClassName.get(hasNmAttr() ? ProvysNmObjectLoader.class : ProvysObjectLoader.class),
                                interfaceName, valueName, proxyName, managerImplName))
                        .build())
                .build();
    }

    @Nonnull
    JavaFile generateLoaderBase() {
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
    private TypeName[] getDbLoaderRecordTypeParams() {
        var result = new TypeName[cAttrs.size() + 1];
        int pos = 0;
        result[pos++] = ClassName.get(BigInteger.class);
        for (var attr : cAttrs) {
            result[pos++] = ClassName.get(attr.getDomain().getImplementingClass(true));
        }
        return result;
    }

    @Nonnull
    private TypeName getDbLoaderRecordType() {
        if (cAttrs.size() >= 22) {
            return ClassName.get("org.jooq", "Record");
        }
        return ParameterizedTypeName.get(
                ClassName.get("org.jooq", "Record" + (cAttrs.size() + 1)),
                getDbLoaderRecordTypeParams());
    }

    @Nonnull
    private FieldSpec getDbContextField() {
        return FieldSpec.builder(
                ClassName.get("com.provys.provysdb", "ProvysDbContext"),
                "dbContext")
                .addAnnotation(Nonnull.class)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
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
                .addParameter((attr == null) ? BigInteger.class : attr.getDomain().getImplementingClass(false),
                        (attr == null) ? "id" : attr.getJavaName())
                .addStatement(
                        "return new $LDbLoadRunner(manager, dbContext, $T.field($S, $T.class).eq($L))",
                        getCProperName(), ClassName.get("org.jooq.impl", "DSL"),
                        (attr == null) ? getNameNm() + "_ID" : attr.getNameNm(),
                        (attr == null) ? BigInteger.class : attr.getDomain().getImplementingClass(true),
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
                        "return new $LDbLoadRunner(manager, dbContext, null)", getCProperName())
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

    @Nonnull
    JavaFile generateDbLoader() {
        return  JavaFile.builder(packageNameDbLoader,
                TypeSpec.classBuilder(dbLoaderName)
                        .addAnnotation(ApplicationScoped.class)
                        .addModifiers(Modifier.PUBLIC)
                        .superclass(ParameterizedTypeName.get(
                                loaderBaseName, getDbLoaderRecordType()))
                        .addField(getDbContextField())
                        .addMethod(MethodSpec.constructorBuilder()
                                .addAnnotation(Inject.class)
                                .addParameter(
                                        ClassName.get("com.provys.provysdb", "ProvysDbContext"),
                                        "dbContext")
                                .addStatement("this.dbContext = $T.requireNonNull(dbContext)", Objects.class)
                                .build())
                        .addMethods(getDbLoaderMethods())
                        .build())
                .build();
    }

    @Nonnull
    private MethodSpec getDbLoadRunnerSelect() {
        var dsl = ClassName.get("org.jooq.impl", "DSL");
        var builder = MethodSpec.methodBuilder("select")
                .addAnnotation(Nonnull.class)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), getDbLoaderRecordType()))
                .addStatement("$T result", ParameterizedTypeName.get(ClassName.get(List.class),
                        getDbLoaderRecordType()))
                .beginControlFlow("try (var dsl = dbContext.createDSL())")
                .addCode("result = dsl.select($T.field($S, $T.class)", dsl, getNameNm() + "_ID", BigInteger.class);
        for (var attr : cAttrs) {
            builder.addCode(", $T.field($S, $T.class)", dsl, attr.getNameNm(),
                    attr.getDomain().getImplementingClass(true));
        }
        return builder.addCode(")\n")
                .addCode("        .from($T.table($S))\n", dsl, getTable()
                        .orElseThrow(() -> new InternalException(LOG, "Table not specified in entity " + getNameNm())))
                .addCode("        .where(condition == null ? $T.noCondition() : condition)\n", dsl)
                .addCode("        .fetch();\n")
                .endControlFlow()
                .addStatement("return result")
                .build();
    }

    @Nonnull
    private CodeBlock getDbLoadRunnerCreateValueObjectBody() {
        var builder = CodeBlock.builder()
                .add("return new $T(getId(sourceObject)", valueName);
        for (var attr : cAttrs) {
            if (attr.useObjectReference()) {
                if (attr.getMandatory()) {
                    builder.add(", getManager().getRepository().get$LManager()." +
                                    "getOrAddById(sourceObject.get($S, $T.class))",
                            catalogueRepository.getEntityManager().getByNameNm(attr.getSubdomainNm().orElseThrow())
                                    .getNameNm(), attr.getNameNm(), BigInteger.class);
                } else {
                    builder.add(", (sourceObject.get($S, $T.class) == null) ? " +
                                    "null : getManager().getRepository().get$LManager()." +
                                    "getOrAddById(sourceObject.get($S, $T.class))",
                            catalogueRepository.getEntityManager().getByNameNm(attr.getSubdomainNm().orElseThrow())
                                    .getNameNm(), attr.getNameNm(), BigInteger.class);
                }
            } else {
                builder.add(", sourceObject.get($S, $T.class)", attr.getNameNm(),
                        attr.getDomain().getImplementingClass(true));
            }
        }
        return builder.add(")").build();

    }

    @Nonnull
    private MethodSpec getDbLoadRunnerCreateValueObject() {
        return MethodSpec.methodBuilder("createValueObject")
                .addAnnotation(Nonnull.class)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PROTECTED)
                .returns(valueName)
                .addParameter(getDbLoaderRecordType(),
                        "sourceObject")
                .addStatement(getDbLoadRunnerCreateValueObjectBody())
                .build();
    }

    @Nonnull
    JavaFile generateDbLoadRunner() {
        return  JavaFile.builder(packageNameDbLoader,
                TypeSpec.classBuilder(dbLoadRunnerName)
                        .superclass(ParameterizedTypeName.get(
                                ClassName.get(ProvysObjectLoadRunner.class),
                                interfaceName, valueName, proxyName, managerImplName,
                                getDbLoaderRecordType()))
                        .addField(getDbContextField())
                        .addField(FieldSpec.builder(ClassName.get("org.jooq", "Condition"),
                                "condition")
                                .addAnnotation(Nullable.class)
                                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                                .build())
                        .addMethod(MethodSpec.constructorBuilder()
                                .addParameter(managerImplName, "manager")
                                .addParameter(
                                        ClassName.get("com.provys.provysdb", "ProvysDbContext"),
                                        "dbContext")
                                .addParameter(ParameterSpec.builder(
                                        ClassName.get("org.jooq", "Condition"),
                                        "condition")
                                        .addAnnotation(Nullable.class)
                                        .build())
                                .addStatement("super(manager)")
                                .addStatement("this.dbContext = $T.requireNonNull(dbContext)", Objects.class)
                                .addStatement("this.condition = condition")
                                .build())
                        .addMethod(getDbLoadRunnerSelect())
                        .addMethod(MethodSpec.methodBuilder("getId")
                                .addAnnotation(Nonnull.class)
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PROTECTED)
                                .returns(BigInteger.class)
                                .addParameter(getDbLoaderRecordType(),"sourceObject")
                                .addStatement("return sourceObject.get($S, $T.class)", getNameNm() + "_ID",
                                        BigInteger.class)
                                .build())
                        .addMethod(getDbLoadRunnerCreateValueObject())
                        .build())
                .build();
    }
}

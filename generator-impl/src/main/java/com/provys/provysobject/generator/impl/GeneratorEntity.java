package com.provys.provysobject.generator.impl;

import com.provys.catalogue.api.*;
import com.provys.provysobject.ProvysNmObject;
import com.provys.provysobject.ProvysObject;
import com.provys.provysobject.impl.*;
import com.squareup.javapoet.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

class GeneratorEntity implements Entity {

    @Nonnull
    private final CatalogueRepository catalogueRepository;
    @Nonnull
    private final Entity entity;
    @Nonnull
    private final Set<String> friendEntities;
    @Nonnull
    private final String packageName;
    @Nonnull
    private final String packageNameApi;
    @Nonnull
    private final String packageNameApiGen;
    @Nonnull
    private final String packageNameImpl;
    @Nonnull
    private final String packageNameImplGen;
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
    private final ClassName valueName;
    @Nonnull
    private final ClassName loaderInterfaceName;
    @Nonnull
    private final ClassName loaderBaseName;
    @Nonnull
    private final List<GeneratorAttr> cAttrs;

    GeneratorEntity(CatalogueRepository catalogueRepository, Entity entity, String module,
                    Collection<String> friendEntities) {
        this.catalogueRepository = Objects.requireNonNull(catalogueRepository);
        this.entity = Objects.requireNonNull(entity);
        this.friendEntities = new HashSet<>(friendEntities);
        this.friendEntities.add(entity.getNameNm()); // we are always friends with ourselves
        this.packageName = "com.provys." + module.toLowerCase();
        this.packageNameApi = packageName + ".api";
        this.packageNameApiGen = packageNameApi + ".gen";
        this.packageNameImpl = packageName + ".impl";
        this.packageNameImplGen = packageNameImpl + ".gen";
        var entityName = getCProperName();
        this.moduleRepositoryName = ClassName.get(packageNameApi,
                Character.toLowerCase(module.charAt(0)) + module.substring(1).toLowerCase());
        this.managerName = ClassName.get(packageNameApi, entityName + "Manager");
        this.managerImplName = ClassName.get(packageNameImpl, entityName + "ManagerImpl");
        this.genInterfaceName =  ClassName.get(packageNameApiGen, "Gen" + entityName);
        this.interfaceName =  ClassName.get(packageNameApi, entityName);
        this.genProxyName = ClassName.get(packageNameImplGen, "Gen" + entityName + "Proxy");
        this.proxyName = ClassName.get(packageNameImpl, entityName + "Proxy");
        this.valueName = ClassName.get(packageNameImplGen, entityName + "Value");
        this.loaderInterfaceName = ClassName.get(packageNameImpl, entityName + "Loader");
        this.loaderBaseName = ClassName.get(packageNameImpl, entityName + "LoaderBase");
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

    @Override
    @Nonnull
    public String getName() {
        return entity.getName();
    }

    @Override
    public boolean isCustom() {
        return entity.isCustom();
    }

    @Override
    public boolean isUsed() {
        return entity.isUsed();
    }

    @Override
    public boolean isObjectClass() {
        return entity.isObjectClass();
    }

    @Override
    @Nonnull
    public Optional<String> getTable() {
        return entity.getTable();
    }

    @Override
    @Nonnull
    public Optional<String> getView() {
        return entity.getView();
    }

    @Override
    @Nonnull
    public Optional<String> getPgPackage() {
        return entity.getPgPackage();
    }

    @Override
    @Nonnull
    public Optional<String> getCpPackage() {
        return entity.getCpPackage();
    }

    @Override
    @Nonnull
    public Optional<String> getEpPackage() {
        return entity.getEpPackage();
    }

    @Override
    @Nonnull
    public Optional<String> getFpPackage() {
        return entity.getFpPackage();
    }

    @Override
    @Nonnull
    public Optional<BigInteger> getAncestorId() {
        return entity.getAncestorId();
    }

    @Override
    @Nonnull
    public Optional<Entity> getAncestor() {
        return entity.getAncestor();
    }

    @Override
    @Nonnull
    public Optional<BigInteger> getEntityGrpId() {
        return entity.getEntityGrpId();
    }

    @Override
    @Nonnull
    public Optional<EntityGrp> getEntityGrp() {
        return entity.getEntityGrp();
    }

    @Override
    @Nonnull
    public Optional<String> getNote() {
        return entity.getNote();
    }

    @Override
    @Nonnull
    public Optional<String> getCustomNote() {
        return entity.getCustomNote();
    }

    @Override
    @Nonnull
    public Optional<String> getStructureDoc() {
        return entity.getStructureDoc();
    }

    @Override
    @Nonnull
    public Optional<String> getUsageDoc() {
        return entity.getUsageDoc();
    }

    @Override
    @Nonnull
    public Optional<String> getBehaviourDoc() {
        return entity.getBehaviourDoc();
    }

    @Override
    @Nonnull
    public Optional<String> getImplDoc() {
        return entity.getImplDoc();
    }

    @Override
    @Nonnull
    public Collection<AttrGrp> getAttrGrps() {
        return entity.getAttrGrps();
    }

    @Override
    @Nonnull
    public Collection<Attr> getAttrs() {
        return entity.getAttrs();
    }

    @Override
    @Nonnull
    public String getNameNm() {
        return entity.getNameNm();
    }

    @Override
    @Nonnull
    public BigInteger getId() {
        return entity.getId();
    }

    @Override
    public int compareTo(Entity o) {
        return entity.compareTo(o);
    }

    @Nonnull
    public String getPackageName() {
        return packageName;
    }

    @Nonnull
    public String getPackageNameApi() {
        return packageNameApi;
    }

    @Nonnull
    public String getPackageNameApiGen() {
        return packageNameApiGen;
    }

    @Nonnull
    public String getPackageNameImpl() {
        return packageNameImpl;
    }

    @Nonnull
    public String getPackageNameImplGen() {
        return packageNameImplGen;
    }

    @Nonnull
    public ClassName getModuleRepositoryName() {
        return moduleRepositoryName;
    }

    @Nonnull
    public ClassName getManagerName() {
        return managerName;
    }

    @Nonnull
    public ClassName getManagerImplName() {
        return managerImplName;
    }

    @Nonnull
    public ClassName getGenInterfaceName() {
        return genInterfaceName;
    }

    @Nonnull
    public ClassName getInterfaceName() {
        return interfaceName;
    }

    @Nonnull
    public ClassName getGenProxyName() {
        return genProxyName;
    }

    @Nonnull
    public ClassName getProxyName() {
        return proxyName;
    }

    @Nonnull
    public ClassName getvalueName() {
        return valueName;
    }

    @Nonnull
    public ClassName getLoaderInterfaceName() {
        return loaderInterfaceName;
    }

    @Nonnull
    public ClassName getLoaderBaseName() {
        return loaderBaseName;
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
                        .addModifiers(Modifier.PUBLIC)
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
                .addModifiers(Modifier.PUBLIC)
                .addParameter(managerImplName, "manager")
                .addParameter(BigInteger.class, "id")
                .addStatement("super(manager, id)")
                .build();
    }

    @Nonnull
    private Collection<MethodSpec> getGenProxyGetters() {
        var result = new ArrayList<MethodSpec>(cAttrs.size() * 2);
        for (var attr : cAttrs) {
            if (!attr.getNameNm().equals("NAME_NM") || !hasNmAttr()) {
                if (attr.useObjectReference()) {
                    result.add(attr.getRefGetterBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override.class)
                            .addStatement("return validateValueObject().$L()", attr.getRefGetterName())
                            .build()
                    );
                }
                result.add(attr.getGetterBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
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
                        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                        .superclass(ParameterizedTypeName.get(
                                hasNmAttr() ? ClassName.get(ProvysNmObjectProxyImpl.class) :
                                        ClassName.get(ProvysObjectProxyImpl.class),
                                interfaceName, valueName, proxyName, managerImplName))
                        .addSuperinterface(genInterfaceName)
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
                        .superclass(genProxyName)
                        .addSuperinterface(interfaceName)
                        .addMethod(getProxyConstructor())
                        .addMethod(getProxySelf())
                        .addMethod(getProxySelfAsObject())
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
                if (attr.useObjectReference()) {
                    result.add(attr.getRefGetterBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override.class)
                            .addStatement(attr.getMandatory() ? "return $L" : "return Optional.ofNullable($L)",
                                    attr.getFieldName())
                            .build()
                    );
                    result.add(attr.getGetterBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override.class)
                            .addStatement(attr.getMandatory() ? "return $L().getId()" :
                                    "return $L().map($T::getId)", attr.getRefGetterName(), ProvysObject.class)
                            .build()
                    );
                } else {
                    result.add(attr.getGetterBuilder()
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override.class)
                            .addStatement(attr.getMandatory() ? "return $1L" : "return Optional.ofNullable($1L)",
                                    attr.getFieldName())
                            .build()
                    );
                }
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
                .addParameter(Object.class, "o")
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
                        .addModifiers(Modifier.PUBLIC)
                        .superclass(hasNmAttr() ? ProvysNmObjectValue.class : ProvysObjectValue.class)
                        .addSuperinterface(genInterfaceName)
                        .addFields(getValueFields())
                        .addMethod(getValueConstructor())
                        .addMethods(getValueGetters())
                        .addMethod(getValueEquals())
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
                        .addTypeVariable(TypeVariableName.get("S"))
                        .superclass(ParameterizedTypeName.get(
                                ClassName.get(hasNmAttr() ? ProvysNmObjectLoaderImpl.class :
                                        ProvysObjectLoaderImpl.class),
                                interfaceName, valueName, proxyName, managerImplName, TypeVariableName.get("S")))
                        .addSuperinterface(loaderInterfaceName)
                        .build())
                .build();
    }
}

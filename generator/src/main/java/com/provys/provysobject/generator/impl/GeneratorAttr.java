package com.provys.provysobject.generator.impl;

import com.provys.catalogue.api.*;
import com.squareup.javapoet.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.Optional;

class GeneratorAttr implements Attr {

    @Nonnull
    private final GeneratorEntity entity;
    @Nonnull
    private final Attr attr;

    GeneratorAttr(GeneratorEntity entity, Attr attr) {
        this.entity = Objects.requireNonNull(entity);
        this.attr = Objects.requireNonNull(attr);
        if (entity.getEntity() != attr.getEntity()) {
            throw new InvalidParameterException("Entity in generator entity and attribute mismatch");
        }
    }

    @Override
    @Nonnull
    public BigInteger getEntityId() {
        return attr.getEntityId();
    }

    @Override
    @Nonnull
    public Entity getEntity() {
        return attr.getEntity();
    }

    @Override
    @Nonnull
    public String getNameNm() {
        return attr.getNameNm();
    }

    @Override
    @Nonnull
    public String getName() {
        return attr.getName();
    }

    @Override
    @Nonnull
    public Optional<String> getProperNameRoot() {
        return attr.getProperNameRoot();
    }

    @Override
    @Nonnull
    public String getJavaName() {
        // temporary... Catalogue is not compilable to fix it at source...
        return Character.toLowerCase(attr.getJavaName().charAt(0)) + attr.getJavaName().substring(1);
    }

    @Override
    @Nonnull
    public Optional<BigInteger> getAttrGrpId() {
        return attr.getAttrGrpId();
    }

    @Override
    public Optional<AttrGrp> getAttrGrp() {
        return attr.getAttrGrp();
    }

    @Override
    public int getOrd() {
        return attr.getOrd();
    }

    @Override
    @Nonnull
    public Optional<String> getNote() {
        return attr.getNote();
    }

    @Override
    @Nonnull
    public AttrType getAttrType() {
        return attr.getAttrType();
    }

    @Override
    @Nonnull
    public Domain getDomain() {
        return attr.getDomain();
    }

    @Override
    @Nonnull
    public Optional<String> getSubdomainNm() {
        return attr.getSubdomainNm();
    }

    @Override
    public boolean getMandatory() {
        return attr.getMandatory();
    }

    @Override
    public Optional<String> getDefValue() {
        return attr.getDefValue();
    }

    @Override
    public int getOrdInEntity() {
        return attr.getOrdInEntity();
    }

    @Override
    @Nonnull
    public BigInteger getId() {
        return attr.getId();
    }

    @Override
    public int compareTo(Attr o) {
        return attr.compareTo(o);
    }

    boolean isKey() {
        return attr.getNameNm().equals(attr.getEntity().getNameNm() + "_ID");
    }

    private static String toInitCap(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    @Nonnull
    String getInitCapJavaName() {
        return toInitCap(getJavaName());
    }

    /**
     * @return true if this attribute is object reference and referenced object is part of group and thus should be
     * referenced as reference to object and not just Id
     */
    boolean useObjectReference() {
        return getDomain().getNameNm().equals("UID")
                && entity.getFriendEntities().contains(getSubdomainNm().orElse(""));
    }

    @Nonnull
    TypeName getFieldTypeName() {
        if (useObjectReference()) {
            return ClassName.get(entity.getPackageNameApi(), entity.getCatalogueRepository().getEntityManager()
                    .getByNameNm(attr.getSubdomainNm().orElseThrow(
                            () -> new RuntimeException("Subdomain not defined in attribute " + getNameNm())))
                    .getNameNm());// should be replaced with getProperName() later
        }
        return TypeName.get(attr.getDomain().getImplementingClass(!attr.getMandatory()));
    }

    @Nonnull
    String getFieldName() {
        if (useObjectReference()) {
            return getJavaName().substring(0, getJavaName().length() - 2);
        }
        return getJavaName();
    }

    @Nonnull
    FieldSpec getFieldSpec() {
        FieldSpec.Builder fieldSpecBuilder = FieldSpec
                .builder(getFieldTypeName(), getFieldName(), Modifier.PRIVATE, Modifier.FINAL);
        if (getMandatory()) {
            if (!getDomain().getImplementingClass(false).isPrimitive()) {
                fieldSpecBuilder.addAnnotation(Nonnull.class);
            }
        } else {
            fieldSpecBuilder.addAnnotation(Nullable.class);
        }
        return fieldSpecBuilder.build();
    }

    @Nonnull
    TypeName getRefGetterReturnType() {
        return getMandatory() ? getFieldTypeName() :
                ParameterizedTypeName.get(ClassName.get(Optional.class), getFieldTypeName());
    }

    @Nonnull
    String getRefGetterName() {
        return "get" + toInitCap(attr.getJavaName()).substring(0, getJavaName().length()-2);
    }

    @Nonnull
    MethodSpec.Builder getRefGetterBuilder() {
        return MethodSpec.methodBuilder(getRefGetterName())
                .returns(getRefGetterReturnType())
                .addAnnotation(Nonnull.class);
    }

    @Nonnull
    TypeName getGetterReturnType() {
        if (attr.getMandatory()) {
            return TypeName.get(getDomain().getImplementingClass(false));
        } else {
            return ParameterizedTypeName.get(Optional.class, getDomain().getImplementingClass(true));
        }
    }

    @Nonnull
    String getGetterName() {
        return (getDomain().getImplementingClass(!attr.getMandatory())==boolean.class ? "is" : "get")
                + toInitCap(attr.getJavaName());
    }

    @Nonnull
    MethodSpec.Builder getGetterBuilder() {
        MethodSpec.Builder getterBuilder = MethodSpec.methodBuilder(getGetterName())
                .returns(getGetterReturnType());
        if (!getMandatory() || !getDomain().getImplementingClass(false).isPrimitive()) {
            getterBuilder.addAnnotation(Nonnull.class);
        }
        return getterBuilder;
    }
}

package com.provys.provysobject.generator.impl;

import com.provys.catalogue.api.*;
import com.squareup.javapoet.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import javax.xml.bind.annotation.XmlElement;
import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.Optional;

class GeneratorAttr implements Comparable<GeneratorAttr> {

    @Nonnull
    private static String toInitCap(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

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

    @Nonnull
    public String getNameNm() {
        return attr.getNameNm();
    }

    @Nonnull
    public String getName() {
        return attr.getName();
    }

    @Nonnull
    public String getJavaName() {
        // temporary... Catalogue is not compilable to fix it at source...
        return attr.getcJavaPropertyName();
    }

    @Nonnull
    public Domain getDomain() {
        return attr.getDomain();
    }

    @Nonnull
    public Optional<String> getSubdomainNm() {
        return attr.getSubdomainNm();
    }

    public boolean isMandatory() {
        return attr.isMandatory();
    }

    boolean isKey() {
        return attr.getNameNm().equals(attr.getEntity().getKeyNm().orElse(null));
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
        return TypeName.get(getDomain().getImplementingClass(!isMandatory()));
    }

    @Nonnull
    String getFieldName() {
        return getJavaName();
    }

    @Nonnull
    FieldSpec getFieldSpec() {
        FieldSpec.Builder fieldSpecBuilder = FieldSpec
                .builder(getFieldTypeName(), getFieldName(), Modifier.PRIVATE, Modifier.FINAL).addAnnotation(AnnotationSpec
                        .builder(XmlElement.class)
                        .addMember("name", '"' + attr.getNameNm() + '"')
                        .build());
        if (isMandatory()) {
            if (!getDomain().getImplementingClass(false).isPrimitive()) {
                fieldSpecBuilder.addAnnotation(Nonnull.class);
            }
        } else {
            fieldSpecBuilder.addAnnotation(Nullable.class);
        }
        return fieldSpecBuilder.build();
    }

    @Nonnull
    TypeName getBuilderFieldTypeName() {
        return TypeName.get(attr.getDomain().getImplementingClass(true));
    }

    @Nonnull
    FieldSpec getBuilderFieldSpec() {
        return FieldSpec.builder(getBuilderFieldTypeName(), getJavaName(), Modifier.PRIVATE)
                .addAnnotation(Nullable.class)
                .build();
    }

    @Nonnull
    ParameterSpec getBuilderSetterParam() {
        var result = ParameterSpec.builder(getBuilderFieldTypeName(), getJavaName());
        if (!isMandatory()) {
            result.addAnnotation(Nullable.class);
        }
        return result.build();
    }

    @Nonnull
    String getUpdFieldName() {
        return "upd" + toInitCap(getJavaName());
    }

    @Nonnull
    FieldSpec getBuilderUpdFieldSpec() {
        return FieldSpec.builder(boolean.class, getUpdFieldName(), Modifier.PRIVATE)
                .initializer("false")
                .build();
    }

    @Nonnull
    TypeName getRefGetterReturnType() {
        var typeName = ClassName.get(entity.getPackageNameApi(), toInitCap(getSubdomainNm().orElseThrow()));
        return isMandatory() ? typeName : ParameterizedTypeName.get(ClassName.get(Optional.class), typeName);
    }

    @Nonnull
    String getRefGetterName() {
        var javaGetterName = attr.getcJavaGetterName();
        return javaGetterName.substring(0, javaGetterName.length()-2);
    }

    @Nonnull
    MethodSpec.Builder getRefGetterBuilder() {
        return MethodSpec.methodBuilder(getRefGetterName())
                .returns(getRefGetterReturnType())
                .addAnnotation(Nonnull.class);
    }

    @Nonnull
    TypeName getGetterReturnType() {
        if (attr.isMandatory()) {
            return TypeName.get(getDomain().getImplementingClass(false));
        } else {
            return ParameterizedTypeName.get(Optional.class, getDomain().getImplementingClass(true));
        }
    }

    @Nonnull
    String getGetterName() {
        return attr.getcJavaGetterName();
    }

    @Nonnull
    MethodSpec.Builder getGetterBuilder() {
        MethodSpec.Builder getterBuilder = MethodSpec.methodBuilder(getGetterName())
                .returns(getGetterReturnType());
        if (!isMandatory() || !getDomain().getImplementingClass(false).isPrimitive()) {
            getterBuilder.addAnnotation(Nonnull.class);
        }
        return getterBuilder;
    }

    @Nonnull
    String getSetterName() {
        return attr.getcJavaSetterName();
    }

    @Nonnull
    String getUpdGetterName() {
        return "isUpd" + toInitCap(attr.getcJavaPropertyName());
    }

    @Nonnull
    String getUpdSetterName() {
        return "setUpd" + toInitCap(attr.getcJavaPropertyName());
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * Uses underlying attr and its comparator for comparison
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(GeneratorAttr o) {
        return attr.compareTo(o.attr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneratorAttr that = (GeneratorAttr) o;
        return entity.equals(that.entity) &&
                attr.equals(that.attr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity, attr);
    }
}

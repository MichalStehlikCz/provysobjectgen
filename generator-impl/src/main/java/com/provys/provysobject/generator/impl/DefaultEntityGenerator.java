package com.provys.provysobject.generator.impl;

import com.provys.catalogue.api.AttrType;
import com.provys.catalogue.api.CatalogueRepository;
import com.provys.provysobject.generator.EntityGenerator;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class DefaultEntityGenerator implements EntityGenerator {

    @Nonnull
    private final CatalogueRepository catalogueRepository;

    @SuppressWarnings("CdiInjectionPointsInspection")
    @Inject
    DefaultEntityGenerator(CatalogueRepository catalogueRepository) {
        this.catalogueRepository = Objects.requireNonNull(catalogueRepository);
    }

    private static String toInitCap(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    @Override
    public String generateInterface(String entityNm) {
        var entity = catalogueRepository.getEntityManager().getByNameNm(entityNm);
        var entityName = Character.toUpperCase(entityNm.charAt(0)) + entityNm.substring(1).toLowerCase();
        var attrs = entity.getAttrs().stream()
                .filter(attr -> (attr.getAttrType() == AttrType.COLUMN))
                .sorted()
                .collect(Collectors.toList());
        var builder = new StringBuilder()
                .append("public interface Gen")
                .append(entityName)
                .append(" extends ProvysObject {\n");
        for (var attr : attrs) {
            builder.append('\n')
                    .append("    /**\n")
                    .append("     * @return ").append(attr.getName()).append(" (attribute ").append(attr.getNameNm())
                    .append(")\n")
                    .append("     */\n");
            if (attr.getMandatory()) {
                builder.append("    @Nonnull\n");
            }
            builder.append("    ");
            if (attr.getMandatory()) {
                builder.append(attr.getDomain().getImplementingClass(true).getSimpleName());
            } else {
                builder.append("Optional<").append(attr.getDomain().getImplementingClass(false).getSimpleName())
                        .append(">");
            }
            builder.append(" get").append(toInitCap(attr.getJavaName())).append("();\n");
        }
        builder.append('}');
        return builder.toString();
    }
}

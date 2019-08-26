package com.provys.provysobject.generator;

public interface EntityGenerator {
    /**
     * Generate interface for accessing instances of given object
     *
     * @param entityNm is internal name of entity class should be based on
     * @return source code for given class
     */
    String generateInterface(String entityNm);
}

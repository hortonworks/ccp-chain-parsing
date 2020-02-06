package com.cloudera.parserchains.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The name of a field contained within a {@link Message}.
 */
public class FieldName {
    private static final Regex validFieldName = Regex.of("[\\w\\d-_. @]+");
    private final String fieldName;

    public static final FieldName of(String fieldName) {
        return new FieldName(fieldName);
    }

    /**
     * Use {@link FieldName#of(String)}.
     */
    private FieldName(String fieldName) {
        if(!validFieldName.matches(fieldName)) {
            throw new IllegalArgumentException(String.format("Invalid field name: '%s'", fieldName));
        }
        this.fieldName = fieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldName that = (FieldName) o;
        return new EqualsBuilder()
                .append(fieldName, that.fieldName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fieldName)
                .toHashCode();
    }

    @Override
    public String toString() {
        return fieldName;
    }
}

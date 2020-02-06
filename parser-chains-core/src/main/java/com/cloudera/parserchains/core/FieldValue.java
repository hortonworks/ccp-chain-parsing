package com.cloudera.parserchains.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The value of a field contained within a {@link Message}.
 */
public class FieldValue {
    // TODO what should be excluded here using this regex?
    private static final Regex validFieldValue = Regex.of(".*");
    private final String value;

    public static FieldValue of(String fieldValue) {
        return new FieldValue(fieldValue);
    }

    /**
     * Instead use {@link FieldValue#of(String)}.
     */
    private FieldValue(String value) {
        if(!validFieldValue.matches(value)) {
            throw new IllegalArgumentException(String.format("Invalid field value: '%s'", value));
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldValue that = (FieldValue) o;
        return new EqualsBuilder()
                .append(value, that.value)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(value)
                .toHashCode();
    }
}

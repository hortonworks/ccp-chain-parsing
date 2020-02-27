package com.cloudera.parserchains.queryservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Objects;

/**
 * The name of a parser.
 */
public class ParserName {
    @JsonValue
    private final String name;

    /**
     * Create a new {@link ParserName}.
     * @param name The name of the parser.
     */
    @JsonCreator
    public static final ParserName of(String name) {
        return new ParserName(name);
    }

    /**
     * Private constructor, use {@link ParserName#of(String)} instead.
     */
    private ParserName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParserName that = (ParserName) o;
        return new EqualsBuilder()
                .append(name, that.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ParserName{" +
                "name='" + name + '\'' +
                '}';
    }
}

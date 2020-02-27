package com.cloudera.parserchains.queryservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Objects;

/**
 * Uniquely identifies a parser.
 */
public class ParserID {
    @JsonValue
    private final String id;

    /**
     * Creates a new {@link ParserID}.
     * @param id The unique identifier for the parser.
     * @return
     */
    @JsonCreator
    public static ParserID of(String id) {
        return new ParserID(id);
    }

    /**
     * Creates a new {@link ParserID} from a Parser class.
     * @param clazz The Parser class.
     * @return
     */
    public static ParserID of(Class<?> clazz) {
        return new ParserID(clazz.getCanonicalName());
    }

    /**
     * Private constructor.  See {@link ParserID#of(String)}.
     */
    private ParserID(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParserID parserID = (ParserID) o;
        return new EqualsBuilder()
                .append(id, parserID.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ParserID{" +
                "id='" + id + '\'' +
                '}';
    }
}
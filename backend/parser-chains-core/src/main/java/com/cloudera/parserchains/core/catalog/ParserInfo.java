package com.cloudera.parserchains.core.catalog;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Objects;

import com.cloudera.parserchains.core.Parser;

/**
 * Describes a {@link Parser} that was discovered using a {@link ParserCatalog}.
 */
public class ParserInfo {
    private final String name;
    private final String description;
    private final Class<Parser> parserClass;

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Private constructor.  See {@link Builder#builder()}.
     */
    private ParserInfo(String name, String description, Class<Parser> parserClass) {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.parserClass = Objects.requireNonNull(parserClass);
    }

    /**
     * Returns the name of the parser.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the parser.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the implementation class of the parser.
     */
    public Class<Parser> getParserClass() {
        return parserClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParserInfo that = (ParserInfo) o;
        return new EqualsBuilder()
                .append(name, that.name)
                .append(description, that.description)
                .append(parserClass, that.parserClass)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(description)
                .append(parserClass)
                .toHashCode();
    }

    public static class Builder {
        private String name;
        private String description;
        private Class<Parser> parserClass;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withParserClass(Class<Parser> parserClass) {
            this.parserClass = parserClass;
            return this;
        }

        public ParserInfo build() {
            return new ParserInfo(name, description, parserClass);
        }
    }
}

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
    private final Class<? extends Parser> parserClass;

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Private constructor.  See {@link Builder#builder()}.
     */
    private ParserInfo(String name, String description, Class<? extends Parser> parserClass) {
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
    public Class<? extends Parser> getParserClass() {
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
        private Class<? extends Parser> parserClass;

        public Builder with(Class<?> clazz) {
            // TODO where should this stuff live; duplicate in ClassIndexPathCatalog
            MessageParser annotation = clazz.getAnnotation(MessageParser.class);
            if(annotation != null && Parser.class.isAssignableFrom(clazz)) {
                // found a parser.  the cast is guaranteed to be safe because of the 'if' condition above
                @SuppressWarnings("unchecked")
                Class<Parser> parserClass = (Class<Parser>) clazz;
                this.name = annotation.name();
                this.description = annotation.description();
                this.parserClass = parserClass;
            }
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder parserClass(Class<? extends Parser> parserClass) {
            this.parserClass = parserClass;
            return this;
        }

        public ParserInfo build() {
            return new ParserInfo(name, description, parserClass);
        }
    }
}

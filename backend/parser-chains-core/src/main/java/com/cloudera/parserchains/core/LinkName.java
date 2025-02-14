package com.cloudera.parserchains.core;

import com.cloudera.parserchains.core.model.define.ParserName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class LinkName {
    private static final Regex validLinkName = Regex.of("^[a-zA-Z_0-9 ,-.:@]{1,120}$");
    private final String linkName;
    private final ParserName parserName;

    // Might it be better to create a proper "Parser" type that captures this metadata and pass it
    // in as an arg? This seems like a LinkedList.
    public static final LinkName of(String linkName, ParserName parserName) {
        return new LinkName(linkName, parserName);
    }

    /**
     * Private constructor. Use {@link LinkName#of(String, ParserName)} instead.
     */
    private LinkName(String linkName, ParserName parserName) {
        if(!validLinkName.matches(linkName)) {
            throw new IllegalArgumentException(String.format("Invalid link name: '%s'", linkName));
        }
        this.linkName = linkName;
        this.parserName = parserName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LinkName that = (LinkName) o;
        return new EqualsBuilder()
                .append(linkName, that.linkName)
                .append(parserName, that.parserName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(linkName)
                .append(parserName)
                .toHashCode();
    }

    public String getLinkName() {
        return linkName;
    }

    public ParserName getParserName() {
        return parserName;
    }

    @Override
    public String toString() {
        return linkName;
    }
}

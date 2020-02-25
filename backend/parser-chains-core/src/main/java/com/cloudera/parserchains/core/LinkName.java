package com.cloudera.parserchains.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class LinkName {
    private static final Regex validLinkName = Regex.of("^[a-zA-Z_0-9 ,-.:@]{1,120}$");
    private final String linkName;

    public static final LinkName of(String LinkName) {
        return new LinkName(LinkName);
    }

    /**
     * Private constructor. Use {@link LinkName#of(String)} instead.
     */
    private LinkName(String linkName) {
        if(!validLinkName.matches(linkName)) {
            throw new IllegalArgumentException(String.format("Invalid link name: '%s'", linkName));
        }
        this.linkName = linkName;
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
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(linkName)
                .toHashCode();
    }

    public String get() {
        return linkName;
    }

    @Override
    public String toString() {
        return linkName;
    }
}

package com.cloudera.parserchains.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * The name of a configuration element used to configure a {@link Parser}.
 */
public class ConfigName {
    private static final Regex validConfigName = Regex.of("[\\w\\d-_.]+");
    private final String name;
    private final boolean isRequired;

    private ConfigName(String name, boolean isRequired) {
        if (!validConfigName.matches(name)) {
            throw new IllegalArgumentException(String.format("Invalid config name: '%s'", name));
        }
        this.name = name;
        this.isRequired = isRequired;
    }

    public static ConfigName of(String name, boolean isRequired) {
        return new ConfigName(name, isRequired);
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public List<ConfigValue> expects() {
        return Arrays.asList(ConfigValue.of("label", ""), ConfigValue.of("index", "0"));
    }

    @Override
    public String toString() {
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

        ConfigName that = (ConfigName) o;
        return new EqualsBuilder()
                .append(isRequired, that.isRequired)
                .append(name, that.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(isRequired)
                .toHashCode();
    }
}


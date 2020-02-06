package com.cloudera.parserchains.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * The value associated with a {@link ConfigName}.
 *
 * <p>Multiple {@link ConfigValue} objects can be associated with a {@link ConfigName}.
 * To distinguish between these the {@link ConfigValue#key} field can be used.
 *
 * <p>For example, the {@link DelimitedTextParser} requires the output fields to be
 * defined.  In this case, two {@link ConfigValue}s are required; one to identify the
 * label of the output field and another for the index.
 */
public class ConfigValue {
    private static final Regex isValidRegex = Regex.of("[\\w\\d\\s-_.,|\\]\\[]*");
    private String key;
    private String value;

    /**
     * Create a {@link ConfigValue} with both a key and value.
     * @param key The key.
     * @param value The value.
     */
    private ConfigValue(String key, String value) {
        if(!isValidRegex.matches(key)) {
            throw new IllegalArgumentException(String.format("Invalid config key: '%s'", key));
        }
        this.key = key;

        if(!isValidRegex.matches(value)) {
            throw new IllegalArgumentException(String.format("Invalid config value: '%s'", value));
        }
        this.value = value;
    }

    /**
     * Create a {@link ConfigValue} with only a value, no key.
     * @param value The value.
     */
    public static ConfigValue of(String value) {
        // the key is not needed, so just use an empty string
        return new ConfigValue(StringUtils.EMPTY, value);
    }

    /**
     * Creates a {@link ConfigValue} with a key and value. Some configuration elements
     * may make use of an additional key in cases where multiple values are required
     * for a given {@link ConfigName}.
     * @param key The key.
     * @param value The value.
     * @return
     */
    public static ConfigValue of(String key, String value) {
        return new ConfigValue(key, value);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ConfigValue{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConfigValue that = (ConfigValue) o;
        return new EqualsBuilder()
                .append(key, that.key)
                .append(value, that.value)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(key)
                .append(value)
                .toHashCode();
    }
}

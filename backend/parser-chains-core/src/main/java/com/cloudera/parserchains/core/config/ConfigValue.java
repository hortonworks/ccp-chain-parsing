package com.cloudera.parserchains.core.config;

import com.cloudera.parserchains.core.Regex;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static com.cloudera.parserchains.core.Validator.mustMatch;

/**
 * The value associated with a {@link ConfigName}.
 *
 * <p>Multiple {@link ConfigValue} objects can be associated with a {@link ConfigName}.
 * To distinguish between these the {@link ConfigKey} can be used.
 *
 * <p>For example, the DelimitedTextParser requires the output fields to be
 * defined.  In this case, two {@link ConfigValue}s are required; one to identify the
 * label of the output field and another for the index.
 */
public class ConfigValue {
    private static final Regex validValue = Regex.of("[\\w\\d\\s-_.,|\\]\\[]*");
    private ConfigKey key;
    private String value;

    /**
     * Create a {@link ConfigValue} with only a value, no key.
     * @param value The value.
     */
    public static ConfigValue of(String value) {
        // the key is not needed, so just use the default key
        return new ConfigValue(ConfigKey.defaultKey(), value);
    }

    /**
     * Creates a {@link ConfigValue} with a key and value.
     * @param key The {@link ConfigKey}.
     * @param value The value.
     */
    public static ConfigValue of(ConfigKey key, String value) {
        return new ConfigValue(key, value);
    }

    /**
     * Private constructor.  See {@link ConfigValue#of(ConfigKey, String)}
     * or {@link ConfigValue#of(String)}.
     */
    private ConfigValue(ConfigKey key, String value) {
        mustMatch(value, validValue, "config value");
        this.key = key;
        this.value = value;
    }

    public ConfigKey getKey() {
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

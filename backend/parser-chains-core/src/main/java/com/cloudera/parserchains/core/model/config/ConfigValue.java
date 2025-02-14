package com.cloudera.parserchains.core.model.config;

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
    private static final Regex validValue = Regex.of("^.{1,200}$");
    private String value;

    /**
     * Creates a {@link ConfigValue} with a key and value.
     * @param value The value.
     */
    public static ConfigValue of(String value) {
        return new ConfigValue(value);
    }

    /**
     * Private constructor.  See {@link ConfigValue#of(String)}.
     */
    private ConfigValue(String value) {
        mustMatch(value, validValue, "config value");
        this.value = value;
    }

    public String get() {
        return value;
    }

    @Override
    public String toString() {
        return "ConfigValue{" +
                "value='" + value + '\'' +
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

package com.cloudera.parserchains.core.config;

import com.cloudera.parserchains.core.Regex;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Map;

/**
 * A {@link ConfigName} is associated with one or more key/value pairs representing
 * the configuration values provided by the user.
 *
 * <p>Each {@link ConfigName} can accept multiple configuration values.  For example,
 * to configure a field to rename using the RenameFieldParser, both a 'from' and 'to'
 * field name is required. To distinguish between these configuration values, the
 * {@link ConfigKey} is used
 *
 * <p>Effectively, a {@link ConfigName} is associated with a Map<{@link ConfigKey}, {@link ConfigValue}>
 * of values. This relationship can be seen in the parser's primary configuration method;
 * {@link com.cloudera.parserchains.core.Parser#configure(ConfigName, Map<ConfigKey, ConfigValue>)}.
 */
public class ConfigKey {
    private static final Regex isValidRegex = Regex.of("[\\w\\d\\s-_.,|\\]\\[]*");
    private final String key;

    /**
     * Create a {@link ConfigKey}.
     * @param key The key.
     */
    public static ConfigKey of(String key) {
        return new ConfigKey(key);
    }

    private ConfigKey(String key) {
        if(!isValidRegex.matches(key)) {
            throw new IllegalArgumentException(String.format("Invalid config key: '%s'", key));
        }
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConfigKey that = (ConfigKey) o;
        return new EqualsBuilder()
                .append(key, that.key)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(key)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ConfigKey{" +
                "key='" + key + '\'' +
                '}';
    }
}

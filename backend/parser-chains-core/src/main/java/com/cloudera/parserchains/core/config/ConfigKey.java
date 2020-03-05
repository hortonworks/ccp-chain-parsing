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

    /**
     * A unique key used to identify the configuration element.
     */
    private final String key;

    /**
     * A brief label that can be shown to the user.
     */
    private final String label;

    /**
     * A description that can be shown to the user.
     */
    private final ConfigDescription description;

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Private constructor.  See {@link #builder()}.
     * @param key A unique key used to identify the configuration element.
     * @param label A brief label that can be shown to the user.
     * @param description A description of the configuration element.
     */
    private ConfigKey(String key, String label, ConfigDescription description) {
        if(!isValidRegex.matches(key)) {
            throw new IllegalArgumentException(String.format("Invalid config key: '%s'", key));
        }
        this.key = key;
        this.label = label;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public ConfigDescription getDescription() {
        return description;
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

    public static class Builder {
        private String key;
        private String label;
        private ConfigDescription description;

        /**
         * @param key A unique key used to identify the configuration element.
         */
        public Builder key(String key) {
            this.key = key;
            return this;
        }

        /**
         * @param label A brief label that can be shown to the user.
         */
        public Builder label(String label) {
            // a label is not required
            this.label = label;
            return this;
        }

        /**
         * @param description A description of the configuration element.
         */
        public Builder description(String description) {
            // a description is not required
            this.description = ConfigDescription.of(description);
            return this;
        }

        public ConfigKey build() {
            return new ConfigKey(key, label, description);
        }
    }
}

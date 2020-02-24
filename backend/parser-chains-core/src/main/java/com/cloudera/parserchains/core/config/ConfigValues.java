package com.cloudera.parserchains.core.config;

import com.cloudera.parserchains.core.Parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * When configuring a {@link Parser}, a {@link ConfigName} is associated with 1 or
 * more configuration values.  This class provides a container for those 1 or
 * more configuration values.
 *
 * <p>See {@link Parser#configure(ConfigName, ConfigValues)}.
 */
public class ConfigValues {
    private final Map<ConfigKey, ConfigValue> values;

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Private constructor. Use {@link ConfigValues#builder()}.
     */
    private ConfigValues(Map<ConfigKey, ConfigValue> values) {
        this.values = values;
    }

    /**
     * Retrieve the value of a configuration parameter.
     * @param label
     * @return
     */
    public Optional<ConfigValue> getValue(ConfigKey label) {
        Optional<ConfigValue> result = Optional.empty();
        if(values.containsKey(label)) {
            result = Optional.of(values.get(label));
        }
        return result;
    }

    public Optional<ConfigValue> getValue() {
        if(values.size() != 1) {
            String error = String.format("Expect only 1 configuration value to exist. Found %s.", values.size());
            throw new IllegalArgumentException(error);
        }
        // return the 1 and only value that exists
        ConfigValue value = values.values().iterator().next();
        return Optional.of(value);
    }

    public static class Builder {
        private Map<ConfigKey, ConfigValue> values;

        public Builder() {
            values = new HashMap<>();
        }

        public Builder withValue(ConfigValue value) {
            if(values.size() != 0) {
                throw new IllegalStateException("Expected only a single, default value.");
            }
            values.put(ConfigKey.defaultKey(), value);
            return this;
        }

        public Builder withValue(String value) {
            return withValue(ConfigValue.of(value));
        }

        public Builder withValue(String key, String value) {
            return withValue(ConfigKey.of(key), ConfigValue.of(value));
        }


        public Builder withValue(ConfigKey label, ConfigValue value) {
            values.put(label, value);
            return this;
        }

        public Builder withValue(ConfigKey label, String value) {
            return withValue(label, ConfigValue.of(value));
        }

        public ConfigValues build() {
            return new ConfigValues(values);
        }
    }
}

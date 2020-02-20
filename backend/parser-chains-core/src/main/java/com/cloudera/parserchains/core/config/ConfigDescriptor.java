package com.cloudera.parserchains.core.config;

import com.cloudera.parserchains.core.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Fully describes a parameter that is used to configure a {@link Parser}.
 *
 * <p>A parser author exposes the configuration parameters available for
 * their parser using this class along with the {@link Parser#validConfigurations()}
 * method.
 */
public class ConfigDescriptor {
    private final ConfigName name;
    private final ConfigDescription description;
    private final boolean isRequired;
    private final Map<ConfigKey, ConfigDescription> requiredValues;

    /**
     * A builder that can be used to construct Creates a {@link ConfigDescriptor}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Private constructor. See ConfigDescriptor{@link #builder()}.
     */
    private ConfigDescriptor(ConfigName name,
                             ConfigDescription description,
                             boolean isRequired,
                             Map<ConfigKey, ConfigDescription> requiredValues) {
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.isRequired = isRequired;
        this.requiredValues = Objects.requireNonNull(requiredValues);
    }

    /**
     * @return The name of the configuration parameter.
     */
    public ConfigName getName() {
        return name;
    }

    /**
     * @return A description of the configuration parameter.
     */
    public ConfigDescription getDescription() {
        return description;
    }

    /**
     * @return If the configuration parameter is required or not.
     */
    public boolean isRequired() {
        return isRequired;
    }

    /**
     * @return The values required by this configuration parameter.
     */
    public Map<ConfigKey, ConfigDescription> getRequiredValues() {
        return requiredValues;
    }

    public static class Builder {
        private ConfigName name;
        private ConfigDescription description;
        private boolean isRequired;
        private Map<ConfigKey, ConfigDescription> requiredValues;

        public Builder() {
            this.requiredValues = new HashMap<>();
        }

        public Builder name(ConfigName name) {
            this.name = Objects.requireNonNull(name);
            return this;
        }

        public Builder name(String name) {
            return name(ConfigName.of(name));
        }

        public Builder description(ConfigDescription description) {
            this.description = Objects.requireNonNull(description);
            return this;
        }

        public Builder description(String description) {
            return description(ConfigDescription.of(description));
        }

        public Builder isRequired(boolean isRequired) {
            this.isRequired = isRequired;
            return this;
        }

        public Builder requiresValue(ConfigKey key, ConfigDescription description) {
            this.requiredValues.put(key, description);
            return this;
        }

        public Builder requiresValue(String key, String description) {
            return requiresValue(ConfigKey.of(key), ConfigDescription.of(description));
        }

        public ConfigDescriptor build() {
            if(requiredValues.size() == 0) {
                // if no required values defined, must use the default
                requiredValues.put(ConfigKey.defaultKey(), ConfigDescription.of("default"));
            }
            return new ConfigDescriptor(name, description, isRequired, requiredValues);
        }
    }
}

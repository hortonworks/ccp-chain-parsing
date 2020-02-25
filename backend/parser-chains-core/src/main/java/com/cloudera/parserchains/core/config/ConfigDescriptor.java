package com.cloudera.parserchains.core.config;

import com.cloudera.parserchains.core.Parser;

import java.util.Collections;
import java.util.HashMap;
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
    private final Map<ConfigKey, ConfigDescription> acceptedValues;

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
                             Map<ConfigKey, ConfigDescription> acceptedValues) {
        this.name = Objects.requireNonNull(name, "Name is required.");
        this.description = Objects.requireNonNull(description, "Description is required.");
        this.isRequired = isRequired;
        this.acceptedValues = Objects.requireNonNull(acceptedValues, "Values are required");
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
    public Map<ConfigKey, ConfigDescription> getAcceptedValues() {
        return Collections.unmodifiableMap(acceptedValues);
    }

    public static class Builder {
        private ConfigName name;
        private ConfigDescription description;
        private boolean isRequired;
        private Map<ConfigKey, ConfigDescription> acceptedValues;

        public Builder() {
            this.acceptedValues = new HashMap<>();
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

        public Builder acceptsValue(ConfigKey key, ConfigDescription description) {
            this.acceptedValues.put(key, description);
            return this;
        }

        public Builder acceptsValue(String key, String description) {
            return acceptsValue(ConfigKey.of(key), ConfigDescription.of(description));
        }

        public Builder acceptsValue(ConfigKey key, String description) {
            return acceptsValue(key, ConfigDescription.of(description));
        }

        public ConfigDescriptor build() {
            if(acceptedValues.size() == 0) {
                throw new IllegalArgumentException("Must define at least 1 required value.");
            }
            return new ConfigDescriptor(name, description, isRequired, acceptedValues);
        }
    }
}
